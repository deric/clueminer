/*
 * Copyright (C) 2011-2019 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.meta.engine;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.clueminer.exec.ClusteringExecutorCached;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.clustering.api.Configurator;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.Rank;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.external.NMIsqrt;
import org.clueminer.evolution.BaseEvolution;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionListener;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.hac.SimpleIndividual;
import org.clueminer.math.Standardisation;
import org.clueminer.math.StandardisationFactory;
import org.clueminer.utils.Props;
import org.clueminer.utils.ServiceFactory;
import org.clueminer.utils.StopWatch;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exhaustive algorithm configurations search
 *
 * @author deric
 * @param <I>
 * @param <E>
 * @param <C>
 */
public abstract class AbsMetaExp<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>> extends BaseEvolution<I, E, C>
        implements Runnable, Evolution<I, E, C>, Lookup.Provider, Callable<List<Clustering<E, C>>> {

    private static final Logger LOG = LoggerFactory.getLogger(Explore.class);

    protected int gen;
    protected List<ClusterLinkage> linkage;
    protected List<CutoffStrategy> cutoff;
    protected int cnt;
    protected int ndRepeat = 5;
    protected int clusteringsEvaluated;
    protected int clusteringsRejected;
    protected int clusteringsFailed;
    protected int clusteringsTimeouted;
    protected int numResults = -1;
    protected Random rand;
    protected ObjectOpenHashSet<String> blacklist;
    protected int maxRetries = 5;
    protected int execPool = 5;
    //maximum number of explored states, use -1 to use unlimited search
    protected int maxStates = -1;
    protected int maxPerAlg = -1;
    protected List<Clustering<E, C>> results;
    protected NMIsqrt cmp;
    protected static final DecimalFormat df = new DecimalFormat("#,##0.00");
    protected int jobs;
    //in miliseconds
    protected long timePerTask = 1000;
    protected ExecutorService pool;
    protected volatile boolean producerRunning = true;
    protected boolean modifyStd = true;
    protected Rank<E, C> raking;
    protected BlockingQueue<ClusteringTask<E, C>> taskQueue;

    public AbsMetaExp() {
        super();
        cmp = new NMIsqrt();
    }

    public void configure(Props p) {
        numResults = p.getInt("results", 100);
        maxStates = p.getInt("max-states", 200);
    }

    @Override
    public void run() {
        try {
            results = call();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public abstract void prepare();

    protected void algorithmInit() {
        clusteringsTimeouted = 0;
        clusteringsEvaluated = 0;
        clusteringsRejected = 0;
        clusteringsFailed = 0;
        jobs = 0;
        blacklist = new ObjectOpenHashSet<>(maxStates > 0 ? maxStates : 200);

        if (maxStates < 0) {
            maxStates = countClusteringJobs();
        }
        LOG.debug("max states updated: {}", maxStates);

        results = new ArrayList<>(maxStates);
    }

    @Override
    public List<Clustering<E, C>> call() throws Exception {
        StopWatch st = new StopWatch();
        CountDownLatch countDownLatch = new CountDownLatch(execPool + 1);
        evolutionStarted(this);
        super.prepare();
        prepare();
        algorithmInit();
        LOG.info("max states: {}, time limit per task: {}", maxStates, timePerTask);

        taskQueue = new LinkedBlockingQueue<>(maxStates);

        //creating the ThreadPoolExecutor
        //pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(execPool + 2);
        pool = Executors.newWorkStealingPool(execPool + 2);
        LOG.info("starting pool with {} threads", (execPool + 2));
        //start the monitoring thread, with 5s interval

        //a "producer" thread
        pool.submit(() -> {
            // a to-do list
            explore(dataset, taskQueue);
            LOG.info("created {} jobs", taskQueue.size());
            producerRunning = false;
            countDownLatch.countDown();
            LOG.info("producer finished,pool size: {}", countDownLatch.getCount());
        });

        runClusterings(taskQueue, countDownLatch);

        pool.shutdown();
        finish(st, countDownLatch);

        return results;
    }

    protected void finish(StopWatch st, CountDownLatch cdl) throws InterruptedException {
        LOG.debug("countdown: {}", cdl.getCount());
        //cdl.await();
        //LOG.debug("countdown finished: {}", cdl.getCount());
        super.finish();

        double acceptRate = (1.0 - (clusteringsRejected / (double) clusteringsEvaluated)) * 100;

        st.endMeasure();
        LOG.info("total time {}s, evaluated {} clusterings, rejected {} clusterings, failed: {}, timeouted: {}",
                st.timeInSec(), clusteringsEvaluated, clusteringsRejected, clusteringsFailed, clusteringsTimeouted);

        LOG.info("acceptance rate: {}%", df.format(acceptRate));
        printStats(results);
    }

    protected void runClusterings(BlockingQueue<ClusteringTask<E, C>> queue, CountDownLatch cdl) throws InterruptedException {
        LOG.info("Search workunits: {}", maxStates);
        if (ph != null) {
            ph.start(maxStates);
        }

        //watch running jobs with preconfigured timeout
        for (int i = 0; i < execPool; i++) {
            pool.submit(() -> {
                Clustering<E, C> c;
                ClusteringTask<E, C> task = null;
                Future<Clustering<E, C>> future;
                Executor exec = new ClusteringExecutorCached();
                if (cg != null) {
                    exec.setColorGenerator(cg);
                }

                try {
                    if (queue.isEmpty()) {
                        LOG.debug("waiting for work. producer: {}, queue: {}", producerRunning, queue.size());
                        Thread.sleep(500L);
                        LOG.debug("just woke up, queue: {}", queue.size());
                    }
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }

                boolean run = true;

                while (run) {
                    try {

                        if (maxStates > 0 && cnt >= maxStates) {
                            LOG.info("exhaused search limit {}. Stopping meta search.", maxStates);
                            return;
                        }
                        LOG.debug("queue size: {}", queue.size());
                        //make sure worker won't wait on empty queue
                        //task = queue.poll(500L, TimeUnit.MILLISECONDS);
                        task = queue.take();
                        LOG.debug("worker given work");

                        if (task != null) {
                            task.setExecutor(exec);
                            future = pool.submit(task);
                            LOG.debug("running {} with time limit: {}ms", task.getAlgName(), task.getTimeLimit());
                            c = future.get(task.getTimeLimit(), TimeUnit.MILLISECONDS);
                            processResult(exec, c, results, queue);
                        }
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                        //terminate current thread
                        clusteringsFailed++;
                        Thread.currentThread().interrupt();
                    } catch (ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                        clusteringsFailed++;
                    } catch (TimeoutException ex) {
                        clusteringsTimeouted++;
                        if (task != null) {
                            LOG.debug("alg {} reached {}ms timeout", task.getAlgName(), task.getTimeLimit());
                            LOG.debug("alg config: {}", task.getConf().toString());
                        } else {
                            LOG.debug("running task reached timeout");
                        }
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                        clusteringsFailed++;
                    }
                    LOG.debug("finished clustering #{}", cnt);
                    cnt++;

                    if (!producerRunning && queue.isEmpty()) {
                        run = false;
                    }

                    if (cnt > maxStates) {
                        run = false;
                    }

                }
                cdl.countDown();
                LOG.info("worker terminating, current pool size: {}", cdl.getCount());

            });
        }

        long timeLimit;
        if (!producerRunning) {
            timeLimit = queue.size() * timePerTask / execPool;
        } else {
            timeLimit = maxStates * timePerTask / execPool;
        }

        LOG.info("Waiting for pool to finish for: {} ms", timeLimit);
        pool.awaitTermination(timeLimit, TimeUnit.MILLISECONDS);
    }

    private void processResult(Executor exec, Clustering<E, C> c, List<Clustering<E, C>> res, BlockingQueue<ClusteringTask<E, C>> queue) {
        LOG.info("processing clustering {}, queue: {}", c.fingerprint(), queue.size());
        if (isValid(c)) {
            c.setId(gen++);
            clusteringsEvaluated++;
            res.add(c);
            clusteringFound(exec, c);
            fireResult(res);
        } else {
            clusteringsRejected++;
        }

        if (ph != null) {
            ph.progress(cnt);
        }
    }

    /**
     * Called when new valid paritioning is found
     *
     * @param exec
     * @param c
     */
    public abstract void clusteringFound(Executor exec, Clustering<E, C> c);

    public abstract SortedMap<Double, Clustering<E, C>> computeRanking();

    protected abstract void fireResult(List<Clustering<E, C>> res);

    /**
     * Iterates over various algorithm configuration
     *
     * @param dataset
     * @param queue
     */
    public abstract void explore(Dataset<E> dataset, BlockingQueue<ClusteringTask<E, C>> queue);

    protected int countMaxPerAlg(int numAlgs) {
        int perAlg;
        if (maxStates > 0) {
            perAlg = maxStates / numAlgs;
        } else {
            if (maxPerAlg > 0) {
                perAlg = maxPerAlg;
            } else {
                perAlg = 20;
            }
        }
        return perAlg;
    }

    protected void createTasks(Dataset<E> dataset, ClusteringAlgorithm alg,
            Props conf, long timeLimit, BlockingQueue<ClusteringTask<E, C>> queue) {
        int repeat = 1;
        if (!alg.isDeterministic()) {
            //repeat non-deterministic algorithms
            repeat = ndRepeat;
        }
        blacklist.add(conf.toJson());
        for (int j = 0; j < repeat; j++) {
            ClusteringTask<E, C> clb = new ClusteringTask(dataset, conf, timeLimit);
            LOG.debug("created task {} with time limit: {}s", conf.toString(), timeLimit / 1000.0);
            if (jobs < maxStates) {
                jobs++;

                try {
                    queue.put(clb);
                } catch (InterruptedException ex) {
                    LOG.warn("failed to insert into queue", ex);
                    Exceptions.printStackTrace(ex);
                }
                LOG.debug("adding to queue: {}", conf.toString());
                //queue.notify();
            }
        }
    }

    protected boolean isValid(Clustering<E, C> c) {
        if (c == null || c.size() < 2) {
            if (c != null) {
                LOG.debug("rejecting invalid clustering with single cluster, params: {}", c.getParams());
            }
            return false;
        }
        Dataset<E> d = c.getLookup().lookup(Dataset.class
        );
        if (c.instancesCount() != d.size()) {
            LOG.debug("rejecting incomplete clustering {}, params: {}", c.fingerprint(), c.getParams());
            return false;
        }
        int maxK = (int) Math.sqrt(d.size());
        if (c.size() > maxK) {
            LOG.debug("rejecting with too many clusters {}, params: {}", c.size(), c.getParams());
            return false;
        }
        return true;
    }

    /**
     * Random exploration strategy
     *
     * @param base
     * @param queue job queue
     */
    protected void expand(Props base, BlockingQueue<ClusteringTask<E, C>> queue) {
        if (queue.size() > maxStates) {
            LOG.debug("reached maximum states: {}", maxStates);
            return;
        }
        ClusteringAlgorithm alg;
        Props props = null;
        Parameter[] params;
        alg = parseAlgorithm(base);
        params = alg.getParameters();
        LOG.debug("algorithm: {}, {} parameters", alg.getName(), params.length);
        boolean uniqueConfig = false;
        int attempt = 0;
        while (!uniqueConfig && attempt < maxRetries) {
            try {
                int pid = rand.nextInt(params.length);
                Parameter p = params[pid];
                props = base.copy();

                LOG.info("param {}", p.getName());
                switch (p.getType()) {
                    case INTEGER:
                        props.putInt(p.getName(), randomInt((int) p.getMin(), (int) p.getMax()));
                        break;
                    case DOUBLE:
                        props.putDouble(p.getName(), randomDouble(p.getMin(), p.getMax()));
                        break;
                    case STRING:
                        if (p.hasFactory()) {
                            ServiceFactory f = p.getFactory();
                            String[] vals = f.getProvidersArray();
                            if (vals.length > 0) {
                                props.put(p.getName(), vals[rand.nextInt(vals.length)]);
                            } else {
                                LOG.error("missing providers for {}", p.getName());
                            }
                        } else {
                            LOG.error("missing factory for parameter {}", p.getName());
                        }
                        break;
                    case BOOLEAN:
                        props.put(p.getName(), rand.nextBoolean());
                        break;
                    default:
                        throw new RuntimeException(p.getType() + " is not supported yet (param: " + p.getName() + ")");
                }

                if (!isBlacklisted(props)) {
                    LOG.debug("setting {} to {}", p.getName(), props.get(p.getName()));
                    uniqueConfig = true;
                }
                attempt++;
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        if (!uniqueConfig) {
            LOG.warn("failed to find an unique config for {}", alg.getName());
        }
        if (props != null) {
            Configurator conf = alg.getConfigurator();
            //in relative units, give some grace period
            long time = (long) (conf.estimateRunTime(dataset, props));
            createTasks(dataset, alg, props, time, queue);
        } else {
            LOG.error("missing props!");
        }
    }

    protected double randomDouble(double min, double max) {
        return min + (max - min) * rand.nextDouble();
    }

    protected int randomInt(int min, int max) {
        if (max > min) {
            return rand.nextInt((max - min) + 1) + min;
        } else {
            LOG.warn("Invalid bounds for random generator, min: {}, max: {}", min, max);
            return min;
        }
    }

    protected boolean isBlacklisted(Props props) {
        return blacklist.contains(props.toJson());
    }

    protected ClusteringAlgorithm parseAlgorithm(Props params) {
        String alg = params.get(AlgParams.ALG);
        ClusteringFactory cf = ClusteringFactory.getInstance();
        if (alg == null || !cf.hasProvider(alg)) {
            LOG.warn("Missing algorithm identifier. params given: {}", params.toString());
            ClusteringAlgorithm[] algorithms = ClusteringFactory.getInstance().getAllArray();
            int idx = randomInt(0, algorithms.length - 1);
            LOG.info("Using algorithm {} instead", algorithms[idx].getName());
            params.put(AlgParams.ALG, algorithms[idx].getName());
            return algorithms[idx];
        }
        return cf.getProvider(alg);
    }

    protected void printStats(List<Clustering<E, C>> res) {
        double score = 0.0, s1 = 0, s3 = 0, s5 = 0.0, s10 = 0.0, s;
        double sTop = 0.0;
        Iterator<Clustering<E, C>> it = res.iterator();
        Clustering<E, C> c;
        EvaluationTable et;
        int n = 0;
        while (it.hasNext()) {
            c = it.next();
            if (c != null) {
                et = c.getEvaluationTable();
                if (et != null) {
                    s = et.getScore("NMI-sqrt");
                    if (n == 0) {
                        s1 = s;
                    }
                    if (n < 3) {
                        s3 += s;
                    }
                    if (n < 5) {
                        s5 += s;
                    }
                    if (n < 10) {
                        s10 += s;
                    }
                    score += s;
                    if (n < numResults) {
                        sTop += s;
                    }
                    n++;
                }
            }
        }
        if (numResults >= 3) {
            s3 /= 3;
        } else {
            s3 = Double.NaN;
        }
        if (numResults >= 5) {
            s5 /= 5;
        } else {
            s5 = Double.NaN;
        }
        if (numResults >= 10) {
            s10 /= 10;
        } else {
            s10 = Double.NaN;
        }

        if (n >= numResults) {
            sTop /= numResults;
        } else {
            sTop = Double.NaN;
        }
        score /= n;

        LOG.info("top score: {}, (top3: {}, top5: {}, top10: {}, top: {})",
                numFormat(s1), numFormat(s3), numFormat(s5), numFormat(s10), numFormat(sTop));
        LOG.info("avg score in whole population ({}): {}", n, numFormat(score));
    }

    private String numFormat(double d) {
        if (Double.isNaN(d)) {
            return "n/a";
        }
        return df.format(d);
    }

    /**
     * Count number of clustering runs for single meta-search.
     *
     * @return
     */
    protected int countClusteringJobs() {
        ClusteringFactory cf = ClusteringFactory.getInstance();
        int total = 0;
        List<ClusteringAlgorithm> algs = cf.getAll();
        int stdSize = 1;
        if (modifyStd) {
            StandardisationFactory sf = StandardisationFactory.getInstance();
            List<Standardisation> stds = sf.getAll();
            stdSize = stds.size();
        }
        int perAlg = countMaxPerAlg(algs.size());
        for (ClusteringAlgorithm alg : algs) {
            if (alg.isDeterministic()) {
                total += perAlg * stdSize;
            } else {
                total += ndRepeat * perAlg * stdSize;
            }
        }
        return total;
    }

    /**
     * Set how many times should be non-deterministic algorithms repeated.
     *
     * @param repeat
     */
    public void setNumNdRepeat(int repeat) {
        this.ndRepeat = repeat;
    }

    @Override
    public I createIndividual() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public void setMaxPerAlg(int maxPerAlg) {
        this.maxPerAlg = maxPerAlg;
    }

    public void setMaxSolutions(int maxSolutions) {
        this.maxStates = maxSolutions;
    }

    protected String printAlg(Map<ClusteringAlgorithm, Double> estTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int i = 0;
        for (Entry<ClusteringAlgorithm, Double> alg : estTime.entrySet()) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(alg.getKey().getName()).append(":").append(alg.getValue());
            i++;
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * Set time per clustering in miliseconds
     *
     * @param limit
     */
    public void setTimePerTask(long limit) {
        this.timePerTask = limit;
    }

    public void setExecPool(int size) {
        this.execPool = size;
    }

    public void setModifyStd(boolean b) {
        this.modifyStd = b;
    }

}
