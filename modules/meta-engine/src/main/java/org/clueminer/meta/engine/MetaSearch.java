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

import com.google.common.collect.Lists;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.EvaluationFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.BIC;
import org.clueminer.eval.McClainRao;
import org.clueminer.eval.RatkowskyLance;
import org.clueminer.eval.external.NMIsqrt;
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
import org.clueminer.utils.MapUtils;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import org.clueminer.utils.StopWatch;
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
public class MetaSearch<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>> extends AbsMetaExp<I, E, C>
        implements Runnable, Evolution<I, E, C>, Lookup.Provider {

    private static final String NAME = "Meta search";
    private static final Logger LOG = LoggerFactory.getLogger(MetaSearch.class);

    protected List<InternalEvaluator<E, C>> evaluators;
    private HashMap<String, Double> meta;
    protected List<ClusterEvaluation<E, C>> objectives;
    protected ClusterEvaluation<E, C> sortObjective;
    private I[] bestIndividuals;
    private int numFronts = 10;
    private int topN = 10;
    private boolean useMetaDB = false;
    private ParetoFrontQueue front;
    private double diversityThreshold = 0.2;
    private boolean expandOnlyTop = false;
    private boolean enforceDiversity = false;
    private MetaStorage storage;

    public MetaSearch() {
        super();
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
        topN = p.getInt("top-n", 10);
        diversityThreshold = p.getDouble("diversity", 0.2);
        sortObjective = ef.getProvider(p.get("sort-objective", "McClain-Rao"));
        useMetaDB = p.getBoolean("use-metadb", false);
        enforceDiversity = p.getBoolean("enforce-diversity", false);
        maxStates = p.getInt("max-states", 200);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private HashMap<String, Double> computeMeta(Dataset<E> data, Props config) {
        DataStatsFactory dsf = DataStatsFactory.getInstance();
        HashMap<String, Double> res = new HashMap<>();
        for (DataStats<E> ds : dsf.getAll()) {
            ds.computeAll(data, res, config);
        }
        return res;
    }

    private double diversity(Clustering<E, C> a, Clustering<E, C> b) {
        return 1 - cmp.score(a, b, config);
    }

    @Override
    public void prepare() {
        LOG.info("Starting {}", getName());
        rand = new Random();
        clusteringsEvaluated = 0;
        clusteringsRejected = 0;
        clusteringsFailed = 0;
        jobs = 0;
        if (enforceDiversity) {
            LOG.info("Objectives: {}, Min diversity: {}", printObjectives(), diversityThreshold);
        } else {
            LOG.info("Objectives: {}", printObjectives());
        }
        if (numResults < 0) {
            numResults = 15;
        }

        InternalEvaluatorFactory<E, C> ief = InternalEvaluatorFactory.getInstance();
        evaluators = ief.getAll();
        //TODO: make sure that numResults < population
        bestIndividuals = (I[]) new SimpleIndividual[numResults];

        if (!config.containsKey(AlgParams.STD)) {
            config.put(AlgParams.STD, "z-score");
        }
        //config.putInt("k", 5);
        Dataset<E> data = standartize(config);
        meta = computeMeta(data, config);
        LOG.info("got {} meta parameters", meta.size());
        storage = null;

        front = new ParetoFrontQueue(numFronts, objectives, sortObjective);

        if (useMetaDB) {
            storage = MetaStore.fetchStorage();
            LOG.info("using {} meta-storage", storage.getName());
        }
    }

    protected void finish(StopWatch st) {
        super.finish();

        double acceptRate = (1.0 - (clusteringsRejected / (double) clusteringsEvaluated)) * 100;

        st.endMeasure();
        LOG.info("total time {}s, evaluated {} clusterings, rejected {} clusterings, failed: {}, results: {}",
                st.timeInSec(), clusteringsEvaluated, clusteringsRejected, clusteringsFailed, results.size());

        LOG.info("acceptance rate: {}%", df.format(acceptRate));
        printStats(results);

        if (useMetaDB) {
            storage.close();
        }
    }

    /**
     * Iterates over various algorithm configuration
     *
     * @param dataset
     * @param queue
     */
    @Override
    public void explore(Dataset<E> dataset, BlockingQueue<ClusteringTask<E, C>> queue) {
        ClusteringFactory cf = ClusteringFactory.getInstance();
        List<ClusteringAlgorithm> algs = cf.getAll();
        Props conf = new Props();
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
                time = alg.getConfigurator().estimateRunTime(dataset, conf);
            }
            estTime.put(alg, time);
        }
        Map<ClusteringAlgorithm, Double> sortedMap = MapUtils.sortByValue(estTime);
        /**
         * LANDMARKING
         */
        LOG.info("landmarking algorithms: {}", printAlg(sortedMap));
        for (Entry<ClusteringAlgorithm, Double> alg : sortedMap.entrySet()) {
            conf = getConfig().copy(PropType.PERFORMANCE, PropType.VISUAL);
            conf.put(AlgParams.ALG, alg.getKey().getName());
            LOG.debug("expanding {}", alg.getKey().getName());
            expand(conf, queue);
        }
        LOG.debug("stats: {}", front.stats());
        front.printRanking(new NMIsqrt());

        exploit(topN, queue);
        LOG.info("exploit completed");
    }

    /**
     * Explore most promising solutions
     *
     * @param queue
     * @param topN
     */
    private void exploit(int topN, BlockingQueue<ClusteringTask<E, C>> queue) {
        LOG.info("expoiting phase, pareto front size: {}", front.size());
        Iterator<Clustering<E, C>> it = front.iterator();
        Clustering<E, C> c;
        int n = 0;
        Props props;
        while (it.hasNext()) {
            if (expandOnlyTop && n < topN) {
                return;
            }

            if (jobs > maxStates) {
                return;
            }

            c = it.next();
            if (c != null) {
                props = c.getParams();
                LOG.debug("expanding solution #{}", n + 1, props);
                expand(props, queue);
                n++;
            }
        }
    }

    public void clearObjectives() {
        if (objectives != null && !objectives.isEmpty()) {
            objectives.clear();
        }
    }

    @Override
    public void clusteringFound(Executor exec, Clustering<E, C> c) {
        LOG.debug("adding clustering to pareto front, size: {}", front.size());
        front.add(c);
        //queue is not used
        exploit(topN, null);
    }

    /**
     * Clusterings from res were already added to Pareto front in
     * clusteringFound
     *
     * @param res
     */
    @Override
    protected void fireResult(List<Clustering<E, C>> res) {
        resultUpdate(front);
    }

    private void resultUpdate(ParetoFrontQueue front) {
        LOG.debug("result update, front: {}", front.size());
        Iterator<Clustering<E, C>> it = front.iterator();
        int n = 0;
        Clustering<E, C> clust;
        SimpleIndividual si;
        int changes = 0;
        double extScore = 0.0;
        //update top solutions found
        while (it.hasNext() && n < numResults) {
            clust = it.next();
            if (clust != null) {
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
            } else {
                LOG.warn("front rentuned null clustering");
            }
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
            LOG.debug("no changes in top population, population size: {}", front.size());
        }
    }

    public HashMap<String, Double> getMeta() {
        return meta;
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

    public void setNumFronts(int numFronts) {
        this.numFronts = numFronts;
    }

    public void setUseMetaDB(boolean b) {
        this.useMetaDB = b;
    }

    public void setExpandOnlyTop(boolean expandOnlyTop) {
        this.expandOnlyTop = expandOnlyTop;
    }

    public void setEnforceDiversity(boolean enforce) {
        this.enforceDiversity = enforce;
    }

    /**
     * Sets minimal required diversity
     *
     * @param diversity
     */
    public void setDiversity(double diversity) {
        this.diversityThreshold = diversity;
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

}
