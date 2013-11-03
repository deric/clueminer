package org.clueminer.dataset.plugin;

import java.util.Random;
import org.clueminer.attributes.AttributeType;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.exception.UnsupportedAttributeType;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tomas Barton
 */
public class SampleDatasetTest {

    private SampleDataset dataset;

    public SampleDatasetTest() throws UnsupportedAttributeType {
        dataset = new SampleDataset(5);
        AttributeBuilder builder = dataset.attributeBuilder();
        dataset.setAttribute(0, builder.create("first", AttributeType.NUMERICAL));
        dataset.setAttribute(1, builder.create("second", AttributeType.NUMERICAL));
        dataset.setAttribute(2, builder.create("third", AttributeType.NUMERICAL));
        //dataset.attributeBuilder().create("class", DataTypes.CLASS_VALUE);

        Instance inst = dataset.builder().create(new double[]{0.1, 0.5, 3});
        dataset.add(inst);
        inst = dataset.builder().create(new double[]{0.5, 3.0, 8});
        dataset.add(inst);
        inst = dataset.builder().create(new double[]{0.3, 3.2, 8});
        dataset.add(inst);
        inst = dataset.builder().create(new double[]{0.1, 4.0, 15});
        dataset.add(inst);

        dataset.setName("test dataset");
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getName method, of class SampleDataset.
     */
    @Test
    public void testGetName() {
        assertEquals("test dataset", dataset.getName());
    }

    /**
     * Test of setName method, of class SampleDataset.
     */
    @Test
    public void testSetName() {
        dataset.setName("test");
        assertEquals("test", dataset.getName());
    }

    /**
     * Test of check method, of class SampleDataset.
     */
    @Test
    public void testCheck_Collection() {
    }

    /**
     * Test of check method, of class SampleDataset.
     */
    @Test
    public void testCheck_Instance() {
        Instance inst = dataset.builder().create(new double[]{0.1, 0.5, 3});
        assertEquals(3, inst.size());
        dataset.check(inst);
        Instance inst2 = dataset.builder().create(new double[]{1, 5});
        //adding less attributes should be OK
        dataset.check(inst2);
    }

    /**
     * Test of add method, of class SampleDataset.
     */
    @Test(expected = RuntimeException.class)
    public void testAdd_Instance() {
        Instance inst3 = dataset.builder().create(new double[]{1, 5, 5, 10});
        //we can't more attributes than are defined in dataset (3)
        dataset.add(inst3);
    }

    /**
     * Test of add method, of class SampleDataset.
     */
    @Test
    public void testAdd_int_Instance() {
        int datasetSizeBefore = dataset.size();
        //we add instance at first position
        Instance inst = dataset.builder().create(new double[]{0.1, 0.5, 3});
        dataset.add(0, inst);
        //inserting instance at the same position should cause shifting
        Instance inst2 = dataset.builder().create(new double[]{1, 5});
        dataset.add(0, inst2);

        //so second is first
        assertEquals(inst2, dataset.get(0));
        //and first after the second :)
        assertEquals(inst, dataset.get(1));
        assertEquals(2 + datasetSizeBefore, dataset.size());
    }

    /**
     * Test of addAll method, of class SampleDataset.
     */
    @Test
    public void testAddAll_Collection() {
    }

    /**
     * Test of addAll method, of class SampleDataset.
     */
    @Test
    public void testAddAll_Dataset() {
    }

    /**
     * Test of addAll method, of class SampleDataset.
     */
    @Test
    public void testAddAll_int_Collection() {
    }

    /**
     * Test of clear method, of class SampleDataset.
     */
    @Test
    public void testClear() {
        Random rand = new Random();
        int sizeBefore = dataset.size();
        int max = 10;
        for (int i = 0; i < max; i++) {
            Instance inst = dataset.builder().create(dataset.attributeCount());
            for (int j = 0; j < dataset.attributeCount(); j++) {
                inst.set(j, rand.nextDouble());
                //System.out.println(inst);
            }
            dataset.add(inst);
        }
        assertEquals(max + sizeBefore, dataset.size());
        dataset.clear();
        assertEquals(0, dataset.size());
    }

    /**
     * Test of getRandom method, of class SampleDataset.
     */
    @Test
    public void testGetRandom() {
    }

    /**
     * Test of getClasses method, of class SampleDataset.
     */
    @Test
    public void testGetClasses() {
    }

    /**
     * Test of attributeCount method, of class SampleDataset.
     */
    @Test
    public void testAttributeCount() {
        assertEquals(3, dataset.attributeCount());
    }

    /**
     * Test of getAttribute method, of class SampleDataset.
     */
    @Test
    public void testGetAttributeInt() {
        Attribute a = dataset.getAttribute(0);
        assertEquals("first", a.getName());
    }

    @Test
    public void testGetAttributeString() {
        Attribute a = dataset.getAttribute("third");
        assertEquals("third", a.getName());
    }

    @Test(expected = RuntimeException.class)
    public void testAttributeNotFound() {
        dataset.getAttribute("foo");
    }

    /**
     * Test of setAttribute method, of class SampleDataset.
     */
    @Test
    public void testSetAttribute() {
    }

    /**
     * Test of getAttributes method, of class SampleDataset.
     */
    @Test
    public void testGetAttributes() {
    }

    /**
     * Test of classIndex method, of class SampleDataset.
     */
    @Test
    public void testClassIndex() {
    }

    /**
     * Test of classValue method, of class SampleDataset.
     */
    @Test
    public void testClassValue() {
    }

    /**
     * Test of builder method, of class SampleDataset.
     */
    @Test
    public void testBuilder() {
    }

    /**
     * Test of attributeBuilder method, of class SampleDataset.
     */
    @Test
    public void testAttributeBuilder() {
    }

    /**
     * Test of copy method, of class SampleDataset.
     */
    @Test
    public void testCopy() {
        Dataset<Instance> copy = dataset.copy();
        assertEquals(dataset.size(), copy.size());
        assertEquals(dataset.attributeCount(), copy.attributeCount());
        Instance inst = copy.builder().create(new double[]{0.1, 0.5, 3});
        copy.add(inst);
        assertEquals(dataset.size() + 1, copy.size());
    }

    /**
     * Test of duplicate method, of class SampleDataset.
     */
    @Test
    public void testDuplicate() {
        Dataset<Instance> dupl = dataset.duplicate();
        //should copy only structure of dataset but not data itself
        assertEquals(0, dupl.size());
        assertEquals(dataset.attributeCount(), dupl.attributeCount());
        Instance inst = dupl.builder().create(new double[]{0.1, 0.5, 3});
        dupl.add(inst);
        assertEquals(1, dupl.size());
    }

    /**
     * Test of toString method, of class SampleDataset.
     */
    @Test
    public void testToString() {
    }

    @Test
    public void testHasParent() {
        assertEquals(false, dataset.hasParent());

        Dataset<Instance> dupl = dataset.duplicate();
        dataset.setParent(dupl);
        assertEquals(true, dataset.hasParent());
    }
}
