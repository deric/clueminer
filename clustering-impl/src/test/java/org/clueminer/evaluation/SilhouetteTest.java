package org.clueminer.evaluation;

import org.clueminer.cluster.FakeClustering;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.clustering.algorithm.HCL;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.AlgorithmParameters;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class SilhouetteTest {

    private static Silhouette test = new Silhouette();
    private static Dataset<Instance> dataset;
    private static Clustering clustering;
    private static Clustering clusters;
    private static AlgorithmParameters params;
    private static HierarchicalResult rowsResult;
    private static Matrix input;
    private static double delta = 1e-9;

    public SilhouetteTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        clusters = FakeClustering.iris();
        dataset = FakeClustering.irisDataset();

        params = getParams();
        input = new JMatrix(dataset.arrayCopy());

      /*  ClusteringAlgorithm algorithm = new HCL();
        algorithm.setDistanceFunction(new EuclideanDistance());
        params.setProperty("method-linkage", String.valueOf(0)); //-1=single, 0=complete, 1/2=average
        rowsResult = algorithm.hierarchy(input, dataset, params);*/
        ///clustering = rowsResult.getClustering(dendroData.getRowsMapping(), dataset);
    }

    private static AlgorithmParameters getParams() {
        AlgorithmParameters p = new AlgorithmParameters();
        // alg name
        p.setProperty("name", "HCL");
        p.setProperty("distance-factor", "1.0");
        p.setProperty("hcl-distance-absolute", "1.0");

        p.setProperty("calculate-experiments", String.valueOf(true));
        p.setProperty("optimize-rows-ordering", String.valueOf(true));
        p.setProperty("optimize-cols-ordering", String.valueOf(true));
        p.setProperty("optimize-sample-ordering", String.valueOf(true));
        p.setProperty("calculate-rows", String.valueOf(true));
        return p;
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getName method, of class Silhouette.
     */
    @Test
    public void testGetName() {
    }

    /**
     * Test of score method, of class Silhouette.
     */
    //@Test
    public void testScore_Clustering() {
        double score;
        long start = System.currentTimeMillis();
        score = test.score(clustering, dataset);
        System.out.println("Silhouette= " + score);
        long end = System.currentTimeMillis();
        assertTrue(score != Double.NaN);
        System.out.println("computing took = " + (end - start) + " ms");
    }

    /**
     * Test of score method, of class Silhouette.
     */
    @Test
    public void testScore_ClusteringMatlab() {
        double score;
        long start = System.currentTimeMillis();
        score = test.score(clusters, dataset);
        System.out.println("Silhouette= " + score);
        double matlab = 0.6567;
        long end = System.currentTimeMillis();
        assertTrue(score != Double.NaN);
       /**
        * @TODO fix this
        */
       // assertEquals(matlab, score, delta);
        System.out.println("computing took = " + (end - start) + " ms");
    }

    /**
     * Test of score method, of class Silhouette.
     */
    @Test
    public void testScore_Clustering_Matrix() {
    }

    /**
     * Test of compareScore method, of class Silhouette.
     */
    @Test
    public void testCompareScore() {
    }
}