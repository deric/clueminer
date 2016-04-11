package org.clueminer.chart;

import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.impl.TimeseriesDataset;
import org.clueminer.types.TimePoint;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author deric
 */
public class ChartDataImplTest {

    private static TimeseriesDataset<ContinuousInstance> dataset;
    private static final double delta = 1e-9;
    private ChartDataImpl subject;

    public ChartDataImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        dataset = new TimeseriesDataset<ContinuousInstance>(5);
        TimePoint tp[] = new TimePointAttribute[6];
        for (int i = 0; i < tp.length; i++) {
            tp[i] = new TimePointAttribute(i, i + 100, Math.pow(i, 2));
        }
        dataset.setTimePoints(tp);
        InstanceBuilder builder = dataset.builder();
        double[] data;
        int size = 10;
        for (int i = 0; i < size; i++) {
            data = new double[tp.length];
            for (int j = 0; j < data.length; j++) {
                data[j] = Math.random();
            }
            dataset.add((ContinuousInstance) builder.create(data));
        }
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
     * Test of setVisible method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetVisible() {
        subject = new ChartDataImpl(dataset);
        //position should be between 0 and 1
        assertEquals(0.0, subject.getFirstTimePoint().getPosition(), delta);
        assertEquals(1.0, subject.getLastTimePoint().getPosition(), delta);
    }

    /**
     * Test of getName method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetName() {
    }

    /**
     * Test of setName method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetName() {
    }

    /**
     * Test of size method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSize() {
    }

    /**
     * Test of updateTimePoints method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testTimePointsUpdated() {
    }

    /**
     * Test of getPositionAt method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetPositionAt() {
    }

    /**
     * Test of setStart method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetStart() {
    }

    /**
     * Test of setStartX method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetStartX() {
    }

    /**
     * Test of getStart method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetStart() {
    }

    /**
     * Test of getFinishX method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetFinishX() {
    }

    /**
     * Test of setFinish method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetFinish_int_double() {
    }

    /**
     * Test of setFinish method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetFinish_int_Rectangle() {
    }

    /**
     * Test of getDisplayedLast method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetDisplayedLast() {
    }

    /**
     * Test of getTimePointsCnt method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetTimePointsCnt() {
    }

    /**
     * Test of getLast method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetLast() {
    }

    /**
     * Test of isSampleSetNull method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testIsSampleSetNull() {
    }

    /**
     * Test of getVisible method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetVisible() {
    }

    /**
     * Test of clearVisible method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testClearVisible() {
    }


    /**
     * Test of getTimePoint method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetTimePoint() {
    }

    /**
     * Test of getTimeAt method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetTimeAt() {
    }

    /**
     * Test of getFirstTime method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetFirstTime() {
    }

    /**
     * Test of updateLastX method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testUpdateLastX() {
    }

    /**
     * Test of getLastX method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetLastX() {
    }

    /**
     * Test of normalize method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testNormalize() {
    }

    /**
     * Test of crop method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testCrop() {
    }

    /**
     * Test of setLastTimePoint method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetLastTimePoint() {
    }

    /**
     * Test of getLastTimePoint method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetLastTimePoint() {
    }

    /**
     * Test of setVisibleRange method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetVisibleRange() {
    }

    /**
     * Test of checkMax method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testCheckMax() {
    }

    /**
     * Test of checkMin method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testCheckMin() {
    }

    /**
     * Test of setMin method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetMin() {
    }

    /**
     * Test of setMax method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetMax() {
    }

    /**
     * Test of getVisibleRange method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetVisibleRange() {
    }

    /**
     * Test of isVisibleNull method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testIsVisibleNull() {
    }

    /**
     * Test of getFinish method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetFinish() {
    }

    /**
     * Test of getChart method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetChart() {
    }

    /**
     * Test of setChart method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetChart() {
    }

    /**
     * Test of isChartNull method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testIsChartNull() {
    }

    /**
     * Test of getDateValues method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetDateValues() {
    }

    /**
     * Test of getMinY method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetMinY() {
    }

    /**
     * Test of getMaxY method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetMaxY() {
    }

    /**
     * Test of getStartX method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetStartX() {
    }

    /**
     * Test of getIndexValues method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetIndexValues() {
    }

    /**
     * Test of getX method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetX_int_Rectangle() {
    }

    /**
     * Test of getX method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetX_3args() {
    }

    /**
     * Test of getPoint method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetPoint_4args() {
    }

    /**
     * Test of getPoint method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetPoint_5args() {
    }

    /**
     * Test of xFromTime method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testXFromTime() {
    }

    /**
     * Test of getXFromRatio method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetXFromRatio_3args() {
    }

    /**
     * Test of getXFromRatio method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetXFromRatio_double_Rectangle() {
    }

    /**
     * Test of getXFromTime method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetXFromTime() {
    }

    /**
     * Test of getY method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetY_3args() {
    }

    /**
     * Test of getY method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetY_4args() {
    }

    /**
     * Test of getIndex method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetIndex_int_Rectangle() {
    }

    /**
     * Test of getIndex method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetIndex_3args() {
    }

    /**
     * Test of revertXtoRatio method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testRevertXtoRatio() {
    }

    /**
     * Test of getIndex method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetIndex_Point_Rectangle() {
    }

    /**
     * Test of findIndex method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testFindIndex() {
    }

    /**
     * Test of interpolate method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testInterpolate() {
    }

    /**
     * Test of findAbsoluteIndex method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testFindAbsoluteIndex() {
    }

    /**
     * Test of getTimeFromX method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetTimeFromX() {
    }

    /**
     * Test of calculateRange method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testCalculateRange() {
    }

    /**
     * Test of addDatasetListener method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testAddDatasetListener() {
    }

    /**
     * Test of removeDatasetListeners method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testRemoveDatasetListeners() {
    }

    /**
     * Test of removeAllDatasetListeners method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testRemoveAllDatasetListeners() {
    }

    /**
     * Test of addIndicatorsDatasetListeners method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testAddIndicatorsDatasetListeners() {
    }

    /**
     * Test of removeIndicatorsDatasetListeners method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testRemoveIndicatorsDatasetListeners() {
    }

    /**
     * Test of removeAllIndicatorsDatasetListeners method, of class
     * ChartDataImpl.
     */
    @org.junit.Test
    public void testRemoveAllIndicatorsDatasetListeners() {
    }

