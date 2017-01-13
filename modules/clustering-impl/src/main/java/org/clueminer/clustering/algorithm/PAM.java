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
import org.clueminer.clustering.api.Assignment;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.Configurator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 * Partitioning Around Medoids (PAM) - the most common realization of k-medoid
 * clustering
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class PAM<E extends Instance, C extends Cluster<E>> extends KClustererBase<E, C> implements ClusteringAlgorithm<E, C> {

    private static final String NAME = "PAM";
    protected int repeats = 1;
    protected int iterLimit = 100;

    @Override
    public String getName() {
        return NAME;
    }

    protected double cluster(Dataset<E> dataset, int[] medioids, Assignment assignments) {
        double totalDistance = 0.0;
        int changes;
        int[] bestMedCand = new int[medioids.length];
        double[] bestMedCandDist = new double[medioids.length];
        double dist;
        Instance current;
        int iter = 0;

        do {
            changes = 0;
            totalDistance = 0.0;

            for (int i = 0; i < dataset.size(); i++) {
                int assign = 0;
                current = dataset.get(i);
                double minDist = distanceFunction.measure(dataset.get(medioids[0]), current);

                for (int k = 1; k < medioids.length; k++) {
                    dist = distanceFunction.measure(dataset.get(medioids[k]), current);
                    if (dist < minDist) {
                        minDist = dist;
                        assign = k;
                    }
                }

                //Update which cluster it is in
                if (assignments.assigned(i) != assign) {
                    changes++;
                    assignments.assign(i, assign);
                }
                totalDistance += minDist * minDist;
            }

            //Update the medioids
            Arrays.fill(bestMedCandDist, Double.MAX_VALUE);
            for (int i = 0; i < dataset.size(); i++) {
                double currCandidateDist = 0.0;
                int clusterId = assignments.assigned(i);
                Instance medCandadate = dataset.get(i);
                for (int j = 0; j < dataset.size(); j++) {
                    if (j == i || assignments.assigned(j) != clusterId) {
                        continue;
                    }
                    currCandidateDist += Math.pow(distanceFunction.measure(medCandadate, dataset.get(j)), 2);
                }

                if (currCandidateDist < bestMedCandDist[clusterId]) {
                    bestMedCand[clusterId] = i;
                    bestMedCandDist[clusterId] = currCandidateDist;
                }
            }
            System.arraycopy(bestMedCand, 0, medioids, 0, medioids.length);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
