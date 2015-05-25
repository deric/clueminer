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

import java.util.Iterator;
import java.util.List;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.PartitioningClustering;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.eval.AIC;
import org.clueminer.eval.SDindex;
import org.clueminer.evolution.mo.MoSolution;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author deric
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class KMeansBagging extends AbstractClusteringAlgorithm implements PartitioningClustering {

    private static final String name = "K-Means bagging";

    public static final String BAGGING = "bagging";

    @Param(name = KMeansBagging.BAGGING, description = "number of independent k-means runs", required = false)
    private int bagging;

    @Override
    public String getName() {
        return name;
    }

    private Clustering[] randClusters(AbstractClusteringAlgorithm alg, Dataset<? extends Instance> dataset, Props props) {
        Clustering[] clusts = new Clustering[bagging];
        for (int i = 0; i < bagging; i++) {
            clusts[i] = alg.cluster(dataset, props);
        }
        return clusts;
    }

    @Override
    public Clustering<? extends Cluster> cluster(Dataset<? extends Instance> dataset, Props props) {
        bagging = props.getInt(BAGGING, 5);
        String initSet = props.get("init_set", "RANDOM");
        //String initSet = props.get("init_set", "MO");
        int k = props.getInt(KMeans.K);
        KMeans alg = new KMeans();

        //mapper
        Clustering[] clusts;
        switch (initSet) {
            case "RANDOM":
                clusts = randClusters(alg, dataset, props);
                break;
            case "MO":
                KmEvolution km = new KmEvolution(new ClusteringExecutorCached(alg));
                km.setDataset(dataset);
                km.addObjective(new AIC());
                km.addObjective(new SDindex());
                km.run();
                List<Solution> sol = km.getSolution();
                clusts = new Clustering[sol.size()];
                for (int i = 0; i < sol.size(); i++) {
                    clusts[i] = ((MoSolution) sol.get(i)).getClustering();
                }

            default:
                throw new RuntimeException("unknown method " + initSet);
        }

        //reducer - find consensus
        //vote about final result
        Instance curr;
        Iterator<Instance> it = clusts[0].instancesIterator();
        Cluster<? extends Instance> cluster;
        int[][] mapping = findMapping(clusts, k, alg.getDistanceFunction());

        if (colorGenerator != null) {
            colorGenerator.reset();
        }

        Clustering<? extends Cluster> res = new ClusterList(k);
        //Clustering<? extends Cluster> res = Clusterings.newList(k);
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
            if (!res.hasAt(idx)) {
                res.createCluster(idx);
                if (colorGenerator != null) {
                    res.get(idx).setColor(colorGenerator.next());
                }
            }
            //final cluster assignment
            res.get(idx).add(curr);
        }
        res.compact();

        return res;
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

    @Override
    public Clustering<? extends Cluster> partition(Dataset<? extends Instance> dataset, Props params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
