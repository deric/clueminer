package org.clueminer.dataset.plugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.attributes.NumericalAttribute;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;
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
public class ArrayDatasetTest {

    private static Dataset<Instance> dataset;
    private static final int dataCapacity = 10;
    private static final int attributesCnt = 2;
    private static Random rand;
    private static final double delta = 1e-7;

    public ArrayDatasetTest() {
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        dataset = new ArrayDataset<>(dataCapacity, attributesCnt);
        dataset.builder().create(new double[]{1, 2});
        dataset.setAttribute(0, dataset.attributeBuilder().create("a1", "NUMERIC"));
        dataset.setAttribute(1, dataset.attributeBuilder().create("a2", "NUMERIC"));
        rand = new Random();
        //before each testing method we add an instance to the dataset
        dataset.add(new DoubleArrayDataRow(new double[]{rand.nextDouble(), rand.nextDouble()}));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testArrayConstructor() {
        double[][] data = new double[][]{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}};
        Dataset<? extends Instance> test = new ArrayDataset<>(data);
        assertEquals(data[0].length, test.attributeCount());
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                test.set(i, j, data[i][j]);
                assertEquals(data[i][j], test.get(i, j), delta);
            }
        }
    }

    /**
     * Test of getClasses method, of class ArrayDataset.
     */
    @Test
    public void testGetClasses() {
    }

    /**
     * Test of add method, of class ArrayDataset.
     */
    @Test
    public void testAdd() {
        int size = dataset.size();
        dataset.add(new DoubleArrayDataRow(new double[]{2, 2}));
        assertEquals(size + 1, dataset.size());
    }

    /**
     * Test of addAll method, of class ArrayDataset.
     */
    @Test
    public void testAddAll_Dataset() {
    }

    /**
     * Test of instance method, of class ArrayDataset.
     */
    @Test
    public void testInstance() {
        Instance inst = dataset.get(0);
        assertNotNull(inst);
        assertEquals(attributesCnt, inst.size());

        int size = dataset.size();
        // get Instance from position bigger than dataset
        // => should create new one (and increase dataset size)
        inst = dataset.instance(size);
        assertEquals(size + 1, dataset.size());
        assertNotNull(inst);
    }

    @Test
    public void testSetName() {
        String name = "foo";
        dataset.setName(name);
        assertEquals(name, dataset.getName());
    }

    /**
     * Test of getRandom method, of class ArrayDataset.
     */
    @Test
    public void testGetRandom() {
    }

    /**
     * Test of size method, of class ArrayDataset.
     */
    @Test
    public void testSize() {
        int size = dataset.size();
        //if executed in random order we can't be sure what's the size
        System.out.println("size= " + dataset.size() + " capacity = " + dataset.getCapacity());
        assertEquals(true, (size <= dataset.getCapacity() && size > 0));
    }

    /**
     * Test of isEmpty method, of class ArrayDataset.
     */
    @Test
    public void testIsEmpty() {
        assertEquals(false, dataset.isEmpty());
    }

    /**
     * Test of classIndex method, of class ArrayDataset.
     */
    @Test
    public void testClassIndex() {
    }

    /**
     * Test of classValue method, of class ArrayDataset.
     */
    @Test
    public void testClassValue() {
    }

    /**
     * Test of builder method, of class ArrayDataset.
     */
    @Test
    public void testBuilder() {
    }

    /**
     * Test of attributeBuilder method, of class ArrayDataset.
     */
    @Test
    public void testAttributeBuilder() {
    }

    /**
     * Test of attributeCount method, of class ArrayDataset.
     */
    @Test
    public void testAttributeCount() {
        assertEquals(2, dataset.attributeCount());
    }

    /**
     * Test of getAttribute method, of class ArrayDataset.
     */
    @Test
    public void testGetAttribute_int() {
    }

    /**
     * Test of getAttribute method, of class ArrayDataset.
     */
    @Test
    public void testGetAttribute_String() {
    }

    /**
     * Test of setAttribute method, of class ArrayDataset.
     */
    @Test
    public void testSetAttribute() {
        Attribute attr = new NumericalAttribute("b1");
        dataset.setAttribute(0, attr);
        assertEquals("b1", dataset.getAttribute(0).getName());
        assertEquals(2, dataset.attributeCount());
    }

    @Test
    public void testSetAttributeReallocation() {
        Dataset<? extends Instance> test = new ArrayDataset<>(5, 2);
        int attrNewCnt = 10;
        int expected = test.attributeCount() + attrNewCnt;
        for (int i = 0; i < attrNewCnt; i++) {
            test.attributeBuilder().create("attr" + i, "NUMERIC");
        }
        assertEquals(expected, test.attributeCount());
    }

    @Test
    public void testSetAttributeValueIntInt() {
        double value = 1.23;
        dataset.set(0, 1, value);
        assertEquals(value, dataset.get(0, 1), delta);
    }

    /**
     * Test of getAttributes method, of class ArrayDataset.
     */
    @Test
    public void testGetAttributes() {
        int attrCnt = 3;
        Dataset<? extends Instance> test = new ArrayDataset<>(3, attrCnt);
        String name;
        for (int i = 0; i < attrCnt; i++) {
            name = "attr" + i;
            System.out.println(name);
            test.addAttribute(new NumericalAttribute(name));
        }
        assertEquals(attrCnt, test.attributeCount());
        //assertEquals(attrCnt, test.getAttributes().size());
    }

    /**
     * Test of copyAttributes method, of class ArrayDataset.
     */
    @Test
    public void testCopyAttributes() {
        Attribute[] attr = dataset.getAttributes().values().toArray(new Attribute[dataset.attributeCount()]);
        Attribute[] clone = dataset.copyAttributes();
        int i = 0;
        for (Attribute a : attr) {
            assertEquals(a.getIndex(), clone[i].getIndex());
            assertEquals(a.getName(), clone[i].getName());
            assertEquals(a.isNumerical(), clone[i].isNumerical());
            assertEquals(a.isMeta(), clone[i].isMeta());
            i++;
        }
        //should be deep copy
        attr[0].setIndex(99);
        System.out.println("clone idx = " + clone[0].getIndex());
        /**
         * TODO fix this
         */
        // assertNotSame(attr[0].getIndex(), clone[0].getIndex());
    }

    /**
     * Test of copy method, of class ArrayDataset.
     */
    @Test
    public void testCopy() {
    }

    /**
     * Test of toString method, of class ArrayDataset.
     */
    @Test
    public void testToString() {
    }

    /**
     * Test of arrayCopy method, of class ArrayDataset.
     */
    @Test
    public void testArrayCopy() {
    }

    /**
     * Test of getAttributeValue method, of class ArrayDataset.
     */
    @Test
    public void testGetAttributeValue_String_int() {
    }

    /**
     * Test of getAttributeValue method, of class ArrayDataset.
     */
    @Test
    public void testGetAttributeValue_Attribute_int() {
    }

    /**
     * Test of getAttributeValue method, of class ArrayDataset.
     */
    @Test
    public void testGetAttributeValue_int_int() {
    }

    /**
     * Test of setAttributes method, of class ArrayDataset.
     */
    @Test
    public void testSetAttributes() {
        Map<Integer, Attribute> map = new HashMap();
        map.put(0, dataset.attributeBuilder().create("attr0", "NUMERIC"));
        map.put(1, dataset.attributeBuilder().create("attr1", "NUMERIC"));
        map.put(2, dataset.attributeBuilder().create("attr2", "NUMERIC"));

        Dataset<? extends Instance> test = new ArrayDataset<>(5, 2);
        test.setAttributes(map);
        assertEquals(3, test.attributeCount());
    }

    /**
     * Test of setAttributeValue method, of class ArrayDataset.
     */
    @Test
    public void testSetAttributeValue() {
    }

    /**
     * Test of getPlotter method, of class ArrayDataset.
     */
    @Test
    public void testGetPlotter() {
    }

    /**
     * Test of duplicate method, of class ArrayDataset.
     */
    @Test
    public void testDuplicate() {
        Dataset<? extends Instance> dup = dataset.duplicate();
        //should not contain instances
        assertEquals(0, dup.size());
        assertEquals(dataset.attributeCount(), dup.attributeCount());
        assertEquals(dataset, dup.getParent());
    }

    /**
     * Test of getCapacity method, of class ArrayDataset.
     */
    @Test
    public void testGetCapacity() {
    }

    /**
     * Test of addAll method, of class ArrayDataset.
     */
    @Test
    public void testAddAll_Collection() {
    }

    /**
     * Test of get method, of class ArrayDataset.
     */
    @Test
    public void testGet() {
        Dataset<? extends Instance> test = new ArrayDataset<>(3, 3);
        double[][] data = new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};

        //test matrix-like approach to accessing data
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                test.set(i, j, data[i][j]);
                assertEquals(data[i][j], test.get(i, j), delta);
            }
        }
    }

    /**
     * Test of ensureCapacity method, of class ArrayDataset.
     */
    @Test
    public void testEnsureCapacity() {
    }

    /**
     * Test of contains method, of class ArrayDataset.
     */
    @Test
    public void testContains() {
        Instance inst = new DoubleArrayDataRow(new double[]{rand.nextDouble(), rand.nextDouble()});
        dataset.add(inst);
        assertEquals(true, dataset.contains(inst));

        //new instance which was not added to dataset
        inst = new DoubleArrayDataRow(new double[]{rand.nextDouble(), rand.nextDouble()});
        assertEquals(false, dataset.contains(inst));
    }

    /**
     * Test of toArray method, of class ArrayDataset.
     */
    @Test
    public void testToArray_0args() {
    }

    /**
     * Test of toArray method, of class ArrayDataset.
     */
    @Test
    public void testToArray_GenericType() {
    }

    /**
     * Test of remove method, of class ArrayDataset.
     */
    @Test
    public void testRemove() {
    }

    /**
     * Test of containsAll method, of class ArrayDataset.
     */
    @Test
    public void testContainsAll() {
    }

    /**
     * Test of removeAll method, of class ArrayDataset.
     */
    @Test
    public void testRemoveAll() {
    }

    /**
     * Test of retainAll method, of class ArrayDataset.
     */
    @Test
    public void testRetainAll() {
    }

    /**
     * Test of clear method, of class ArrayDataset.
     */
    @Test
    public void testClear() {
    }

    /**
     * Test of iterator method, of class ArrayDataset.
     */
    @Test
    public void testIterator() {
        int size = dataset.size();
        int i = 0;
        Iterator it = dataset.iterator();
        while (it.hasNext()) {
            it.next();
            i++;
        }
        assertEquals(size, i);
    }

    @Test
    public void testAddChild() {
        double data[][] = dataset.arrayCopy();
        Matrix m = Scaler.standartize(data, "z-score", false);

        m.print(6, 2);
        ArrayDataset<Instance> copy = (ArrayDataset<Instance>) dataset.duplicate();
        for (int i = 0; i < m.rowsCount(); i++) {
            Instance inst = new DoubleArrayDataRow(m.getRowVector(i).toArray());
            copy.add(inst);
        }
        String key = "z-score";
        dataset.addChild(key, copy);

        assertEquals(copy, dataset.getChild(key));
    }

    @Test
    public void testHasParent() {
        assertEquals(false, dataset.hasParent());

        Dataset<? extends Instance> dupl = dataset.duplicate();
        dataset.setParent(dupl);
        assertEquals(true, dataset.hasParent());
    }

    /**
     * Test of hasIndex method, of class ArrayDataset.
     */
    @Test
    public void testHasIndex() {
        assertEquals(true, dataset.hasIndex(0));
        assertEquals(true, dataset.hasIndex(dataset.size() - 1));
        assertEquals(false, dataset.hasIndex(dataset.size()));
        assertEquals(false, dataset.hasIndex(dataset.size() + 1));
        assertEquals(false, dataset.hasIndex(-1));
        assertEquals(false, dataset.hasIndex(99999));
    }

    @Test
    public void testGetAttributeByRole() {
        Attribute[] attr = dataset.attributeByRole(BasicAttrRole.INPUT);
        assertEquals(2, attr.length);
    }

    @Test
    public void testZeroCapacity() {
        dataset = new ArrayDataset<>(0, attributesCnt);
        dataset.builder().create(new double[]{1, 2});
        dataset.setAttribute(0, dataset.attributeBuilder().create("a1", "NUMERIC"));
        dataset.setAttribute(1, dataset.attributeBuilder().create("a2", "NUMERIC"));
        rand = new Random();
        //before each testing method we add an instance to the dataset
        dataset.add(new DoubleArrayDataRow(new double[]{rand.nextDouble(), rand.nextDouble()}));
    }

    @Test
    public void testInstanceIndex() {
        Instance inst = dataset.get(0);
        assertEquals(0, inst.getIndex());
        dataset.add(dataset.builder().create(new double[]{1.0, 2.0}));
        int index = dataset.size() - 1;
        inst = dataset.get(index);
        assertEquals(index, inst.getIndex());
    }

}
