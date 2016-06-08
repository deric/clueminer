/*
 * Copyright (C) 2011-2016 clueminer.org
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.clueminer.eval.McClainRao;
import org.clueminer.eval.PointBiserialNorm;
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
    private static final Logger LOGGER = Logger.getLogger(MetaSearch.class.getName());

    protected final Executor exec;
    protected int gen;
    private List<Distance> dist;
    protected List<ClusterLinkage> linkage;
    protected List<CutoffStrategy> cutoff;
    protected List<InternalEvaluator<E, C>> evaluators;
    protected int cnt;
    private ParetoFrontQueue q;
    private HashMap<String, Double> meta;

    public MetaSearch() {
        super();
        exec = new ClusteringExecutorCached();
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
        Props conf;
        Clustering<E, C> c;
        for (ClusteringAlgorithm alg : cf.getAll()) {
            conf = new Props();
            conf.put(AlgParams.ALG, alg.getName());
            System.out.println("c: " + alg.getName());
            c = cluster(dataset, conf);
            System.out.println(c.size() + ": " + c.fingerprint());
            queue.add(c);
            fireResult(c);
            if (ph != null) {
                ph.progress(cnt++);
            }
        }
        System.out.println(queue.stats());
        queue.printRanking(new NMIsqrt());
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

        if (ph != null) {
            ClusteringFactory cf = ClusteringFactory.getInstance();
            int workunits = cf.getAll().size();
            LOGGER.log(Level.INFO, "search workunits: {0}", workunits);
            ph.start(workunits);
        }

        if (!config.containsKey(AlgParams.STD)) {
            config.put(AlgParams.STD, "z-score");
        }
        config.putInt("k", 5);
        Dataset<E> data = standartize(config);
        meta = computeMeta(data, config);
        LOGGER.log(Level.INFO, "got {0} meta parameters", meta.size());
        List<ClusterEvaluation<Instance, Cluster<Instance>>> objectives = new LinkedList<>();
        objectives.add(new PointBiserialNorm<>());
        objectives.add(new RatkowskyLance<>());
        ParetoFrontQueue queue = new ParetoFrontQueue(10, objectives, new McClainRao<E, C>());

        landmark(dataset, queue);
        cnt = 0;

        finish();
        return queue;
    }

    private void fireResult(Clustering<E, C> clustering) {
        I[] individuals = (I[]) new SimpleIndividual[]{new SimpleIndividual(clustering)};
        for (EvolutionListener listener : evoListeners) {
            listener.resultUpdate(individuals);
        }
    }

    public HashMap<String, Double> getMeta() {
        return meta;
    }

    @Override
    public I createIndividual() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
