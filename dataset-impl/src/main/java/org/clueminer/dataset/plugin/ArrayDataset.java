package org.clueminer.dataset.plugin;

import java.util.*;
import javax.swing.JComponent;
import org.clueminer.attributes.AttributeFactoryImpl;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.math.plot.Plot2DPanel;

/**
 * Dataset with fixed number of items
 *
 * @param <E>
 * @TODO consider performance of this dataset and possible removal
 *
 * @author Tomas Barton
 */
public class ArrayDataset<E extends Instance> extends AbstractArrayDataset<E> implements Dataset<E> {

    private static final long serialVersionUID = -5482153886671625555L;
    private Instance[] data;
    private InstanceBuilder builder;
    private AttributeBuilder attributeBuilder;
    private final TreeSet<Object> classes = new TreeSet<Object>();
    protected Attribute[] attributes;
    private int attrCnt = 0;
    /**
     * (n - 1) is index of last inserted item, n itself represents current
     * number of instances in this dataset
     */
    private int n = 0;

    public ArrayDataset(int instancesCapacity, int attributesCnt) {
        data = new Instance[instancesCapacity];
        attributes = new Attribute[attributesCnt];
    }

    @Override
    public SortedSet<Object> getClasses() {
        return classes;
    }

    @Override
    public boolean add(Instance i) {
        if ((n + 1) >= getCapacity()) {
            int capacity = (int) (n * 1.618); //golden ratio :)
            if (capacity == size()) {
                capacity = n * 3; // for small numbers due to int rounding we wouldn't increase the size
            }
            ensureCapacity(capacity);
        }
        data[n++] = i;
        return true;
    }


    /*
     * public boolean addAll(Collection<? extends Instance> c) { throw new
     * UnsupportedOperationException("Not supported yet."); }
     */
    @Override
    public boolean addAll(Dataset<E> d) {
        Iterator<E> it = d.iterator();
        while (n < data.length && it.hasNext()) {
            Instance i = it.next();
            data[n++] = i;
        }
        return !it.hasNext();
    }

    @Override
    public E instance(int index) {
        return (E) data[index];
    }

    @Override
    public boolean hasIndex(int idx) {
        return idx >= 0 && idx < size();
    }

    @Override
    public E getRandom(Random rand) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int size() {
        return n;
    }

    /**
     *
     * @return true when dataset is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return (size() == 0);
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
            attributeBuilder = new AttributeFactoryImpl();
        }
        return attributeBuilder;
    }

    /**
     * Real attribute count, doesn't include null attributes
     *
     * @return actual number of attributes
     */
    @Override
    public int attributeCount() {
        return attrCnt;
    }

    /**
     * Get i-th attribute instance
     *
     * @param i
     * @return
     */
    @Override
    public Attribute getAttribute(int i) {
        return attributes[i];
    }

    /**
     * Get i-th attribute by its name
     *
     * @param attributeName
     * @return
     */
    @Override
    public Attribute getAttribute(String attributeName) {
        for (Attribute attribute : attributes) {
            if (attribute.getName().equals(attributeName)) {
                return attribute;
            }
        }
        throw new RuntimeException("Attribute with name " + attributeName + " was not found");
    }

    /**
     * Set i-th attribute (column)
     *
     * @param i
     * @param attr
     */
    @Override
    public void setAttribute(int i, Attribute attr) {
        attr.setIndex(i);
        if (attributes[i] == null) {
            attrCnt++;
        }
        attributes[i] = attr;
    }

    /**
     *
     * @return reference to attribute map
     */
    @Override
    public Map<Integer, Attribute> getAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Attribute[] copyAttributes() {
        return attributes.clone();
    }

    /**
     * Deep copy of dataset
     *
     * @return
     */
    @Override
    public Dataset<E> copy() {
        SampleDataset out = new SampleDataset();
        Instance inst;
        for (int i = 0; i < size(); i++) {
            inst = instance(i);
            out.add(inst.copy());
        }
        return out;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("ArrayDataset [ size= " + size() + " \n");
        for (int i = 0; i < size(); i++) {
            str.append(classValue(i)).append(">> ").append(this.instance(i).toString());
        }
        str.append("\n ]");
        return str.toString();
    }

    @Override
    public double[][] arrayCopy() {
        double[][] res = new double[this.size()][this.attributeCount()];
        int i = 0;
        int cols = this.attributeCount();
        if (cols <= 0) {
            throw new ArrayIndexOutOfBoundsException("given dataset has width " + cols);
        }
        for (Instance inst : this) {
            for (int j = 0; j < inst.size(); j++) {
                res[i][j] = inst.value(j);///scaleToRange((float)inst.value(j), min, max, -10, 10);
            }
            i++;
        }
        return res;
    }

    @Override
    public double getAttributeValue(String attributeName, int instanceIdx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getAttributeValue(Attribute attribute, int instanceIdx) {
        return data[instanceIdx].value(attribute.getIndex());
    }

    @Override
    public double getAttributeValue(int attributeIndex, int instanceIdx) {
        return data[instanceIdx].value(attributeIndex);
    }

    @Override
    public void setAttributes(Map attributes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAttributeValue(String attributeName, int instanceIdx, double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JComponent getPlotter() {
        Plot2DPanel plot = new Plot2DPanel();

        double[] x = new double[this.size()];
        double[] y = new double[this.size()];
        // Dump.printMatrix(data.length,data[0].length,data,2,5);
        int k = 5;
        for (int j = 0; j < this.size(); j++) {
            x[j] = getAttributeValue(k, j);
        }

        k = 0;
        for (int j = 0; j < this.size(); j++) {
            //Attribute ta =  dataset.getAttribute(j);
            y[j] = getAttributeValue(k, j);

        }
        plot.addScatterPlot(getName(), x, y);
        return plot;
    }

    /**
     * Copies attributes but not data itself
     *
     * @return copy of dataset structure
     */
    @Override
    public Dataset<E> duplicate() {
        ArrayDataset<E> copy = new ArrayDataset<E>(this.size(), this.attributeCount());
        copy.attributes = this.attributes;
        copy.attrCnt = this.attrCnt;
        return copy;
    }

    @Override
    public int getCapacity() {
        if (data != null) {
            return data.length;
        }
        return 0;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E get(int index) {
        return (E) data[index];
    }

    @Override
    public void ensureCapacity(int capacity) {
        if (capacity > size()) {
            Instance[] tmp = new Instance[capacity];
            System.arraycopy(data, 0, tmp, 0, n);
            data = tmp;
        }
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return (T[]) data;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class ArrayDatasetIterator implements Iterator<Instance> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public Instance next() {
            index++;
            return instance(index - 1);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from dataset using the iterator.");

        }
    }

    @Override
    public Iterator<E> iterator() {
        return (Iterator<E>) new ArrayDatasetIterator();
    }
}
