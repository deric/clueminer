package org.clueminer.dataset.plugin;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.junit.After;
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
    private static final double delta = 1e-9;

    public DoubleArrayFactoryTest() {
    }

    @Before
    public void setUp() {
        dataset = new ArrayDataset<>(2, 2);
        subject = new DoubleArrayFactory(dataset);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreate_doubleArr() {
        assertEquals(0, dataset.size());
        subject.create(new double[]{1.0, 2.0});
        assertEquals(1, dataset.size());
        assertEquals(1.0, dataset.instance(0).get(0), delta);
        assertEquals(2.0, dataset.instance(0).get(1), delta);
    }

    @Test
    public void testBuild_doubleArr() {
    }

    @Test
    public void testCreate_doubleArr_Object() {
    }

    @Test
    public void testCreate_doubleArr_String() {
    }

    @Test
    public void testBuild_doubleArr_String() {
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
    public void testCreateCopyOf_GenericType() {
    }

    @Test
    public void testCreateCopyOf_GenericType_Dataset() {
    }

    @Test
    public void testCreate_int() {
    }

    @Test
    public void testBuild_int() {
    }

    @Test
    public void testCreate_StringArr_AttributeArr() {
    }

    @Test
    public void testCreate_ObjectArr_AttributeArr() {
    }

    @Test
    public void testCreate_DoubleArr_AttributeArr() {
    }

}
