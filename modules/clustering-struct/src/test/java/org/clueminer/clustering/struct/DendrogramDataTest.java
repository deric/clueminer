package org.clueminer.clustering.struct;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.distance.api.Distance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.exception.ParserError;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.ARFFHandler;
import org.clueminer.utils.Props;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class DendrogramDataTest {

    private DendrogramData subject;
    private static final CommonFixture tf = new CommonFixture();
    private final Dataset<? extends Instance> iris;
    //private ClusteringAlgorithm algorithm = new HAC();

    public DendrogramDataTest() throws IOException {
        iris = loadIris();

    }

    private Dataset<? extends Instance> loadIris() throws FileNotFoundException, IOException {
        Dataset<? extends Instance> irisData = new ArrayDataset(150, 4);
        ARFFHandler arff = new ARFFHandler();
        try {
            arff.load(tf.irisArff(), irisData, 4);
        } catch (ParserError ex) {
            Exceptions.printStackTrace(ex);
        }
        return irisData;
    }

    @Before
    public void setUp() {
        subject = new DendrogramData();
        subject.setDataset(iris);
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
    public void testGetNumberOfRows() {
        assertEquals(150, subject.getNumberOfRows());
    }

    @Test
    public void testGetNumberOfColumns() {
        assertEquals(4, subject.getNumberOfColumns());
    }


    //@Test
    public void testGetDataset() throws IOException {
        Distance dm = DistanceFactory.getInstance().getDefault();
        Dataset<? extends Instance> dataset = loadIris();
        HierarchicalResult rowsResult = hclustRows(dataset, dm, new Props());
        DendrogramMapping mapping = new DendrogramData(dataset, rowsResult);
    }

    public HierarchicalResult hclustRows(Dataset<? extends Instance> dataset, Distance dm, Props params) {
        /*
         HierarchicalResult rowsResult = algorithm.hierarchy(dataset, params);
         CutoffStrategy strategy = getCutoffStrategy(params);
         double cut = rowsResult.findCutoff(strategy);
         params.putDouble(AgglParams.CUTOFF, cut);
         return rowsResult;*/
        return null;
    }


}
