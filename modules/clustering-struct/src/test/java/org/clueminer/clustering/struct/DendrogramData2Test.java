package org.clueminer.clustering.struct;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.clueminer.utils.Props;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DendrogramData2Test {

    private DendrogramData2 subject;
    private static final CommonFixture tf = new CommonFixture();
    private final Dataset<? extends Instance> iris;
    //private ClusteringAlgorithm algorithm = new HAC();

    public DendrogramData2Test() throws IOException {
        iris = loadIris();

    }

    private Dataset<? extends Instance> loadIris() throws FileNotFoundException, IOException {
        Dataset<? extends Instance> irisData = new ArrayDataset(150, 4);
        ARFFHandler arff = new ARFFHandler();
        arff.load(tf.irisArff(), irisData, 4);
        return irisData;
    }

    @Before
    public void setUp() {
        subject = new DendrogramData2();
        subject.setDataset(iris);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSetDataset() {
        assertEquals(iris, subject.getDataset());
    }

    @Test
    public void testIsEmpty() {
        assertFalse(subject.isEmpty());
    }

    @Test
    public void testGetColumnIndex() {
    }

    @Test
    public void testGetRowIndex() {
    }

    @Test
    public void testGetNumberOfRows() {
        assertEquals(150, subject.getNumberOfRows());
    }

    @Test
    public void testGetNumberOfColumns() {
        assertEquals(4, subject.getNumberOfColumns());
    }

    @Test
    public void testGetMatrix() {
    }

    @Test
    public void testSetMatrix() {
    }

    @Test
    public void testGetMinValue() {
    }

    @Test
    public void testGetMaxValue() {
    }

    @Test
    public void testGetMidValue() {
    }

    @Test
    public void testGet() {

    }

    @Test
    public void testGetMappedValue() {
    }

    // @Test
    public void testGetDataset() throws IOException {
        DistanceMeasure dm = DistanceFactory.getInstance().getDefault();
        Dataset<? extends Instance> dataset = loadIris();
        HierarchicalResult rowsResult = hclustRows(dataset, dm, new Props());
        DendrogramMapping mapping = new DendrogramData2(dataset, rowsResult);
    }

    public HierarchicalResult hclustRows(Dataset<? extends Instance> dataset, DistanceMeasure dm, Props params) {
        /*
         HierarchicalResult rowsResult = algorithm.hierarchy(dataset, params);
         CutoffStrategy strategy = getCutoffStrategy(params);
         double cut = rowsResult.findCutoff(strategy);
         params.putDouble(AgglParams.CUTOFF, cut);
         return rowsResult;*/
        return null;
    }

    @Test
    public void testGetRowsResult() {
    }

    @Test
    public void testSetRowsResult() {
    }

    @Test
    public void testGetColsResult() {
    }

    @Test
    public void testSetColsResult() {
    }

    @Test
    public void testGetRowsClustering() {
    }

    @Test
    public void testGetColumnsClustering() {
    }

    @Test
    public void testSetRowsTreeCutoffByLevel() {
    }

    @Test
    public void testSetColumnsTreeCutoffByLevel() {
    }

    @Test
    public void testHasRowsClustering() {
    }

    @Test
    public void testHasColumnsClustering() {
    }

    @Test
    public void testPrintMappedMatix() {
    }

    @Test
    public void testPrintMatrix() {
    }

}
