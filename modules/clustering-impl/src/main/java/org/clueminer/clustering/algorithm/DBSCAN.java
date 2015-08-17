/*
 * Copyright (C) 2011-2015 clueminer.org
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.neighbor.RNNSearch;
import org.clueminer.utils.Props;

/**
 * A density-based algorithm for discovering clusters in large spatial databases
 * with noise.
 *
 * @cite Ester, Martin, et al. "A density-based algorithm for discovering
 * clusters in large spatial databases with noise." Kdd. Vol. 96. No. 34. 1996.
 *
 * @author deric
 */
public class DBSCAN extends AbstractClusteringAlgorithm implements ClusteringAlgorithm {

    public static final String name = "DBSCAN";

    public static final String MIN_PTS = "minPts";
    public static final String RADIUS = "radius";

    /**
     * Label for unclassified data samples.
     */
    private static final int UNCLASSIFIED = -1;
    /**
     * The minimum number of points required to form a cluster
     */
    @Param(name = MIN_PTS, description = "minimum number of points required to form a cluster", required = true, min = 1, max = Double.MAX_VALUE)
    private double minPts;
    /**
     * The range of neighborhood.
     */
    @Param(name = RADIUS, description = "the range of a point neighborhood", required = true, min = 1e-9, max = Double.MAX_VALUE)
    private double radius;

    public DBSCAN() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Clustering<? extends Cluster> cluster(Dataset<? extends Instance> dataset, Props props) {
        minPts = props.getDouble(MIN_PTS);
        if (minPts < 1) {
            throw new IllegalArgumentException("Invalid minPts: " + minPts);
        }

        radius = props.getDouble(RADIUS);
        if (radius <= 0.0) {
            throw new IllegalArgumentException("Invalid radius: " + radius);
        }

        int k = 0;

        RNNSearch<Instance> nns;

        int n = dataset.size();
        int[] y = new int[n];
        Arrays.fill(y, UNCLASSIFIED);

        for (int i = 0; i < n; i++) {
            if (y[i] == UNCLASSIFIED) {
                List<Neighbor<T>> neighbors = new ArrayList<Neighbor<T>>();
                nns.range(dataset.get(i), radius, neighbors);
                if (neighbors.size() < minPts) {
                    y[i] = OUTLIER;
                } else {
                    y[i] = k;
                    for (int j = 0; j < neighbors.size(); j++) {
                        if (y[neighbors.get(j).index] == UNCLASSIFIED) {
                            y[neighbors.get(j).index] = k;
                            Neighbor<T> neighbor = neighbors.get(j);
                            List<Neighbor<T>> secondaryNeighbors = new ArrayList<Neighbor<T>>();
                            nns.range(neighbor.key, radius, secondaryNeighbors);

                            if (secondaryNeighbors.size() >= minPts) {
                                neighbors.addAll(secondaryNeighbors);
                            }
                        }

                        if (y[neighbors.get(j).index] == OUTLIER) {
                            y[neighbors.get(j).index] = k;
                        }
                    }
                    k++;
                }
            }
        }

        int[] size = new int[k + 1];
        for (int i = 0; i < n; i++) {
            if (y[i] == OUTLIER) {
                size[k]++;
            } else {
                size[y[i]]++;
            }
        }

        return null;
    }

}
