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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;
import org.clueminer.exec.ClusteringExecutorCached;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.external.NMIsqrt;
import org.clueminer.evolution.BaseEvolution;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionListener;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.hac.SimpleIndividual;
import org.clueminer.meta.api.CostFunction;
import org.clueminer.meta.api.CostFunctionFactory;
import org.clueminer.meta.api.CostMeasure;
import org.clueminer.meta.api.DataStats;
import org.clueminer.meta.api.DataStatsFactory;
import org.clueminer.meta.api.MetaStorage;
import org.clueminer.utils.MapUtils;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import org.clueminer.utils.ServiceFactory;
import org.clueminer.utils.StopWatch;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use meta-features to select suitable (optimal) clustering algorithm.
 *
 * @author deric
 * @param <I>
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = Evolution.class)
public class Explore<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>> extends BaseEvolution<I, E, C>
        implements Runnable, Evolution<I, E, C>, Lookup.Provider, Callable<List<Clustering<E, C>>> {

    private static final String NAME = "Explore";
    private static final Logger LOG = LoggerFactory.getLogger(MetaSearch.class);

    protected final Executor exec;
    protected int gen;
    protected List<ClusterLinkage> linkage;
    protected List<CutoffStrategy> cutoff;
    protected int cnt;
    private HashMap<String, Double> meta;
    private int ndRepeat = 5;
    private double clusteringTime;
    private int clusteringsEvaluated;
    private int clusteringsRejected;
    private ArrayList<I> bestIndividuals;
    private int numResults = -1;
    private boolean useMetaDB = false;
    private Random rand;
    private ObjectOpenHashSet<String> blacklist;
    private int maxRetries = 5;
    //maximum number of explored states, use -1 to use unlimited search
    private int maxStates = -1;
    private int maxPerAlg = -1;
    private List<Clustering<E, C>> results;
    private NMIsqrt cmp;
    private static final DecimalFormat df = new DecimalFormat("#,##0.00");

    public Explore() {
        super();
        exec = new ClusteringExecutorCached();
        cmp = new NMIsqrt();
    }

    public void configure(Props p) {
        numResults = p.getInt("results", 100);
        useMetaDB = p.getBoolean("use-metadb", false);
        maxStates = p.getInt("max-states", 200);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void run() {
        try {
            results = call();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private HashMap<String, Double> computeMeta(Dataset<E> data, Props config) {
        DataStatsFactory dsf = DataStatsFactory.getInstance();
        HashMap<String, Double> res = new HashMap<>();
        for (DataStats<E> ds : dsf.getAll()) {
            ds.computeAll(data, res, config);
        }
        return res;
    }

    /**
     * Iterates over various algorithm configuration
     *
     * @param dataset
     * @param queue
     */
    private void explore(List<Clustering<E, C>> res, Dataset<E> dataset) {
        ClusteringFactory cf = ClusteringFactory.getInstance();
        List<ClusteringAlgorithm> algs = cf.getAll();
        Props conf;
        Map<ClusteringAlgorithm, Double> estTime = new HashMap<>();
        double time;
        CostFunctionFactory cff = CostFunctionFactory.getInstance();
        CostFunction costFunc = cff.getDefault();
        //estimate computing time for each algorithm
        for (ClusteringAlgorithm alg : algs) {
            time = -1;
            if (useMetaDB) {
                time = costFunc.estimate(alg.getName(), CostMeasure.TIME, meta);
            }
            //in case that we're not using meta db, or estimation failed
            if (time < 0) {
                time = alg.getConfigurator().estimateRunTime(dataset, config);
            }
            estTime.put(alg, time);
        }
        Map<ClusteringAlgorithm, Double> sortedMap = MapUtils.sortByValue(estTime);

        LOG.info("available algorithms: {}", printAlg(sortedMap));
        int perAlg = countMaxPerAlg(algs.size());

        for (Entry<ClusteringAlgorithm, Double> alg : sortedMap.entrySet()) {
            conf = getConfig().copy(PropType.PERFORMANCE, PropType.VISUAL);
            conf.put(AlgParams.ALG, alg.getKey().getName());
            LOG.debug("expanding #{} configs of alg: {}", perAlg, alg.getKey().getName());
            for (int i = 0; i < perAlg; i++) {
                expand(res, conf);
                //execute(res, dataset, alg.getKey(), conf);
            }
        }
    }

    private int countMaxPerAlg(int numAlgs) {
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

    private void execute(List<Clustering<E, C>> res, Dataset<E> dataset, ClusteringAlgorithm alg, Props conf) {
        int repeat = 1;
        int i = 0;
        if (!alg.isDeterministic()) {
            //repeat non-deterministic algorithms
            repeat = ndRepeat;
        }
        do {
            if (maxStates > 0 && cnt >= maxStates) {
                LOG.info("exhaused search limit {}. Stopping meta search.", maxStates);
                if (ph != null) {
                    ph.finish();
                }
                return;
            }

            Clustering<E, C> c = cluster(dataset, conf);
            cleanUp(c);
            cnt++;
            if (isValid(c)) {
                c.setId(gen++);
                res.add(c);
                fireResult(res);
            } else {
                clusteringsRejected++;
            }

            if (ph != null) {
                ph.progress(cnt);
            }
            i++;
        } while (i < repeat);
    }

    /**
     * Remove empty clusters
     *
     * @param clustering
     * @return
     */
    private Clustering<E, C> cleanUp(Clustering<E, C> clustering) {
        for (int i = 0; i < clustering.size(); i++) {
            Cluster<E> c = clustering.get(i);
            if (c.isEmpty()) {
                clustering.remove(i);
            }
        }
        return clustering;
    }

    private Clustering<E, C> cluster(Dataset<E> dataset, Props conf) {
        StopWatch time = new StopWatch(true);
        Clustering<E, C> c = exec.clusterRows(dataset, conf);
        time.endMeasure();
        blacklist.add(c.getParams().toJson());
        clusteringTime += time.timeInSec();
        clusteringsEvaluated++;
        c.lookupAdd(time);
        return c;
    }

    private boolean isValid(Clustering<E, C> c) {
        if (c == null || c.size() < 2) {
            if (c != null) {
                LOG.debug("rejecting invalid clustering with single cluster, params: {}", c.getParams());
            }
            return false;
        }
        Dataset<E> d = c.getLookup().lookup(Dataset.class);
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

    @Override
    public List<Clustering<E, C>> call() throws Exception {
        rand = new Random();
        clusteringTime = 0.0;
        clusteringsEvaluated = 0;
        clusteringsRejected = 0;
        int workunits = 0;

        LOG.info("Starting {}", getName());

        evolutionStarted(this);
        prepare();

        bestIndividuals = new ArrayList<>(numResults > 0 ? numResults : 200);

        if (cg != null) {
            exec.setColorGenerator(cg);
        }

        blacklist = new ObjectOpenHashSet<>(maxStates > 0 ? maxStates : 200);

        if (ph != null) {

            if (maxStates > -1) {
                workunits = maxStates;
            } else {
                workunits = countClusteringJobs();
            }
            LOG.info("search workunits: {}", workunits);
            ph.start(workunits);
        }

        if (!config.containsKey(AlgParams.STD)) {
            config.put(AlgParams.STD, "z-score");
        }
        //config.putInt("k", 5);
        Dataset<E> data = standartize(config);
        meta = computeMeta(data, config);
        LOG.info("got {} meta parameters", meta.size());
        List<Clustering<E, C>> res = new ArrayList<>(workunits);
        cnt = 0;
        MetaStorage storage = null;

        if (useMetaDB) {
            storage = MetaStore.fetchStorage();
            LOG.info("using {} meta-storage", storage.getName());
        }

        explore(res, dataset);

        finish();
        double acceptRate = (1.0 - (clusteringsRejected / (double) clusteringsEvaluated)) * 100;
        LOG.info("total time {}s, evaluated {} clusterings, rejected {} clusterings",
                df.format(clusteringTime), clusteringsEvaluated, clusteringsRejected);
        LOG.info("acceptance rate: {}%", df.format(acceptRate));
        printStats(res);
        /* for (String str : blacklist) {            LOG.debug("blacklist: {}", str);
        } */
        if (useMetaDB) {
            storage.close();
        }
        return res;
    }

    /**
     * Random exploration strategy
     *
     * @param c
     * @param base
     */
    private void expand(List<Clustering<E, C>> res, Props base) {
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
            execute(res, dataset, alg, props);
        } else {
            LOG.error("missing props!");
        }
    }

    private double randomDouble(double min, double max) {
        return min + (max - min) * rand.nextDouble();
    }

    private int randomInt(int min, int max) {
        if (max > min) {
            return rand.nextInt((max - min) + 1) + min;
        } else {
            LOG.warn("Invalid bounds for random generator, min: {}, max: {}", min, max);
            return min;
        }
    }

    private boolean isBlacklisted(Props props) {
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

    private void printStats(List<Clustering<E, C>> res) {
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
    private int countClusteringJobs() {
        ClusteringFactory cf = ClusteringFactory.getInstance();
        int total = 0;
        List<ClusteringAlgorithm> algs = cf.getAll();
        int perAlg = countMaxPerAlg(algs.size());
        for (ClusteringAlgorithm alg : algs) {
            if (alg.isDeterministic()) {
                total += perAlg;
            } else {
                total += ndRepeat * perAlg;
            }
        }
        return total;
    }

    private void fireResult(List<Clustering<E, C>> res) {
        I[] update = (I[]) new SimpleIndividual[res.size()];

        SimpleIndividual si;
        int n = 0;
        for (Clustering<E, C> clust : res) {
            if (clust != null) {
                si = clust.getLookup().lookup(SimpleIndividual.class);
                if (si == null) {
                    si = new SimpleIndividual(clust);
                    clust.lookupAdd(si);
                    update[n] = (I) si;
                } else {
                    update[n] = (I) si;
                    //otherwise nothing has changed
                }
                n++;
            }
        }

        for (EvolutionListener listener : evoListeners) {
            listener.resultUpdate(update, true);
        }

    }

    public HashMap<String, Double> getMeta() {
        return meta;
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

    public void setUseMetaDB(boolean b) {
        this.useMetaDB = b;
    }

    public void setMaxSolutions(int maxSolutions) {
        this.maxStates = maxSolutions;
    }

    private String printAlg(Map<ClusteringAlgorithm, Double> estTime) {
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

}
