package org.clueminer.dataset.impl;

import org.clueminer.dataset.impl.AttrHashDataset;
import java.util.HashMap;
import java.util.Map;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.Instance;
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
public class AttrHashDatasetTest {

    private static AttrHashDataset dataset;
    private AttributeBuilder builder;
    private static final double delta = 1e-7;

    public AttrHashDatasetTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        dataset = new AttrHashDataset<>(10);
        this.builder = dataset.attributeBuilder();
        builder.create("first", BasicAttrType.NUMERICAL);
        builder.create("second", BasicAttrType.NUMERICAL);
        builder.create("third", BasicAttrType.NUMERICAL);
        //dataset.attributeBuilder().create("class", DataTypes.CLASS_VALUE);

        dataset.builder().create(new double[]{0.1, 0.8, 3});
        dataset.builder().create(new double[]{0.5, 3.0, 8});
        dataset.builder().create(new double[]{0.3, 3.2, 12});
        dataset.builder().create(new double[]{0.1, 4.0, 15});

        dataset.setName("test dataset");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSize() {
        assertEquals(4, dataset.size());
    }

    /**
     * Test of setAttribute method, of class AttrHashDataset.
     */
    @Test
    public void testSetAttribute() {
        dataset.setAttribute(3, builder.create("foo", BasicAttrType.NUMERIC));
        assertEquals(4, dataset.attributeCount());
    }

    /**
     * Test of setAttributes method, of class AttrHashDataset.
     */
    @Test
    public void testSetAttributes() {
        Map<Integer, Attribute> attr = new HashMap<>();
        attr.put(3, builder.build("foo", BasicAttrType.NUMERIC));
        attr.put(4, builder.build("bar", BasicAttrType.NUMERIC));
        attr.put(5, builder.build("oogh", BasicAttrType.NUMERIC));
        assertEquals(3, attr.size());
        dataset.setAttributes(attr);
        assertEquals(6, dataset.attributeCount());
    }

    @Test
    public void testSetValuesForNewAttributes() {
        Map<Integer, Attribute> attr = new HashMap<>();
        attr.put(3, builder.create("foo", BasicAttrType.NUMERIC));
        attr.put(4, builder.create("bar", BasicAttrType.NUMERIC));
        dataset.setAttributes(attr);
        double value = 456.0;
        dataset.setAttributeValue("foo", 0, value);
        assertEquals(value, dataset.getAttributeValue("foo", 0), delta);
    }

    /**
     * Test of getAttributeValue method, of class AttrHashDataset.
     *
     * Getting value from dataset by name of an attribute
     *
     */
    @Test
    public void testGetAttributeValue() {
        assertEquals(0.1, dataset.getAttributeValue("first", 0), delta);
        assertEquals(3.0, dataset.getAttributeValue("second", 1), delta);
    }

    /**
     * Test of setAttributeValue method, of class AttrHashDataset.
     */
    @Test
    public void testSetAttributeValue() {
        double value = 42;
        dataset.setAttributeValue("first", 0, value);
        assertEquals(value, dataset.getAttributeValue("first", 0), delta);
    }

    /**
     * Test of getAttribute method, of class AttrHashDataset.
     */
    @Test
    public void testGetAttribute() {
    }

    /**
     * Test of duplicate method, of class AttrHashDataset.
     */
    @Test
    public void testDuplicate() {
    }

}
