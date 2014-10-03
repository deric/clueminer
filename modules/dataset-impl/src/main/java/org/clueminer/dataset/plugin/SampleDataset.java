package org.clueminer.dataset.plugin;

import java.util.*;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.clueminer.attributes.AttributeFactoryImpl;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.math.plot.Plot2DPanel;
import org.openide.util.lookup.ServiceProvider;

/**
 * Strongly typed dataset based on standard Java ArrayList requires specified
 * all attributes before adding instances. If you don't know exactly the number
 * of instances, this is probably a good compromise between speed and
 * flexibility.
 *
 * @author Tomas Barton
 * @param <E>
 */
@ServiceProvider(service = Dataset.class)
public class SampleDataset<E extends Instance> extends AbstractDataset<E> implements Dataset<E> {

    private static final long serialVersionUID = -6412010424414577127L;
    protected Map<Integer, Attribute> attributes = new HashMap<>();
    protected InstanceBuilder builder;
    protected AttributeBuilder attributeBuilder;
    protected TreeSet<Object> classes = new TreeSet<>();
    private static final Logger logger = Logger.getLogger(SampleDataset.class.getName());
    private int attrCapacity = -1;
    private int lastAttr = 0;

    /**
     * Creates an empty data set with capacity of ten
     */
    public SampleDataset() {
        super(10);
    }

    public SampleDataset(int capacity, int numAttrs) {
        attributes = new HashMap<>(numAttrs);
        attrCapacity = numAttrs;
    }

    /**
     * Create new dataset and references parent as an original dataset
     *
     * @param parent
     */
    public SampleDataset(Dataset<E> parent) {
        super(10);
        setParent(parent);
    }

    /**
     * Create dataset with given capacity of instances
     *
     * @param capacity
     */
    public SampleDataset(int capacity) {
        super(capacity);
    }

    protected void check(Collection<? extends E> c) {
        for (E i : c) {
            check(i);
        }
    }

    /**
     * Checks number of attributes in added instance
     *
     * @param i Instance
     */
    protected void check(E i) {
        if (i.classValue() != null) {
            classes.add(i.classValue());
        }
        if (i.size() > attributes.size()) {
            System.out.println(i);
            throw new RuntimeException("instance contains attributes that are not "
                    + "defined in dataset! expected " + attributes.size() + " attributes, but got: " + i.size());
        }
    }

    @Override
    public boolean add(E e) {
        check(e);
        instanceAdded(e);
        if (e.getIndex() < 0) {
            e.setIndex(size());
        }
        return super.add(e);
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).
     *
     * @param index
     * @param e
     */
    @Override
    public void add(int index, E e) {
        check(e);
        e.setIndex(index);
        super.add(index, e);
    }

    @Override
    public final boolean addAll(Collection<? extends E> c) {
        return super.addAll(c);
    }

    @Override
    public boolean addAll(Dataset<E> d) {
        for (E i : d) {
            add(i);
        }
        return true;
    }

    /**
     * Inserts all of the elements in the specified collection into this list,
     * starting at the specified position.
     *
     * @param index
     * @param c
     * @return
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        check(c);
        return super.addAll(index, c);
    }

    @Override
    public void clear() {
        classes.clear();
        super.clear();
    }

    @Override
    public E instance(int index) {
        if (hasIndex(index)) {
            return get(index);
        } else if (index == size()) {
            //doesn't make sense to create instance with 0 attributes
            int attrs = attributeCount() == 0 ? attrCapacity : attributeCount();
            E inst = (E) builder().create(attrs);
            add(inst);
            return inst;
        }
        throw new ArrayIndexOutOfBoundsException("can't get instance at position: " + index);
    }

    @Override
    public E getRandom(Random rand) {
        int max = this.size();
        int min = 0;
        int i = min + (int) (rand.nextDouble() * ((max - min) + 1));
        return get(i);
    }

    @Override
    public SortedSet<Object> getClasses() {
        return classes;
    }

    @Override
    public int attributeCount() {
        return lastAttr;
    }

    /**
     * Get name of i-th attribute
     *
     * @param i
     * @return
     */
    @Override
    public Attribute getAttribute(int i) {
        return attributes.get(i);
    }

    /**
     * Set i-th attribute (column)
     *
     * @param i
     * @param attr
     */
    @Override
    public void setAttribute(int i, Attribute attr) {
        if (i > lastAttr) {
            lastAttr = i;
        }
        attr.setIndex(i);
        attr.setDataset(this);
        attributes.put(i, attr);
    }

