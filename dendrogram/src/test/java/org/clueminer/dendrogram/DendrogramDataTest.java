package org.clueminer.dendrogram;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;
import org.clueminer.clustering.algorithm.DendrogramBuilder;
import org.clueminer.clustering.algorithm.HierarchicalAgglomerativeClustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.Merge;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.hclust.linkage.SingleLinkage;
import org.clueminer.io.ARFFHandler;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author deric
 */
public class DendrogramDataTest {

    protected static ClusteringAlgorithm algorithm;
    private static Dataset<? extends Instance> dataset;
    private static DendrogramData dendroData;

    public DendrogramDataTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        CommonFixture tf = new CommonFixture();
        dataset = new SampleDataset();
        ARFFHandler arff = new ARFFHandler();
        try {
            arff.load(tf.irisArff(), dataset, 4);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedAttributeType ex) {
            Exceptions.printStackTrace(ex);
        }
        algorithm = new HierarchicalAgglomerativeClustering();


        //prepare clustering
        //@TODO: this is too complex, there must be a one-line method for doing this
        Preferences pref = NbPreferences.forModule(DendrogramDataTest.class);
        Matrix input = Scaler.standartize(dataset.arrayCopy(), pref.get("std", "None"), pref.getBoolean("log-scale", false));

        HierarchicalResult rowsResult = algorithm.cluster(input, pref);
        System.out.println(rowsResult.toString());
        DendrogramBuilder db = new DendrogramBuilder();
        List<Merge> merges = db.buildDendrogram(rowsResult.getSimilarityMatrix(), new SingleLinkage(new EuclideanDistance()));        
        rowsResult.setMerges(merges);

        dendroData = new DendrogramData(dataset, input, rowsResult, null);
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
     * Test of isEmpty method, of class DendrogramData.
     */
    @Test
    public void testIsEmpty() {
        assertEquals(false, dendroData.isEmpty());
    }

    /**
     * Test of getColumnIndex method, of class DendrogramData.
     */
    @Test
    public void testGetColumnIndex() {
    }

    /**
     * Test of getRowIndex method, of class DendrogramData.
     */
    @Test
    public void testGetRowIndex() {
    }

    /**
     * Test of getNumberOfRows method, of class DendrogramData.
     */
    @Test
    public void testGetNumberOfRows() {
        assertEquals(150, dendroData.getNumberOfRows());
    }

    /**
     * Test of getNumberOfColumns method, of class DendrogramData.
     */
    @Test
    public void testGetNumberOfColumns() {
        assertEquals(4, dendroData.getNumberOfColumns());
    }

    /**
     * Test of getMatrix method, of class DendrogramData.
     */
    @Test
    public void testGetMatrix() {
    }

    /**
     * Test of setMatrix method, of class DendrogramData.
     */
    @Test
    public void testSetMatrix() {
    }

    /**
     * Test of getMinValue method, of class DendrogramData.
     */
    @Test
    public void testGetMinValue() {
    }

    /**
     * Test of getMaxValue method, of class DendrogramData.
     */
    @Test
    public void testGetMaxValue() {
    }

    /**
     * Test of getMidValue method, of class DendrogramData.
     */
    @Test
    public void testGetMidValue() {
    }

    /**
     * Test of get method, of class DendrogramData.
     */
    @Test
    public void testGet() {
    }

    /**
     * Test of getMappedValue method, of class DendrogramData.
     */
    @Test
    public void testGetMappedValue() {
    }

    /**
     * Test of getInstances method, of class DendrogramData.
     */
    @Test
    public void testGetInstances() {
    }

    /**
     * Test of setInstances method, of class DendrogramData.
     */
    @Test
    public void testSetInstances() {
    }

    /**
     * Test of getRowsResult method, of class DendrogramData.
     */
    @Test
    public void testGetRowsResult() {
    }

    /**
     * Test of setRowsResult method, of class DendrogramData.
     */
    @Test
    public void testSetRowsResult() {
    }

    /**
     * Test of getColsResult method, of class DendrogramData.
     */
    @Test
    public void testGetColsResult() {
    }

    /**
     * Test of setColsResult method, of class DendrogramData.
     */
    @Test
    public void testSetColsResult() {
    }

    /**
     * Test of getRowsClustering method, of class DendrogramData.
     */
    @Test
    public void testGetRowsClustering() {
    }

    /**
     * Test of getColumnsClustering method, of class DendrogramData.
     */
    @Test
    public void testGetColumnsClustering() {
    }

    /**
     * Test of setRowsTreeCutoffByLevel method, of class DendrogramData.
     */
    @Test
    public void testSetRowsTreeCutoffByLevel() {
    }

    /**
     * Test of setColumnsTreeCutoffByLevel method, of class DendrogramData.
     */
    @Test
    public void testSetColumnsTreeCutoffByLevel() {
    }

    /**
     * Test of hasRowsClustering method, of class DendrogramData.
     */
    @Test
    public void testHasRowsClustering() {
        assertEquals(true, dendroData.hasRowsClustering());
    }

    /**
     * Test of hasColumnsClustering method, of class DendrogramData.
     */
    @Test
    public void testHasColumnsClustering() {
        assertEquals(false, dendroData.hasColumnsClustering());
    }
}