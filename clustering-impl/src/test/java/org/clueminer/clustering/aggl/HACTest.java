package org.clueminer.clustering.aggl;

import java.io.IOException;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.hclust.linkage.SingleLinkage;
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
public class HACTest {

    private static final HAC subject = new HAC();

    public HACTest() {
    }

    @BeforeClass
    public static void setUpClass() {

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
     * Test of getName method, of class HC1.
     */
    @Test
    public void testGetName() {
        assertNotNull(subject.getName());
    }

    /**
     * Test of cluster method, of class HC1.
     */
    @Test
    public void testCluster_Matrix_Preferences() {
    }

    /**
     * Test of hierarchy method, of class HC1.
     */
    @Test
    public void testHierarchy_Dataset_Preferences() {
    }

    /**
     * Test of hierarchy method, of class HC1.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testHierarchy_3args() throws IOException {
        Dataset<? extends Instance> dataset = FakeDatasets.schoolData();
        assertEquals(17, dataset.size());
        assertEquals(4, dataset.attributeCount());
        Matrix input = new JMatrix(dataset.arrayCopy());
        input.print(3, 2);
        Preferences pref = NbPreferences.forModule(HACTest.class);
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        HierarchicalResult result = subject.hierarchy(input, dataset, pref);
        Matrix similarityMatrix = result.getSimilarityMatrix();
        assertNotNull(similarityMatrix);
        assertEquals(similarityMatrix.rowsCount(), dataset.size());

        similarityMatrix.printLower(5, 2);
        result.getTreeData().print();
        Matrix proximity = result.getProximityMatrix();
        assertNotNull(proximity);
        assertEquals(dataset.size(), proximity.rowsCount());
        assertEquals(dataset.size(), proximity.columnsCount());
    }

    @Test
    public void testColumnClustering() throws IOException {
        Dataset<? extends Instance> dataset = FakeDatasets.schoolData();
        Matrix input = new JMatrix(dataset.arrayCopy());
        input.print(3, 2);
        Preferences pref = NbPreferences.forModule(HACTest.class);
        pref.put(AgglParams.LINKAGE, SingleLinkage.name);
        pref.putBoolean(AgglParams.CLUSTER_ROWS, false);
        HierarchicalResult result = subject.hierarchy(input, dataset, pref);
        Matrix similarityMatrix = result.getSimilarityMatrix();
        assertNotNull(similarityMatrix);
        assertEquals(similarityMatrix.rowsCount(), dataset.attributeCount());
        assertEquals(similarityMatrix.columnsCount(), dataset.attributeCount());

        result.getTreeData().print();
        Matrix proximity = result.getProximityMatrix();
        assertNotNull(proximity);
        assertEquals(dataset.attributeCount(), proximity.rowsCount());
        assertEquals(dataset.attributeCount(), proximity.columnsCount());

    }

    /**
     * Test of hierarchy method, of class HC1.
     */
    @Test
    public void testHierarchy_Matrix_Preferences() {
    }

    /**
     * Test of cluster method, of class HC1.
     */
    @Test
    public void testCluster_Dataset() {
    }

}