    @Override
    public void setAttributes(Map<Integer, Attribute> attr) {
        this.attributes = attr;
        for (Attribute a : attr.values()) {
            a.setDataset(this);
        }
        lastAttr = attr.size();
    }

    /**
     * @TODO make sure, a deep copy is returned
     *
     * @return
     */
    @Override
    public Attribute[] copyAttributes() {
        return attributes.values().toArray(new Attribute[attributeCount()]);
    }

    /**
     * Reference to attributes specification
     *
     * @return
     */
    @Override
    public Map<Integer, Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public int classIndex(Object clazz) {
        if (clazz != null) {
            return this.getClasses().headSet(clazz).size();
        } else {
            return -1;
        }

    }

    @Override
    public Object classValue(int index) {
        int i = 0;
        for (Object o : this.classes) {
            if (i == index) {
                return o;
            }
            i++;
        }
        return null;
    }

    /**
     * When an item is added, we have to recompute statistics
     *
     * @param row
     */
    private void instanceAdded(Instance row) {
        Attribute a;
        for (int i = 0; i < attributeCount(); i++) {
            a = attributes.get(i);
            a.updateStatistics(row.value(i));
        }
    }

    @Override
    public InstanceBuilder builder() {
        if (builder == null) {
            builder = new DoubleArrayFactory('.');
        }
        return builder;
    }

    @Override
    public AttributeBuilder attributeBuilder() {
        if (attributeBuilder == null) {
            attributeBuilder = new AttributeFactoryImpl<>(this);
        }
        return attributeBuilder;
    }

    /**
     * Deep copy of dataset
     *
     * @return
     */
    @Override
    public Dataset<E> copy() {
        SampleDataset out = new SampleDataset();
        out.attributes = this.attributes;
        out.lastAttr = attributes.size();
        for (Instance i : this) {
            out.add(i.copy());
        }
        return out;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("SampleDataset [ size= " + size() + " \n");
        for (int i = 0; i < size(); i++) {
            str.append(classValue(i)).append(">> ").append(this.get(i).toString());
        }
        str.append("\n ]");
        return str.toString();
    }

    /**
     * @TODO consider using hashmap for attribute names. though this dataset is
     * not really meant for this type of operations
     *
     * @param attributeName
     * @param instanceIdx
     * @return
     */
    @Override
    public double getAttributeValue(String attributeName, int instanceIdx) {
        int i = 0;
        while (i < attributeCount()) {
            if (attributes.get(i).getName().equals(attributeName)) {
                return get(instanceIdx, i);
            }
            i++;
        }
        throw new RuntimeException("attribute " + attributeName + " not found");
    }

    @Override
    public double getAttributeValue(Attribute attribute, int instanceIdx) {
        return get(instanceIdx).value(attribute.getIndex());
    }

    /**
     * {@inheritDoc}
     *
     * @param instanceIdx
     * @param attributeIndex
     * @return
     */
    @Override
    public double get(int instanceIdx, int attributeIndex) {
        return get(instanceIdx).value(attributeIndex);
    }

    @Override
    public void setAttributeValue(String attributeName, int instanceIdx, double value) {
        int i = 0;
        boolean success = false;
        while (i < attributeCount() && !success) {
            if (attributes.get(i).getName().equals(attributeName)) {
                instance(instanceIdx).set(i, value);
                success = true;
            }
            i++;
        }
        if (!success) {
            throw new RuntimeException("attribute " + attributeName + " not found");
        }
    }

    @Override
    public JComponent getPlotter() {
        Plot2DPanel plot = new Plot2DPanel();

        double[] x = new double[this.size()];
        double[] y = new double[this.size()];
        // Dump.printMatrix(data.length,data[0].length,data,2,5);
        int k = 5;
        for (int j = 0; j < this.size(); j++) {
            x[j] = get(j, k);
        }

        k = 0;
        for (int j = 0; j < this.size(); j++) {
            //Attribute ta =  dataset.getAttribute(j);
            y[j] = get(j, k);

        }
        plot.addScatterPlot(getName(), x, y);
        return plot;
    }

    @Override
    public Attribute getAttribute(String attributeName) {
        for (Attribute a : attributes.values()) {
            if (a.getName().equals(attributeName)) {
                return a;
            }
        }
        throw new RuntimeException("attribute " + attributeName + " was not found");
    }

    @Override
    public Dataset<E> duplicate() {
        SampleDataset<E> copy = new SampleDataset<>(this.size());
        copy.setAttributes(attributes);
        return copy;
    }

    @Override
    public void addAttribute(Attribute attr) {
        attr.setDataset(this);
        attr.setIndex(lastAttr);
        attributes.put(lastAttr++, attr);
    }

}
