package org.clueminer.dataset.row;

import java.util.Arrays;
import java.util.Iterator;
import org.clueminer.dataset.api.DataRow;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.math.Vector;

/**
 *
 * @author Tomas Barton
 */
public class DoubleArrayDataRow extends DataRow implements Iterable<Double>, Vector<Double> {

    private static final long serialVersionUID = -4054619137644952331L;
    private int last = 0;
    /**
     * Holds the values for all attributes
     */
    private double[] data;
    private Iterator<Double> it;

    /**
     * Creates a new data row backed by an primitive array.
     */
    public DoubleArrayDataRow(double[] data) {
        super(null);
        set(data);
    }

    public DoubleArrayDataRow(int size) {
        super(null);
        set(new double[size]);
    }

    public DoubleArrayDataRow(int size, Object classValue) {
        super(classValue);
        set(new double[size]);
    }

    @Override
    public String getFullName() {
        return getId() + " - " + getName();
    }

    /**
     * Add value to row and increment counter of last used value
     *
     * @param value
     * @return
     */
    @Override
    public int put(double value) {
        if (last >= size()) {
            //extending array is rather expensive on 
            //reallocation of array, so we rather make bigger space
            setCapacity((int) (last * 1.5));
        }
        data[last] = value;
        return last++;
    }

    @Override
    public double value(int index) {
        return data[index];
    }

    /**
     * Returns the desired data for the given index.
     */
    @Override
    protected double getValue(int index, double defaultValue) {
        if (index < 0 || index >= last) {
            return defaultValue;
        }
        return value(index);
    }

    @Override
    public Number getValue(int index) {
        return value(index);
    }

    public double get(int index) {
        return get(index);
    }

    @Override
    public void put(int index, double value) {
        data[index] = (float) value;
    }

    public final void set(double[] data) {
        this.data = data;
        last = data.length;
    }

    @Override
    public void set(int index, Number value) {
        data[index] = value.doubleValue();
    }

    /**
     * Sets the given data for the given index.
     */
    @Override
    protected void setValue(int index, double value, double defaultValue) {
        data[index] = value;
    }

    /**
     * Number of attributes in the instance
     *
     * @return
     */
    @Override
    public int size() {
        if (data == null) {
            return 0;
        }
        return data.length;
    }

    @Override
    public boolean isEmpty() {
        return (size() == 0);
    }

    /**
     * Creates a new array of the given size if necessary and copies the data
     * into the new array.
     *
     * @param numberOfColumns
     */
    @Override
    public void setCapacity(int numberOfColumns) {
        if (data.length >= numberOfColumns) {
            return;
        }
        double[] newData = new double[numberOfColumns];
        System.arraycopy(data, 0, newData, 0, data.length);
        data = newData;
    }

    /**
     * Capacity is the same as size of the data array
     *
     * @return
     */
    @Override
    public int getCapacity() {
        return this.size();
    }

    /**
     * Does nothing.
     */
    @Override
    public void trim() {
    }

    /**
     * Remove i-th attribute
     *
     * @param i
     */
    @Override
    public void remove(int i) {
        double[] tmp = data.clone();
        this.data = new double[tmp.length - 1];
        System.arraycopy(tmp, 0, data, 0, i);
        System.arraycopy(tmp, i + 1, data, i, tmp.length - i - 1);
    }

    /**
     * Index of last item in an array
     *
     * @return
     */
    public int getLast() {
        return last;
    }

    @Override
    public Instance copy() {
        DoubleArrayDataRow copy = new DoubleArrayDataRow(this.size());
        for (int i = 0; i < this.size(); i++) {
            copy.put(i, this.value(i));
        }
        return copy;
    }

    /**
     * @TODO consider this: As long as most instance copies provides deep copy,
     * it's not systematic to provide a shallow copy, although it might be
     * useful in some cases.
     *
     * @return
     */
    @Override
    public double[] arrayCopy() {
        return data.clone();
    }

    @Override
    public Plotter getPlotter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double magnitude() {
        double m = 0;
        int length = size();
        for (int i = 0; i < length; ++i) {
            double d = get(i);
            m += d * d;
        }
        return Math.sqrt(m);
    }

    @Override
    public Vector<Double> add(Vector<Double> other) {
        if (this.size() != other.size()) {
            throw new IllegalArgumentException("Vectors of different sizes cannot be added");
        }
        Vector<Double> res = new DoubleArrayDataRow(this.size());
        for (int i = 0; i < this.size(); i++) {
            res.set(i, getValue(i).doubleValue() + other.getValue(i).doubleValue());
        }
        return res;
    }

    class InstanceValueIterator implements Iterator<Double> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public Double next() {
            index++;
            return value(index - 1);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from instance using the iterator.");

        }
    }

    @Override
    public Iterator<Double> iterator() {
        if (it == null) {
            it = new InstanceValueIterator();
        }
        return it;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DoubleArrayDataRow other = (DoubleArrayDataRow) obj;
        if (!Arrays.equals(data, other.data)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a string representation of the data row.
     */
    @Override
    public String toString() {
        return this.toString(",");
    }

    @Override
    public String toString(String separator) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            result.append(i == 0 ? "" : ",").append(data[i]);
        }
        return result.toString();
    }
}
