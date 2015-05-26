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
package org.clueminer.bagging;

import org.clueminer.clustering.api.ClusteringReduce;
import java.util.Iterator;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class NaiveReduce implements ClusteringReduce {

    @Override
    public Clustering<? extends Cluster> reduce(Clustering[] clusts, AbstractClusteringAlgorithm alg, ColorGenerator cg, Props props) {
        int k = props.getInt(KMeans.K);

        Clustering<? extends Cluster> result = new ClusterList(k); //reducer - find consensus
        //vote about final result
        Instance curr;
        Iterator<Instance> it = clusts[0].instancesIterator();
        Cluster<? extends Instance> cluster;
        int[][] mapping = findMapping(clusts, k, alg.getDistanceFunction());

        if (cg != null) {
            cg.reset();
        }

        int idx;
        while (it.hasNext()) {
            curr = it.next();
            int[] assign = new int[k];
            for (int i = 0; i < clusts.length; i++) {
                cluster = clusts[i].assignedCluster(curr);
                if (i > 0) {
                    assign[map(mapping, i, cluster.getClusterId())]++;
                } else {
                    assign[cluster.getClusterId()]++;
                }
            }
            idx = findMax(assign);
            //check if cluster already exists
            if (!result.hasAt(idx)) {
                result.createCluster(idx);
                if (cg != null) {
                    result.get(idx).setColor(cg.next());
                }
            }
            //final cluster assignment
            result.get(idx).add(curr);
        }
        result.compact();

        return result;
    }

    /**
     * Find maximum integer in an array
     *
     * @param assign
     * @return
     */
    private int findMax(int[] assign) {
        int max = -1;
        for (int i = 0; i < assign.length; i++) {
            if (assign[i] > max) {
                max = assign[i];
            }
        }
        return max;
    }

    /**
     * Mapping contains records about all clustering except first one which is
     * considered as reference one
     *
     * @param mapping
     * @param resId
     * @param source
     * @return
     */
    private int map(int[][] mapping, int resId, int source) {
        return mapping[resId - 1][source];
    }

    /**
     * Find closes centroid in all other clusterings
     *
     * @param clusts
     * @return array with n-1 mappings
     */
    private int[][] findMapping(Clustering[] clusts, int k, DistanceMeasure dm) {
        Clustering<? extends Cluster> first = clusts[0];
        int[][] res = new int[clusts.length - 1][k];
        double dist;
        for (int i = 1; i < clusts.length; i++) {
            for (int j = 0; j < k; j++) {
                double min = Double.MAX_VALUE;
                int closest = -1;
                // go through all other clusters
                for (int l = 0; l < k; l++) {
                    dist = dm.measure(first.get(j).getCentroid(), clusts[i].get(l).getCentroid());
                    if (dist < min) {
                        // j -> l
                        min = dist;
                        closest = clusts[i].get(l).getClusterId();
                    }
                }
                res[i - 1][j] = closest;
            }
        }
        return res;
    }

}
