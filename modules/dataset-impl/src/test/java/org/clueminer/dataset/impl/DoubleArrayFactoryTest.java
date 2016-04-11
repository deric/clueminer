package org.clueminer.dataset.impl;

import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DoubleArrayFactoryTest {

    private DoubleArrayFactory subject;
    private Dataset<? extends Instance> dataset;
    private static final double DELTA = 1e-9;

    @Before
    public void setUp() {
        dataset = new ArrayDataset<>(2, 2);
        dataset.attributeBuilder().create("x", BasicAttrType.NUMERIC);
        dataset.attributeBuilder().create("y", BasicAttrType.NUMERIC);
        subject = new DoubleArrayFactory(dataset);
    }

    @Test
    public void testCreate_doubleArr() {
        assertEquals(0, dataset.size());
        subject.create(new double[]{1.0, 2.0});
        assertEquals(1, dataset.size());
        assertEquals(1.0, dataset.instance(0).get(0), DELTA);
        assertEquals(2.0, dataset.instance(0).get(1), DELTA);
    }

    @Test
    public void testBuild_doubleArr() {
    }

    @Test
    public void testCreate_doubleArr_String() {
        Instance inst = subject.create(new String[]{"1.0", "2.0"}, "foo");
        assertEquals(2, inst.size());
        assertEquals(1.0, dataset.instance(0).get(0), DELTA);
        assertEquals(2.0, dataset.instance(0).get(1), DELTA);
        assertEquals("foo", inst.classValue());
    }

    @Test
    public void testDecimalCharacterSeparator() {
        subject = new DoubleArrayFactory(dataset, ',');
        Instance inst = subject.create(new String[]{"1,0", "2,0"}, "foo");
        assertEquals(2, inst.size());
        assertEquals(1.0, dataset.instance(0).get(0), DELTA);
        assertEquals(2.0, dataset.instance(0).get(1), DELTA);
        assertEquals("foo", inst.classValue());
    }

    @Test
    public void testCreate() {
        assertEquals(0, dataset.size());
        subject.create();
        assertEquals(1, dataset.size());
    }

    @Test
    public void testBuild() {
        assertEquals(0, dataset.size());
        Instance inst = subject.build();
        assertEquals(0, inst.size());
        assertEquals(0, dataset.size());
    }

    @Test
    public void testCreate_int() {
    }

    @Test
    public void testBuild_int() {
    }

    @Test
    public void testCreate_DoubleArr_AttributeArr() {
    }

}
