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
import java.util.List;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.PartitioningClustering;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
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
        KMeans alg = new KMeans();

        //mapper
        Clustering[] clusts;

        //result store
        Clustering<? extends Cluster> res = null;
        //Clustering<? extends Cluster> res = Clusterings.newList(k);
        switch (initSet) {
            case "RANDOM":
                //map
                clusts = randClusters(alg, dataset, props);
                //reduce
                ClusteringReduce reducer = new NaiveReduce();
                res = reducer.reduce(clusts, alg, colorGenerator, props);
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
                break;
            default:
                throw new RuntimeException("unknown method " + initSet);
        }

        return res;
    }

    @Override
    public Clustering<? extends Cluster> partition(Dataset<? extends Instance> dataset, Props params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
