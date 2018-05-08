/*
 * Copyright (C) 2011-2018 clueminer.org
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

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.clustering.api.factory.EvaluationFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.eval.BIC;
import org.clueminer.eval.McClainRao;
import org.clueminer.eval.RatkowskyLance;
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
import org.clueminer.meta.ranking.ParetoFrontQueue;
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
public class MetaSearch<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>> extends BaseEvolution<I, E, C>
        implements Runnable, Evolution<I, E, C>, Lookup.Provider, Callable<ParetoFrontQueue> {

    private static final String NAME = "Meta search";
    private static final Logger LOG = LoggerFactory.getLogger(MetaSearch.class);

    protected final Executor exec;
    protected int gen;
    private List<Distance> dist;
    protected List<ClusterLinkage> linkage;
    protected List<CutoffStrategy> cutoff;
    protected List<InternalEvaluator<E, C>> evaluators;
    protected int cnt;
    private ParetoFrontQueue q;
    private HashMap<String, Double> meta;
    private int ndRepeat = 5;
    protected List<ClusterEvaluation<E, C>> objectives;
    protected ClusterEvaluation<E, C> sortObjective;
    private double clusteringTime;
    private int clusteringsEvaluated;
    private int clusteringsRejected;
    private I[] bestIndividuals;
    private int numResults = 15;
    private int numFronts = 10;
    private boolean useMetaDB = false;
    private Random rand;
    private int maxRetries = 5;
    //maximum number of explored states, use -1 to use unlimited search
    private int maxStates = 200;
    private ObjectOpenHashSet<String> blacklist;
    private ParetoFrontQueue queue;
    private NMIsqrt cmp;
    private double diversityThreshold = 0.2;
    private static final DecimalFormat df = new DecimalFormat("#,##0.00");
    private boolean expandOnlyTop = false;

    public MetaSearch() {
        super();
        exec = new ClusteringExecutorCached();
        this.objectives = Lists.newLinkedList();
        objectives.add(new BIC<>());
        objectives.add(new RatkowskyLance<>());
        sortObjective = new McClainRao<>();
        cmp = new NMIsqrt();
    }

    public void configure(Props p) {
        // comma separated objectives
        EvaluationFactory ef = EvaluationFactory.getInstance();
        String obj = p.get("objectives", "");
        if (!obj.isEmpty()) {
            objectives.clear();
            String[] objs = obj.split(",");
            for (String str : objs) {
                objectives.add(ef.getProvider(str));
            }
        }
        numFronts = p.getInt("fronts", 10);
        numResults = p.getInt("results", 15);
        diversityThreshold = p.getDouble("diversity", 0.2);
        sortObjective = ef.getProvider(p.get("sort-objective", "McClain-Rao"));
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
            q = call();
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
     * TODO: only fast algorithms should be included in landmarking phase
     *
     * @param dataset
     * @param queue
     */
    private void landmark(Dataset<E> dataset) {
        ClusteringFactory cf = ClusteringFactory.getInstance();
        List<ClusteringAlgorithm> algs = cf.getAll();
        Props conf;
        TreeMap<ClusteringAlgorithm, Double> estTime = new TreeMap<>();
        double time;
        CostFunctionFactory cff = CostFunctionFactory.getInstance();
        CostFunction costFunc = cff.getDefault();
        //estimate computing time for each algorithm
        for (ClusteringAlgorithm alg : algs) {
            time = costFunc.estimate(alg.getName(), CostMeasure.TIME, meta);
            estTime.put(alg, time);
        }
        LOG.info("landmarking algorithms: {}", printAlg(estTime));
        for (ClusteringAlgorithm alg : estTime.descendingKeySet()) {
            conf = getConfig().copy(PropType.PERFORMANCE, PropType.VISUAL);
            conf.put(AlgParams.ALG, alg.getName());
            execute(dataset, alg, conf);
        }
        LOG.debug("stats: {}", queue.stats());
        queue.printRanking(new NMIsqrt());
    }

    private void execute(Dataset<E> dataset, ClusteringAlgorithm alg, Props conf) {
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
            cnt++;
            if (isValid(c)) {
                c.setId(gen++);
                queue.add(c);
                fireResult(queue);
            } else {
                clusteringsRejected++;
            }

            if (ph != null) {
                ph.progress(cnt);
            }
            i++;
        } while (i < repeat);
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
        int maxK = (int) (2 * Math.sqrt(d.size()));
        if (c.size() > maxK) {
            LOG.debug("rejecting with too many clusters {}, params: {}", c.size(), c.getParams());
            return false;
        }

        Clustering<E, C> other;
        Iterator<Clustering<E, C>> it = queue.iterator();
        double diverse;
        while (it.hasNext()) {
            other = it.next();
            if (other != null) {
                diverse = diversity(other, c);
                LOG.trace("diversity = {}. {} vs {}", diverse, other.fingerprint(), c.fingerprint());
                if (diverse < diversityThreshold) {
                    LOG.debug("rejecting {} (vs {}) due to low diversity = {}", c.fingerprint(), other.fingerprint(), diverse);
                    return false;
                }
            }
        }

        return true;
    }

    private double diversity(Clustering<E, C> a, Clustering<E, C> b) {
        return 1 - cmp.score(a, b, config);
    }

    @Override
    public ParetoFrontQueue call() throws Exception {
        rand = new Random();
        clusteringTime = 0.0;
        clusteringsEvaluated = 0;
        clusteringsRejected = 0;
        LOG.info("Starting meta-search. Objectives: {}, Min diversity: {}", printObjectives(), diversityThreshold);
        evolutionStarted(this);
        prepare();
        InternalEvaluatorFactory<E, C> ief = InternalEvaluatorFactory.getInstance();
        evaluators = ief.getAll();
        //TODO: make sure that numResults < population
        bestIndividuals = (I[]) new SimpleIndividual[numResults];
        if (cg != null) {
            exec.setColorGenerator(cg);
        }
        blacklist = new ObjectOpenHashSet<>(numFronts * numResults);

        if (ph != null) {
            int workunits;
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
        queue = new ParetoFrontQueue(numFronts, objectives, sortObjective);
        cnt = 0;
        MetaStorage storage = null;

        if (useMetaDB) {
            storage = MetaStore.fetchStorage();
            LOG.info("using {} meta-storage", storage.getName());
        }

        if (queue.isEmpty()) {
            //initialize queue with default alg configurations
            landmark(dataset);
        }
        //expand top solutions
        explore(numResults);

        finish();
        double acceptRate = (1.0 - (clusteringsRejected / (double) clusteringsEvaluated)) * 100;
        LOG.info("total time {}s, evaluated {} clusterings, rejected {} clusterings",
                df.format(clusteringTime), clusteringsEvaluated, clusteringsRejected);
        LOG.info("acceptance rate: {}%", df.format(acceptRate));
        printStats(queue);
        /* for (String str : blacklist) {            LOG.debug("blacklist: {}", str);
        } */
        if (useMetaDB) {
            storage.close();
        }
        return queue;
    }

    /**
     * Explore most promising solutions
     *
     * @param queue
     * @param topN
     */
    private void explore(int topN) {
        Iterator<Clustering<E, C>> it = queue.iterator();
        Clustering<E, C> c;
        int n = 0;
        Props props;
        while (it.hasNext()) {
            if (expandOnlyTop && n < topN) {
                return;
            }
            c = it.next();
            if (c != null) {
                props = c.getParams();
                LOG.debug("expanding solution#{}", n + 1, props);
                expand(c, props);
                n++;
            }
        }
    }

    /**
     * Random exploration strategy
     *
     * @param c
     * @param base
     */
    private void expand(Clustering<E, C> c, Props base) {
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
            execute(dataset, alg, props);
        } else {
            LOG.error("missing props!");
        }
    }

    private double randomDouble(double min, double max) {
        return min + (max - min) * rand.nextDouble();
    }

    private int randomInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    private boolean isBlacklisted(Props props) {
        return blacklist.contains(props.toJson());
    }

    protected ClusteringAlgorithm parseAlgorithm(Props params) {
        String alg = params.get(AlgParams.ALG);
        if (alg == null) {
            throw new RuntimeException("missing algorithm identifier for " + params.toString());
        }
        return ClusteringFactory.getInstance().getProvider(alg);
    }

    private void printStats(ParetoFrontQueue queue) {
        double score = 0.0, s1 = 0, s3 = 0, s5 = 0.0, s10 = 0.0, s;
        double sTop = 0.0;
        Iterator<Clustering<E, C>> it = queue.iterator();
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
        LOG.info("avg score in whole population ({}): {}, fronts: {}", n, numFormat(score), queue.stats());
    }

    private String numFormat(double d) {
        if (Double.isNaN(d)) {
            return "n/a";
        }
        return df.format(d);
    }

    public void clearObjectives() {
        if (objectives != null && !objectives.isEmpty()) {
            objectives.clear();
        }
    }

    /**
     * Count number of clustering runs for single meta-search.
     *
     * @return
     */
    private int countClusteringJobs() {
        ClusteringFactory cf = ClusteringFactory.getInstance();
        int total = 0;
        for (ClusteringAlgorithm alg : cf.getAll()) {
            if (alg.isDeterministic()) {
                total += 1;
            } else {
                total += ndRepeat;
            }
        }
        return total;
    }

    private void fireResult(ParetoFrontQueue queue) {
        Iterator<Clustering<E, C>> it = queue.iterator();
        int n = 0;
        Clustering<E, C> clust;
        SimpleIndividual si;
        int changes = 0;
        double extScore = 0.0;
        //update top solutions found
        while (it.hasNext() && n < numResults) {
            clust = it.next();
            si = clust.getLookup().lookup(SimpleIndividual.class);
            if (si == null) {
                si = new SimpleIndividual(clust);
                clust.lookupAdd(si);
                bestIndividuals[n] = (I) si;
                changes++;
            } else {
                if (!si.getClustering().equals(clust)) {
                    changes++;
                }
                bestIndividuals[n] = (I) si;
                //otherwise nothing has changed
            }
            extScore += si.getFitness();
            n++;
        }

        if (changes > 0) {
            LOG.info("{} changes in top population, avg fitness = {}", changes, extScore / n);
            I[] update;
            if (n < bestIndividuals.length) {
                update = (I[]) new SimpleIndividual[n];
                System.arraycopy(bestIndividuals, 0, update, 0, n);
            } else {
                update = bestIndividuals;
            }
            for (EvolutionListener listener : evoListeners) {
                listener.resultUpdate(update, true);
            }
        } else {
            LOG.debug("no changes in top population, population size: {}", queue.size());
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

    public void addObjective(ClusterEvaluation eval) {
        objectives.add(eval);
    }

    public void removeObjective(ClusterEvaluation eval) {
        objectives.remove(eval);
    }

    public void setSortObjective(ClusterEvaluation eval) {
        this.sortObjective = eval;
    }

    public ClusterEvaluation getObjective(int idx) {
        return objectives.get(idx);
    }

    public List<ClusterEvaluation<E, C>> getObjectives() {
        return objectives;
    }

    public int getNumObjectives() {
        return objectives.size();
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public void setNumFronts(int numFronts) {
        this.numFronts = numFronts;
    }

    public void setUseMetaDB(boolean b) {
        this.useMetaDB = b;
    }

    public void setMaxSolutions(int maxSolutions) {
        this.maxStates = maxSolutions;
    }

    public void setExpandOnlyTop(boolean expandOnlyTop) {
        this.expandOnlyTop = expandOnlyTop;
    }

    private String printObjectives() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int i = 0;
        for (ClusterEvaluation<E, C> ce : objectives) {
            if (i > 0) {
                sb.append(", ");
            }

            sb.append(ce.getName());
            i++;
        }
        sb.append("]");

        return sb.toString();
    }

    private String printAlg(TreeMap<ClusteringAlgorithm, Double> estTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int i = 0;
        for (Entry<ClusteringAlgorithm, Double> alg : estTime.descendingMap().entrySet()) {
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
