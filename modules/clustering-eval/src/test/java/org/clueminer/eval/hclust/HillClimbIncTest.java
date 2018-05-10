/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.eval.hclust;

import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.aggl.HCLW;
import org.clueminer.clustering.aggl.linkage.CentroidLinkage;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.AIC;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HillClimbIncTest {

    private static final HillClimbInc subject = new HillClimbInc();
    private final Dataset<? extends Instance> dataset;
    private final HCLW alg;

    public HillClimbIncTest() {
        subject.setEvaluator(new AIC());
        dataset = FakeDatasets.schoolData();
        alg = new HCLW();
    }

    @Test
    public void testFindCutoff() {
        Props pref = new Props();
        pref.put(AlgParams.LINKAGE, SingleLinkage.name);
        pref.put(AlgParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        HierarchicalResult result = alg.hierarchy(dataset, pref);

        result.getTreeData().print();

        double cut = subject.findCutoff(result, pref);
        System.out.println("cutoff = " + cut);
        assertEquals(true, cut > 0);
        int numClusters = result.getClustering().size();
        System.out.println("clustering size: " + numClusters);
        assertEquals(true, numClusters == 6);
    }

    @Test
    public void testIris() {
        Dataset<Instance> data = (Dataset<Instance>) FakeDatasets.irisDataset();
        Executor<Instance, Cluster<Instance>> exec = new ClusteringExecutorCached<>();
        Props pref = new Props();
        pref.put(AlgParams.LINKAGE, CentroidLinkage.name);
        pref.put(AlgParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        pref.put(Algorithm.LOG, true);
        //TODO: this combination of parameters does not return valid clustering
        pref.put(AlgParams.STD, "Maximum");
        pref.put(AlgParams.CUTOFF_SCORE, "KsqDetW");
        Clustering<Instance, Cluster<Instance>> clust = exec.clusterRows(data, pref);
        //assertEquals(36, clust.size());
        System.out.println("clu" + clust.toString());
        assertEquals(150, clust.instancesCount());
    }

}
