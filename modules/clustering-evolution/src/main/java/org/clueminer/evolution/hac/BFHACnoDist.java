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
package org.clueminer.evolution.hac;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.CutoffStrategyFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.api.factory.LinkageFactory;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.Individual;
import org.clueminer.math.StandardisationFactory;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 * @param <I>
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = Evolution.class)
public class BFHACnoDist<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>>
        extends BruteForceHacEvolution<I, E, C> implements Runnable, Evolution<I, E, C>, Lookup.Provider {

    private static final String NAME = "Brute-force HAC (no dist)";
    private static final Logger logger = Logger.getLogger(BFHACnoDist.class.getName());

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void run() {
        evolutionStarted(this);
        prepare();
        StandardisationFactory sf = StandardisationFactory.getInstance();
        List<String> standartizations = sf.getProviders();
        LinkageFactory lf = LinkageFactory.getInstance();
        linkage = lf.getAll();
        CutoffStrategyFactory cf = CutoffStrategyFactory.getInstance();
        cutoff = cf.getAll();
        InternalEvaluatorFactory ief = InternalEvaluatorFactory.getInstance();
        evaluators = ief.getAll();

        int stdMethods = standartizations.size();

        if (ph != null) {
            int workunits = stdMethods * 2 * linkage.size() * cutoff.size() * evaluators.size();
            logger.log(Level.INFO, "stds: {0}", stdMethods);
            logger.log(Level.INFO, "linkages: {0}", linkage.size());
            logger.log(Level.INFO, "evolution workunits: {0}", workunits);
            ph.start(workunits);
        }
        cnt = 0;
        for (ClusterLinkage link : linkage) {
            ClusteringAlgorithm alg = getAlgorithm();
            if (alg instanceof AgglomerativeClustering) {
                if (!((AgglomerativeClustering) alg).isLinkageSupported(link.getName())) {
                    //skip unsupported linkages
                    continue;
                }
            }
            for (String std : standartizations) {
                //no log scale
                makeClusters(std, false, link);
                //with log scale
                makeClusters(std, true, link);
            }
        }
        finish();
    }

    /**
     * Make clusters - not war
     *
     * @param std
     * @param logscale
     * @param link
     */
    @Override
    protected void makeClusters(String std, boolean logscale, ClusterLinkage link) {
        Props params = new Props();
        Clustering<E, C> clustering;
        //for cophenetic correlation we need proximity matrix
        params.put(PropType.PERFORMANCE, AlgParams.KEEP_PROXIMITY, true);
        params.put(AlgParams.ALG, exec.getAlgorithm().getName());
        params.putBoolean(AlgParams.LOG, logscale);
        params.put(AlgParams.STD, std);
        params.putBoolean(AlgParams.CLUSTERING_TYPE, true);
        params.put(AlgParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        params.put(AlgParams.LINKAGE, link.getName());
        params.put(AlgParams.DIST, DistanceFactory.getInstance().getProvider("Euclidean").getName());

        for (CutoffStrategy cut : cutoff) {
            params.put(AlgParams.CUTOFF_STRATEGY, cut.getName());
            for (InternalEvaluator ie : evaluators) {
                params.put(AlgParams.CUTOFF_SCORE, ie.getName());
                clustering = exec.clusterRows(dataset, params);
                //make sure the clustering is valid
                if (clustering.instancesCount() == dataset.size()) {
                    clustering.setName("#" + cnt);
                    individualCreated(clustering);
                }
                if (ph != null) {
                    ph.progress(cnt++);
                }
            }
        }
    }

}
