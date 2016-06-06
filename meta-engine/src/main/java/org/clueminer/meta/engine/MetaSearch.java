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
import org.clueminer.eval.AIC;
import org.clueminer.eval.PointBiserial;
import org.clueminer.eval.RatkowskyLance;
import org.clueminer.eval.external.NMIsqrt;
import org.clueminer.evolution.BaseEvolution;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.Individual;
import org.clueminer.meta.api.DataStats;
import org.clueminer.meta.api.DataStatsFactory;
import org.clueminer.meta.ranking.ParetoFrontQueue;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * Use meta-features to select suitable (optimal) clustering algorithm.
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class MetaSearch<E extends Instance, C extends Cluster<E>> extends BaseEvolution
        implements Runnable, Evolution, Lookup.Provider, Callable<ParetoFrontQueue> {

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

    public MetaSearch() {
        super();
        exec = new ClusteringExecutorCached();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Individual createIndividual() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        HashMap<String, Double> meta = new HashMap<>();
        double v;
        for (DataStats<E> ds : dsf.getAll()) {
            ds.computeAll(data, meta, config);
        }
        System.out.println("meta features: " + meta.toString());
        return meta;
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
            c = exec.clusterRows(dataset, conf);
            System.out.println(c.size() + ": " + c.fingerprint());
            queue.add(c);
        }
        System.out.println(queue.stats());
        queue.printRanking(new NMIsqrt());
    }

    @Override
    public ParetoFrontQueue call() throws Exception {
        evolutionStarted(this);
        prepare();
        InternalEvaluatorFactory<E, C> ief = InternalEvaluatorFactory.getInstance();
        evaluators = ief.getAll();

        if (ph != null) {
            int workunits = 5;
            LOGGER.log(Level.INFO, "search workunits: {0}", workunits);
            ph.start(workunits);
        }

        if (!config.containsKey(AlgParams.STD)) {
            config.put(AlgParams.STD, "z-score");
        }
        config.putInt("k", 5);
        Dataset<E> data = standartize(config);
        HashMap<String, Double> meta = computeMeta(data, config);
        LOGGER.log(Level.INFO, "got {0} meta parameters", meta.size());
        List<ClusterEvaluation<Instance, Cluster<Instance>>> objectives = new LinkedList<>();
        objectives.add(new AIC<>());
        objectives.add(new RatkowskyLance<>());
        ParetoFrontQueue queue = new ParetoFrontQueue(10, objectives, new PointBiserial<E, C>());

        landmark(dataset, queue);
        cnt = 0;

        finish();
        return queue;
    }

}
