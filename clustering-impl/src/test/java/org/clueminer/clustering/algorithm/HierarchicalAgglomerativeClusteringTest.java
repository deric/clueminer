package org.clueminer.clustering.algorithm;

import java.util.List;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.Merge;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.hclust.linkage.CompleteLinkage;
import org.clueminer.hclust.linkage.SingleLinkage;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.utils.Dump;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.NbPreferences;

/**
 *
 * @author deric
 */
public class HierarchicalAgglomerativeClusteringTest {

    private static HierarchicalAgglomerativeClustering alg = new HierarchicalAgglomerativeClustering();
    private static Matrix similarityMatrix;

    public HierarchicalAgglomerativeClusteringTest() {
    }

    @BeforeClass
    public static void setUpClass() {


        /**
         * @see
         * http://home.dei.polimi.it/matteucc/Clustering/tutorial_html/hierarchical.html
         */
        double[][] data = {
            {0.0, 662, 877, 255, 412, 996},
            {662, 0.0, 295, 468, 268, 400},
            {877, 295, 0.0, 754, 564, 138},
            {255, 468, 754, 0.0, 219, 869},
            {412, 268, 564, 219, 0.0, 669},
            {996, 400, 138, 869, 669, 0.0}
        };

        similarityMatrix = new JMatrix(data);
        similarityMatrix.print(3, 0);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of partition method, of class HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testPartition_Dataset() {
    }

    /**
     * Test of partition method, of class HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testPartition_Dataset_AlgorithmParameters() {
    }

    /**
     * Test of hierarchy method, of class HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testHierarchy_Dataset_AlgorithmParameters() {
    }

    /**
     * Test of hierarchy method, of class HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testHierarchy_3args() {
    }

    /**
     * Test of getLinkage method, of class HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testGetLinkage() {
    }

    /**
     * Test of setLinkage method, of class HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testSetLinkage() {
    }

    /**
     * Test of cluster method, of class HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testCluster_Matrix_Preferences() {
    }

    /**
     * Test of cluster method, of class HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testCluster_3args() {
        ClusterLinkage linkage = new CompleteLinkage(new EuclideanDistance());
        alg.setLinkage(linkage);
        //Preferences pref = NbPreferences.forModule(HierarchicalAgglomerativeClustering.class);
        HierarchicalResult rowsResult = HierarchicalAgglomerativeClustering.clusterSimilarityMatrix(similarityMatrix, -1, linkage, -1);
        Dump.array(rowsResult.getMapping(), "assignments");

        DendrogramBuilder db = new DendrogramBuilder();
        List<Merge> merges = db.buildDendrogram(rowsResult.getSimilarityMatrix(), linkage);
        rowsResult.setMerges(merges);
        System.out.println("merges " + merges.toString());

//        res.get
        //res.get
        //Dump.array(a, null);
    }

    /**
     * Test of partitionRows method, of class
     * HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testPartitionRows() {
    }

    /**
     * Test of clusterRows method, of class HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testClusterRows() {
    }

    /**
     * Test of findMostSimilar method, of class
     * HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testFindMostSimilar() {
    }

    /**
     * Test of generateInitialAssignment method, of class
     * HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testGenerateInitialAssignment() {
    }

    /**
     * Test of computeRowSimilarityMatrix method, of class
     * HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testComputeRowSimilarityMatrix() {
    }

    /**
     * Test of computeColumnsSimilarityMatrix method, of class
     * HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testComputeColumnsSimilarityMatrix() {
    }

    /**
     * Test of cluster method, of class HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testCluster_5args() {
    }

    /**
     * Test of clusterSimilarityMatrix method, of class HierarchicalAgglomerativeClustering.
     */
    @Test
    public void testClusterSimilarityMatrix() {
    }
}