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
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.neighbor.RNNSearch;
import org.clueminer.neighbor.RnnFactory;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * A density-based algorithm for discovering clusters in large spatial databases
 * with noise.
 *
 * @param <E>
 * @param <C>
 * @cite Ester, Martin, et al. "A density-based algorithm for discovering
 * clusters in large spatial databases with noise." Kdd. Vol. 96. No. 34. 1996.
 *
 * @author deric
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class DBSCAN<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> implements ClusteringAlgorithm<E, C> {

    public static final String name = "DBSCAN";

    public static final String MIN_PTS = "minPts";
    public static final String EPS = "eps";

    public static final String RNN_ALG = "rnnAlg";

    /**
     * Label for unclassified data samples.
     */
    private static final int UNCLASSIFIED = -1;
    /**
     * The minimum number of points required to form a cluster
     */
    @Param(name = MIN_PTS, description = "minimum number of points required to form a cluster", required = true, min = 1, max = Double.MAX_VALUE)
    private int minPts;
    /**
     * The range of neighborhood.
     */
    @Param(name = EPS, description = "the range of a point neighborhood", required = true, min = 1e-9, max = Double.MAX_VALUE)
    private double radius;

    private RNNSearch<E> nns;

    private int k;

    public DBSCAN() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> dataset, Props props) {
        int[] y = scan(dataset, props);
        int n = dataset.size();
        Clustering res = Clusterings.newList();
        int avgSize = (int) Math.sqrt(dataset.size());
        Cluster curr;
        int clustIdx;
        if (colorGenerator != null) {
            colorGenerator.reset();
        }
        for (int i = 0; i < n; i++) {
            if (y[i] == OUTLIER) {
                clustIdx = k;
            } else {
                clustIdx = y[i];
            }
            if (!res.hasAt(clustIdx)) {
                curr = res.createCluster(clustIdx, avgSize);
                curr.setAttributes(dataset.getAttributes());
                if (colorGenerator != null) {
                    curr.setColor(colorGenerator.next());
                }
            }
            curr = res.get(clustIdx);
            curr.add(dataset.get(i));
        }
        if (res.hasAt(k)) {
            res.get(k).setName(Algorithm.OUTLIER_LABEL);
        }
        res.lookupAdd(dataset);
        res.setParams(props);
        return res;
    }

    public ArrayList<Instance> findNoise(Dataset<E> dataset, Props props) {
        int[] y = scan(dataset, props);
        int n = dataset.size();
        ArrayList<Instance> result = null;
        for (int i = 0; i < n; i++) {
            if (y[i] == OUTLIER) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(dataset.get(i));
            }
        }
        return result;
    }

    private int[] scan(Dataset<E> dataset, Props props) {
        minPts = props.getInt(MIN_PTS);
        if (minPts < 1) {
            throw new IllegalArgumentException("Invalid minPts: " + minPts);
        }

        radius = props.getDouble(EPS);
        if (radius <= 0.0) {
            throw new IllegalArgumentException("Invalid radius: " + radius);
        }

        k = 0;

        String rnnProvider = props.get(RNN_ALG, "linear RNN");
        nns = RnnFactory.getInstance().getProvider(rnnProvider);
        if (nns == null) {
            throw new RuntimeException("RNN provider was not found");
        }
        nns.setDataset(dataset);

        int n = dataset.size();
        int[] y = new int[n];
        Arrays.fill(y, UNCLASSIFIED);

        for (int i = 0; i < n; i++) {
            if (y[i] == UNCLASSIFIED) {
                //expand cluster
                List<Neighbor<E>> seeds = new ArrayList<>();
                nns.range(dataset.get(i), radius, seeds);
                //no core points
                if (seeds.size() < minPts) {
                    y[i] = OUTLIER;
                } else {
                    //all points in seeds are density-reachable from Point y[i]
                    y[i] = k;
                    for (int j = 0; j < seeds.size(); j++) {
                        if (y[seeds.get(j).index] == UNCLASSIFIED) {
                            y[seeds.get(j).index] = k;
                            Neighbor<E> neighbor = seeds.get(j);
                            List<Neighbor<E>> secondaryNeighbors = new ArrayList<>();
                            nns.range(neighbor.key, radius, secondaryNeighbors);

                            if (secondaryNeighbors.size() >= minPts) {
                                seeds.addAll(secondaryNeighbors);
                            }
                        }

                        if (y[seeds.get(j).index] == OUTLIER) {
                            y[seeds.get(j).index] = k;
                        }
                    }
                    k++;
                }
            }
        }
        return y;
    }

    public RNNSearch<E> getNns() {
        return nns;
    }

    public void setNns(RNNSearch<E> nns) {
        this.nns = nns;
    }
}
