package org.clueminer.eval.hclust;

import java.util.prefs.Preferences;
import org.clueminer.clustering.aggl.AgglParams;
import org.clueminer.clustering.aggl.HAC;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.clustering.algorithm.HCLResult;
import org.clueminer.clustering.algorithm.HCL;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.hclust.linkage.CompleteLinkage;
import org.clueminer.math.Matrix;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.NbPreferences;

/**
 * This test was inspired by example here:
 *
 * @see
 * http://people.revoledu.com/kardi/tutorial/Clustering/Numerical%20Example.htm
 * @author Tomas Barton
 */
public class CopheneticCorrelationTest {

    private static Dataset<Instance> dataset;
    private static CopheneticCorrelation test;
    private static Preferences params;
    private static HierarchicalResult rowsResult;
    private static Matrix input;

    public CopheneticCorrelationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        test = new CopheneticCorrelation();

        int instanceCnt = 10;
        dataset = new SampleDataset<Instance>(instanceCnt);
        dataset.setName("test");
        dataset.setAttribute(0, dataset.attributeBuilder().create("X", "NUMERIC"));
        dataset.setAttribute(1, dataset.attributeBuilder().create("Y", "NUMERIC"));

        Instance i;
        i = dataset.builder().create(new double[]{1, 1});
        i.setName("A");
        dataset.add(i);
        i = dataset.builder().create(new double[]{1.5, 1.5});
        i.setName("B");
        dataset.add(i);
        i = dataset.builder().create(new double[]{5, 5});
        i.setName("C");
        dataset.add(i);
        i = dataset.builder().create(new double[]{3, 4});
        i.setName("D");
        dataset.add(i);
        i = dataset.builder().create(new double[]{4, 4});
        i.setName("E");
        dataset.add(i);
        i = dataset.builder().create(new double[]{3, 3.5});
        i.setName("F");
        dataset.add(i);

        System.out.println("dataset size " + dataset.size());

