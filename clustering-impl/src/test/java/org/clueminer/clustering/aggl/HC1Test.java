package org.clueminer.clustering.aggl;

import java.io.IOException;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.CsvLoader;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JMatrix;
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
public class HC1Test {

    private static final HC1 subject = new HC1();
    private static final CommonFixture fixture = new CommonFixture();

    public HC1Test() {
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
     */
    @Test
    public void testHierarchy_3args() throws IOException {
        CsvLoader loader = new CsvLoader();
        Dataset<? extends Instance> dataset = new ArrayDataset(17, 4);

        loader.setClassIndex(4);
        //loader.addMetaAttr(4);
        ///loader.setSkipIndex(skip);
        //loader.setSkipHeader(true);
        loader.setSeparator(' ');
        loader.load(fixture.schoolData(), dataset);
        assertEquals(17, dataset.size());
        System.out.println("attr cnt: " + dataset.attributeCount());
        Matrix input = new JMatrix(dataset.arrayCopy());
        input.print(3, 2);
        HierarchicalResult result = subject.hierarchy(input, dataset, NbPreferences.forModule(HC1Test.class));

        assertNotNull(result.getSimilarityMatrix());
        assertEquals(result.getSimilarityMatrix().rowsCount(), dataset.size());

        System.out.println("single linkage");
        result.getTreeData().print();
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
