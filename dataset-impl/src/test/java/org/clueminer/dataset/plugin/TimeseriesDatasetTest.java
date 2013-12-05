package org.clueminer.dataset.plugin;

import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.row.TimeInstance;
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
public class TimeseriesDatasetTest {

    private static TimeseriesDataset<ContinuousInstance> dataset;
    private static final double delta = 1e-9;

    public TimeseriesDatasetTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
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

    @After
    public void tearDown() {
    }

    /**
     * Test of attributeCount method, of class TimeseriesDataset.
     */
    @Test
    public void testAttributeCount() {
    }

    /**
     * Test of getAttribute method, of class TimeseriesDataset.
     */
    @Test
    public void testGetAttribute_int() {
    }

    /**
     * Test of setAttribute method, of class TimeseriesDataset.
     */
    @Test
    public void testSetAttribute() {
    }

    /**
     * Test of setAttributes method, of class TimeseriesDataset.
     */
    @Test
    public void testSetAttributes() {
    }

    /**
     * Test of add method, of class TimeseriesDataset.
     */
    @Test
    public void testAdd() {
        int size = dataset.size();
        dataset.add(new TimeInstance(dataset.attributeCount()));
        assertEquals(size + 1, dataset.size());
    }

    /**
     * Test of addAll method, of class TimeseriesDataset.
     */
    @Test
    public void testAddAll() {
    }

    /**
     * Test of check method, of class TimeseriesDataset.
     */
    @Test
    public void testCheck() {
    }

    /**
     * Test of getAttributes method, of class TimeseriesDataset.
     */
    @Test
    public void testGetAttributes() {
    }

    /**
     * Test of copyAttributes method, of class TimeseriesDataset.
     */
    @Test
    public void testCopyAttributes() {
    }

    /**
     * Test of getTimePoints method, of class TimeseriesDataset.
     */
    @Test
    public void testGetTimePoints() {
    }

    /**
     * Test of getTimePointsArray method, of class TimeseriesDataset.
     */
    @Test
    public void testGetTimePointsArray() {
    }

    /**
     * Test of setTimePoints method, of class TimeseriesDataset.
     */
    @Test
    public void testSetTimePoints() {
    }

    /**
     * Test of crop method, of class TimeseriesDataset.
     */
    @Test
    public void testCrop() {
    }

    /**
     * Test of checkMinMax method, of class TimeseriesDataset.
     */
    @Test
    public void testCheckMinMax() {
        double[] data = new double[dataset.attributeCount()];
        for (int j = 0; j < data.length; j++) {
            data[j] = Math.random();
        }
        double max = Math.random() * 100000;
        data[0] = max;
        dataset.add((ContinuousInstance) dataset.builder().create(data));
        assertEquals(max, dataset.getMax(), delta);
    }

    /**
     * Test of resetMinMax method, of class TimeseriesDataset.
     */
    @Test
    public void testResetMinMax() {
    }

    /**
     * Test of interpolate method, of class TimeseriesDataset.
     */
    @Test
    public void testInterpolate() {
    }

    /**
     * Test of getMin method, of class TimeseriesDataset.
     */
    @Test
    public void testGetMinMax() {
        double min = dataset.getMin();
        double max = dataset.getMax();
        assertEquals(true, max >= min);
    }

    /**
     * Test of equals method, of class TimeseriesDataset.
     */
    @Test
    public void testEquals() {
    }

    /**
     * Test of hashCode method, of class TimeseriesDataset.
     */
    @Test
    public void testHashCode() {
    }

    /**
     * Test of toString method, of class TimeseriesDataset.
     */
    @Test
    public void testToString() {
    }

    /**
     * Test of getClasses method, of class TimeseriesDataset.
     */
    @Test
    public void testGetClasses() {
    }

    /**
     * Test of classIndex method, of class TimeseriesDataset.
     */
    @Test
    public void testClassIndex() {
    }

    /**
     * Test of classValue method, of class TimeseriesDataset.
     */
    @Test
    public void testClassValue() {
    }

    /**
     * Test of builder method, of class TimeseriesDataset.
     */
    @Test
    public void testBuilder() {
    }

    /**
     * Test of attributeBuilder method, of class TimeseriesDataset.
     */
    @Test
    public void testAttributeBuilder() {
    }

    /**
     * Test of copy method, of class TimeseriesDataset.
     */
    @Test
    public void testCopy() {
    }

    /**
     * Test of instance method, of class TimeseriesDataset.
     */
    @Test
    public void testInstance() {
        assertNotNull(dataset.instance(0));
        assertNotNull(dataset.instance(1));
        assertNotNull(dataset.instance(dataset.size() - 1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testInstanceException() {
        dataset.instance(-1);
    }

    /**
     * Test of getRandom method, of class TimeseriesDataset.
     */
    @Test
    public void testGetRandom() {
    }

    /**
     * Test of getAttributeValue method, of class TimeseriesDataset.
     */
    @Test
    public void testGetAttributeValue_String_int() {
    }

    /**
     * Test of getAttributeValue method, of class TimeseriesDataset.
     */
    @Test
    public void testGetAttributeValue_Attribute_int() {
    }

    /**
     * Test of getAttributeValue method, of class TimeseriesDataset.
     */
    @Test
    public void testGetAttributeValue_int_int() {
    }

    /**
     * Test of setAttributeValue method, of class TimeseriesDataset.
     */
    @Test
    public void testSetAttributeValue() {
    }

    /**
     * Test of getPlotter method, of class TimeseriesDataset.
     */
    @Test
    public void testGetPlotter() {
    }

    /**
     * Test of getAttribute method, of class TimeseriesDataset.
     */
    @Test
    public void testGetAttribute_String() {
    }

    /**
     * Test of duplicate method, of class TimeseriesDataset.
     */
    @Test
    public void testDuplicate() {
    }
}
