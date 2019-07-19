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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.Rank;
import org.clueminer.clustering.api.factory.RankFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.external.NMIsqrt;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionListener;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.hac.SimpleIndividual;
import org.clueminer.math.StandardisationFactory;
import org.clueminer.utils.MapUtils;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
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
@ServiceProvider(service = Evolution.class)
public class Explore<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>> extends AbsMetaExp<I, E, C>
        implements Runnable, Evolution<I, E, C>, Lookup.Provider, Callable<List<Clustering<E, C>>> {

    private static final String NAME = "Explore";
    private static final Logger LOG = LoggerFactory.getLogger(Explore.class);

    public Explore() {
        super();
        cmp = new NMIsqrt();
    }

    public void configure(Props p) {
        numResults = p.getInt("results", 100);
        maxStates = p.getInt("max-states", 200);
    }

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    public void prepare() {
        LOG.info("Starting {}", getName());
        rand = new Random();
        clusteringsEvaluated = 0;
        clusteringsRejected = 0;
        clusteringsFailed = 0;
        jobs = 0;

        if (!config.containsKey(AlgParams.STD)) {
            config.put(AlgParams.STD, "z-score");
        }
        cnt = 0;
        RankFactory rf = RankFactory.getInstance();
        //ranking = rf.getDefault();
    }


    public void clusteringFound(Executor exec, Clustering<E, C> c) {
        //TODO: process result?
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
        Props conf;
        Map<ClusteringAlgorithm, Double> estTime = new HashMap<>();
        double time;
        //estimate computing time for each algorithm
        for (ClusteringAlgorithm alg : algs) {
            time = alg.getConfigurator().estimateRunTime(dataset, config);
            estTime.put(alg, time);
        }
        Map<ClusteringAlgorithm, Double> sortedMap = MapUtils.sortByValue(estTime);
        StandardisationFactory sf = StandardisationFactory.getInstance();
        List<String> standartizations = sf.getProviders();

        LOG.info("available algorithms: {}", printAlg(sortedMap));
        int perAlg = countMaxPerAlg(algs.size());

        for (int i = 0; i < perAlg; i++) {
            for (Entry<ClusteringAlgorithm, Double> alg : sortedMap.entrySet()) {
                conf = getConfig().copy(PropType.PERFORMANCE, PropType.VISUAL);
                conf.put(AlgParams.ALG, alg.getKey().getName());
                LOG.debug("expanding {} configs of alg: {}", perAlg, alg.getKey().getName());
                if (modifyStd) {
                    for (String std : standartizations) {
                        conf.put(AlgParams.STD, std);
                        expand(conf, queue);
                    }
                } else {
                    expand(conf, queue);
                }

            }
        }
    }

    protected void fireResult(List<Clustering<E, C>> res) {
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
                    //otherwise nothing has changedq
                }
                n++;
            }
        }

        for (EvolutionListener listener : evoListeners) {
            listener.resultUpdate(update, true);
        }

    }

    @Override
    public SortedMap<Double, Clustering<E, C>> computeRanking() {
        return null;
    }

}