    /**
     * Test of addOverlaysDatasetListeners method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testAddOverlaysDatasetListeners() {
    }

    /**
     * Test of removeOverlaysDatasetListeners method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testRemoveOverlaysDatasetListeners() {
    }

    /**
     * Test of removeAllOverlaysDatasetListeners method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testRemoveAllOverlaysDatasetListeners() {
    }

    /**
     * Test of eventListenerList method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testEventListenerList() {
    }

    /**
     * Test of removeMarkerListener method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testRemoveMarkerListener() {
    }

    /**
     * Test of fireMarkerMoved method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testFireMarkerMoved() {
    }

    /**
     * Test of addNormalizationListener method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testAddNormalizationListener() {
    }

    /**
     * Test of fireDataNormalized method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testFireDataNormalized() {
    }

    /**
     * Test of fireDatasetCropped method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testFireDatasetCropped() {
    }

    /**
     * Test of fireDatasetChanged method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testFireDatasetChanged() {
    }

    /**
     * Test of setSavedOverlays method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetSavedOverlays() {
    }

    /**
     * Test of getSavedOverlays method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetSavedOverlays() {
    }

    /**
     * Test of clearSavedOverlays method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testClearSavedOverlays() {
    }

    /**
     * Test of setAnnotationsCount method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetAnnotationsCount() {
    }

    /**
     * Test of getAnnotationsCount method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetAnnotationsCount() {
    }

    /**
     * Test of clearAnnotationsCount method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testClearAnnotationsCount() {
    }

    /**
     * Test of setAnnotations method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetAnnotations() {
    }

    /**
     * Test of getAnnotations method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetAnnotations() {
    }

    /**
     * Test of clearAnnotations method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testClearAnnotations() {
    }

    /**
     * Test of chartChanged method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testChartChanged() {
    }

    /**
     * Test of overlayAdded method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testOverlayAdded() {
    }

    /**
     * Test of overlayRemoved method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testOverlayRemoved() {
    }

    /**
     * Test of zoomIn method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testZoomIn() {
    }

    /**
     * Test of zoomOut method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testZoomOut() {
    }

    /**
     * Test of getZoom method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetZoom() {
    }

    /**
     * Test of setZoom method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testSetZoom() {
    }

    /**
     * Test of equals method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testEquals() {
    }

    /**
     * Test of hashCode method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testHashCode() {
    }

    /**
     * Test of getDataInsets method, of class ChartDataImpl.
     */
    @org.junit.Test
    public void testGetDataInsets() {
    }

}
