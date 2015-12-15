package org.clueminer.eval.hclust;

import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.aggl.HCLW;
import org.clueminer.clustering.aggl.linkage.CentroidLinkage;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.AgglParams;
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
import org.junit.Test;
import static org.junit.Assert.*;

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
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
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
        pref.put(AgglParams.LINKAGE, CentroidLinkage.name);
        pref.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        pref.put(Algorithm.LOG, true);
        //TODO: this combination of parameters does not return valid clustering
        pref.put(AgglParams.STD, "Maximum");
        pref.put(AgglParams.CUTOFF_SCORE, "KsqDetW");
        Clustering<Instance, Cluster<Instance>> clust = exec.clusterRows(data, pref);
        //assertEquals(36, clust.size());
        //wtf?
        System.out.println("clu" + clust.toString());
        assertEquals(5, clust.instancesCount());
    }

}