        params = getParams();
        input = new JMatrix(dataset.arrayCopy());
    }

    @After
    public void tearDown() throws Exception {
    }

    private static Preferences getParams() {
        Preferences p = NbPreferences.forModule(CopheneticCorrelationTest.class);
        // alg name
        p.put("name", "HCL");
        p.putDouble("distance-factor", 1.0);
        p.putDouble("hcl-distance-absolute", 1.0);

        p.putBoolean("calculate-experiments", true);
        p.putBoolean("optimize-rows-ordering", true);
        p.putBoolean("optimize-cols-ordering", true);
        p.putBoolean("optimize-sample-ordering", true);
        p.putBoolean("calculate-rows", true);
        return p;
    }

    /**
     * Test of getName method, of class CopheneticCorrelation.
     */
    @Test
    public void testGetName() {
        assertEquals("Cophenetic Correlation", test.getName());
    }

    /**
     * Test of score method, of class CopheneticCorrelation.
     */
    @Test
    public void testSingleLinkage() {
        AgglomerativeClustering algorithm = new HCL();
        algorithm.setDistanceFunction(new EuclideanDistance());
        params.putInt("method-linkage", -1); //-1=single, 0=complete, 1/2=average
        rowsResult = algorithm.hierarchy(input, dataset, params);
        //CPCC with single linkage
        double cpcc = test.score(rowsResult);
        System.out.println("cophenetic= " + cpcc);
        assertEquals(0.864, cpcc, 0.001);
    }

    /**
     * Test of score method, of class CopheneticCorrelation.
     *
     * @TODO according to Matlab implementation, the result should be the same -
     * 0.8640
     */
    @Test
    public void testCompleteLinkage() {
        AgglomerativeClustering algorithm = new HCL();
        algorithm.setDistanceFunction(new EuclideanDistance());
        params.putInt("method-linkage", 0); //-1=single, 0=complete, 1/2=average
        rowsResult = algorithm.hierarchy(input, dataset, params);
        //CPCC with single linkage
        double cpcc = test.score(rowsResult);
        System.out.println("cophenetic= " + cpcc);
        assertEquals(0.861, cpcc, 0.001);
    }

    /**
     * Test of score method, of class CopheneticCorrelation.
     *
     * @TODO according to Matlab implementation, the result should be 0.8658
     */
    @Test
    public void testAverageLinkage() {
        AgglomerativeClustering algorithm = new HCL();
        algorithm.setDistanceFunction(new EuclideanDistance());
        params.putInt("method-linkage", 1); //-1=single, 0=complete, 1/2=average
        rowsResult = algorithm.hierarchy(input, dataset, params);
        //CPCC with single linkage
        double cpcc = test.score(rowsResult);
        System.out.println("cophenetic= " + cpcc);
        assertEquals(0.865, cpcc, 0.001);
    }

    /**
     * Test of getCopheneticMatrix method, of class CopheneticCorrelation.
     *
     * test data taken from
     *
     * @see http://people.revoledu.com/kardi/tutorial/Clustering/Cophenetic.htm
     */
    @Test
    public void testGetCopheneticMatrix() {
        AgglomerativeClustering algorithm = new HCL();
        algorithm.setDistanceFunction(new EuclideanDistance());
        params.putInt("method-linkage", -1); //-1=single, 0=complete, 1/2=average
        rowsResult = algorithm.hierarchy(input, dataset, params);
        double precision = 0.01;
        Matrix proximity = rowsResult.getProximityMatrix();
        HCLResult r = (HCLResult) rowsResult;
        double[][] copheneticMatrix = test.getCopheneticMatrixOld(r.getTreeData(), proximity.rowsCount(), proximity.columnsCount());
        //symetrical matrix
        assertEquals(copheneticMatrix[1][2], copheneticMatrix[2][1], precision);
        //axis is equal to 0.0 (distance to itself)
        for (int i = 0; i < copheneticMatrix.length; i++) {
            assertEquals(copheneticMatrix[i][i], 0.0, precision);
        }

        //we expect this matrix
        //0.00  0.71  2.50  2.50  2.50  2.50
        //0.71  0.00  2.50  2.50  2.50  2.50
        //2.50  2.50  0.00  1.41  1.41  1.41
        //2.50  2.50  1.41  0.00  1.00  0.50FF
        //2.50  2.50  1.41  1.00  0.00  1.00
        //2.50  2.50  1.41  0.50  1.00  0.00
        assertEquals(0.71, copheneticMatrix[0][1], precision);
        assertEquals(2.50, copheneticMatrix[0][2], precision);
        assertEquals(1.41, copheneticMatrix[2][3], precision);
        assertEquals(0.50, copheneticMatrix[5][3], precision);
        assertEquals(1.00, copheneticMatrix[5][4], precision);

        //  Covariance cov = new Covariance();
    }

    /**
     * New tree structure
     */
    @Test
    public void testGetCopheneticMatrixTreeStuct() {
        AgglomerativeClustering algorithm = new HAC();
        algorithm.setDistanceFunction(new EuclideanDistance());
        params.put(AgglParams.LINKAGE, CompleteLinkage.name);
        rowsResult = algorithm.hierarchy(input, dataset, params);
        double precision = 0.01;
        Matrix proximity = rowsResult.getProximityMatrix();
        assertNotNull(rowsResult);
        DendroTreeData tree = rowsResult.getTreeData();
        assertNotNull(tree);
        assertEquals(6, proximity.rowsCount());
        assertEquals(6, proximity.columnsCount());
        double[][] copheneticMatrix = test.getCopheneticMatrix(rowsResult.getTreeData(), proximity.rowsCount(), proximity.columnsCount());
        //symetrical matrix
        assertEquals(copheneticMatrix[1][2], copheneticMatrix[2][1], precision);
        //axis is equal to 0.0 (distance to itself)
        for (int i = 0; i < copheneticMatrix.length; i++) {
            assertEquals(copheneticMatrix[i][i], 0.0, precision);
        }

        //we expect this matrix
        //0.00  0.71  2.50  2.50  2.50  2.50
        //0.71  0.00  2.50  2.50  2.50  2.50
        //2.50  2.50  0.00  1.41  1.41  1.41
        //2.50  2.50  1.41  0.00  1.00  0.50
        //2.50  2.50  1.41  1.00  0.00  1.00
        //2.50  2.50  1.41  0.50  1.00  0.00
        assertEquals(0.71, copheneticMatrix[0][1], precision);
        assertEquals(2.50, copheneticMatrix[0][2], precision);
        assertEquals(1.41, copheneticMatrix[2][3], precision);
        assertEquals(0.50, copheneticMatrix[5][3], precision);
        assertEquals(1.00, copheneticMatrix[5][4], precision);

        //  Covariance cov = new Covariance();
    }

    /**
     * Test of copheneticCoefficient method, of class CopheneticCorrelation.
     */
    @Test
    public void testCopheneticCoefficient() {
    }

    /**
     * Test of score method, of class CopheneticCorrelation.
     */
    @Test
    public void testScore() {
    }
}
