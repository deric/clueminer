/*
 * Copyright (C) 2011-2017 clueminer.org
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
import java.util.HashMap;
import java.util.List;
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
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.InternalEvaluator;
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
import org.clueminer.meta.api.DataStats;
import org.clueminer.meta.api.DataStatsFactory;
import org.clueminer.meta.ranking.ParetoFrontQueue;
import org.clueminer.utils.Props;
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

    public MetaSearch() {
        super();
        exec = new ClusteringExecutorCached();
        this.objectives = Lists.newLinkedList();
        objectives.add(new BIC<>());
        objectives.add(new RatkowskyLance<>());
        sortObjective = new McClainRao<>();
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
    private void landmark(Dataset<E> dataset, ParetoFrontQueue queue) {
        ClusteringFactory cf = ClusteringFactory.getInstance();

        for (ClusteringAlgorithm alg : cf.getAll()) {
            execute(dataset, alg, queue);
        }
        LOG.debug("stats: {}", queue.stats());
        queue.printRanking(new NMIsqrt());
    }

    private void execute(Dataset<E> dataset, ClusteringAlgorithm alg, ParetoFrontQueue queue) {
        Props conf = getConfig().copy();
        conf.put(AlgParams.ALG, alg.getName());

        int repeat = 1;
        int i = 0;
        if (!alg.isDeterministic()) {
            //repeat non-deterministic algorithms
            repeat = ndRepeat;
        }
        do {
            Clustering<E, C> c = cluster(dataset, conf);
            c.setId(cnt++);
            queue.add(c);
            fireResult(c);
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
        c.lookupAdd(time);
        return c;
    }

    @Override
    public ParetoFrontQueue call() throws Exception {
        evolutionStarted(this);
        prepare();
        InternalEvaluatorFactory<E, C> ief = InternalEvaluatorFactory.getInstance();
        evaluators = ief.getAll();
        if (cg != null) {
            exec.setColorGenerator(cg);
        }

        if (ph != null) {
            int workunits = countClusteringJobs();
            LOG.info("search workunits: {}", workunits);
            ph.start(workunits);
        }

        if (!config.containsKey(AlgParams.STD)) {
            config.put(AlgParams.STD, "z-score");
        }
        config.putInt("k", 5);
        Dataset<E> data = standartize(config);
        meta = computeMeta(data, config);
        LOG.info("got {} meta parameters", meta.size());
        ParetoFrontQueue queue = new ParetoFrontQueue(getPopulationSize(), objectives, sortObjective);
        cnt = 0;
        landmark(dataset, queue);

        finish();
        return queue;
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

    private void fireResult(Clustering<E, C> clustering) {
        I ind = (I) new SimpleIndividual(clustering);
        I[] individuals = (I[]) new SimpleIndividual[1];
        individuals[0] = ind;
        //TODO we need to compute all properties first...
        //fireIndividualCreated(ind);
        for (EvolutionListener listener : evoListeners) {
            listener.resultUpdate(individuals);
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

}
