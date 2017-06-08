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
package org.clueminer.clustering.algorithm;

import java.util.Arrays;
import org.clueminer.clustering.ClusterHelper;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.Configurator;
import org.clueminer.clustering.api.SeedSelection;
import org.clueminer.clustering.api.SeedSelectionFactory;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Partitioning Around Medoids (PAM) - the most common realization of k-medoid
 * clustering
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class PAM<E extends Instance, C extends Cluster<E>> extends KClustererBase<E, C> implements ClusteringAlgorithm<E, C> {

    private static final String NAME = "PAM";
    protected int repeats = 1;
    protected int iterLimit = 100;

    @Param(name = KMeans.K, description = "number of clusters", required = true, min = 2, max = 50)
    protected int k;

    public static final String SEED_SELECTION = "seed_selection";
    @Param(name = SEED_SELECTION, description = "Seed selection")
    protected String seedSelection;

    private static final Logger LOG = LoggerFactory.getLogger(PAM.class);

    @Override
    public String getName() {
        return NAME;
    }

    protected double cluster(Dataset<E> dataset, E[] medoids, Clustering<E, C> clustering) {
        double totalDistance = 0.0;
        int changes;
        int[] bestMedCand = new int[medoids.length];
        double[] bestMedCandDist = new double[medoids.length];
        int[] currMedoids = new int[medoids.length];
        for (int i = 0; i < medoids.length; i++) {
            currMedoids[i] = medoids[i].getIndex();
        }
        double dist;
        E current;
        int iter = 0;
        boolean changed;

        do {
            changes = 0;
            totalDistance = 0.0;

            for (int i = 0; i < dataset.size(); i++) {
                int assign = 0;
                current = dataset.get(i);
                double minDist = distanceFunction.measure(dataset.get(currMedoids[0]), current);

                for (int k = 1; k < medoids.length; k++) {
                    dist = distanceFunction.measure(dataset.get(currMedoids[k]), current);
                    if (dist < minDist) {
                        minDist = dist;
                        assign = k;
                    }
                }
                C clust;
                if (!clustering.hasAt(assign)) {
                    LOG.debug("creating cluster {}, current size: {}", assign, clustering.size());
                    clust = clustering.createCluster(assign);
                    if (colorGenerator != null) {
                        clust.setColor(colorGenerator.next());
                    }
                    changes++;
                    changed = true;
                } else {
                    clust = clustering.assignedCluster(current);
                    if (clust == null) {
                        clust = clustering.get(assign);
                        changes++;
                        changed = true;
                    } else if (clust.getClusterId() != assign) {
                        clust.remove(current);
                        clust = clustering.get(assign);
                        changes++;
                        changed = true;
                    } else {
                        changed = false;
                    }
                }
                if (changed) {
                    clust.add(current);
                }

                totalDistance += minDist * minDist;
            }

            //Update the medioids
            Arrays.fill(bestMedCandDist, Double.MAX_VALUE);
            for (int i = 0; i < dataset.size(); i++) {
                double currCandidateDist = 0.0;
                int clusterId = clustering.assignedCluster(i);
                E medCandadate = dataset.get(i);
                for (int j = 0; j < dataset.size(); j++) {
                    if (j == i || clustering.assignedCluster(j) != clusterId) {
                        continue;
                    }
                    currCandidateDist += Math.pow(distanceFunction.measure(medCandadate, dataset.get(j)), 2);
                }

                if (clusterId >= 0 && currCandidateDist < bestMedCandDist[clusterId]) {
                    bestMedCand[clusterId] = i;
                    bestMedCandDist[clusterId] = currCandidateDist;
                }
            }
            System.arraycopy(bestMedCand, 0, currMedoids, 0, medoids.length);
            LOG.debug("iter {}, changes = {}, distance = {}", iter, changes, totalDistance);
        } while (changes > 0 && iter++ < iterLimit);

        return totalDistance;
    }

    /**
     * TODO: make an interface from this (maybe we could use different
     * estimations methods)
     *
     * @param dataset
     * @return
     */
    public int guessK(Dataset<E> dataset) {
        return (int) Math.sqrt(dataset.size() / 2);
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> dataset, Props props) {
        SeedSelectionFactory sf = SeedSelectionFactory.getInstance();
        SeedSelection<E> seed = sf.getProvider(props.get(SEED_SELECTION, "random"));

        distanceFunction = ClusterHelper.initDistance(props);
        int k = guessK(dataset);
        Clustering<E, C> clustering = (Clustering<E, C>) Clusterings.newList(k);
        clustering.lookupAdd(dataset);
        if (colorGenerator != null) {
            colorGenerator.reset();
        }
        E[] prototypes = seed.selectPrototypes(dataset, props);

        double dist = cluster(dataset, prototypes, clustering);
        LOG.debug("total distane = {}", dist);
        return clustering;
    }

    @Override
    public Configurator<E> getConfigurator() {
        //we can use same approach as in case of k-means
        return KMeansConfig.getInstance();
    }

    @Override
    public boolean isDeterministic() {
        return true;
    }

}
