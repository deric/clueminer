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
public class IntegerDataRow extends DataRow<Integer> implements Iterable<Integer>, Instance<Integer> {

    private static final long serialVersionUID = -635455125197219190L;
    protected int[] data;
    protected int last = 0;
    protected Iterator<Integer> it;

    /**
     * @TODO: Array is passed as a reference, when working with data reference
     * it might cause unexpected results
     *
     * @param data
     */
    public IntegerDataRow(int[] data) {
        super(null);
        set(data);
    }

    public IntegerDataRow(int size) {
        super(null);
        data = new int[size];
    }

    public final void set(int[] data) {
        this.data = data;
        last = data.length;
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
    public int put(int value) {
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

    public int intValue(int index) {
        return data[index];
    }

    /**
     * Returns the desired data for the given index.
     *
     * @return
     */
    @Override
    protected double getValue(int index, double defaultValue) {
        if (index < 0 || index >= last) {
            return defaultValue;
        }
        return value(index);
    }

    @Override
    public Integer getValue(int index) {
        return data[index];
    }

    @Override
    public double get(int index) {
        return data[index];
    }

    @Override
    public void set(int index, double value) {
        data[index] = (int) value;
        if (index >= last) {
            last = index + 1;
        }
    }

    @Override
    public void set(int index, Number value) {
        data[index] = value.intValue();
        if (index >= last) {
            last = index + 1;
        }
    }

    /**
     * Sets the given data for the given index.
     */
    @Override
    protected void setValue(int index, double value, double defaultValue) {
        data[index] = (int) value;
        if (index >= last) {
            last = index + 1;
        }
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
        return last;
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
        int[] newData = new int[numberOfColumns];
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
        int[] tmp = data.clone();
        this.data = new int[tmp.length - 1];
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
            copy.set(i, this.value(i));
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
        double[] copy = new double[this.size()];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = data[i];

        }
        return copy;
    }

    public int[] arrayCopyInt() {
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

    private void checkForSameSize(Vector<Integer> other) {
        if (this.size() != other.size()) {
            throw new IllegalArgumentException("Vectors of different sizes cannot be added");
        }
    }

    @Override
    public Vector<Integer> add(Vector<Integer> other) {
        checkForSameSize(other);
        Vector<Integer> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, getValue(i) + other.getValue(i));
        }
        return res;
    }

    @Override
    public int put(double value) {
        if (last >= size()) {
            //extending array is rather expensive on
            //reallocation of array, so we rather make bigger space
            setCapacity((int) (last * 1.5));
        }
        data[last] = (int) value;
        return last++;
    }

    @Override
    public Vector<Integer> duplicate() {
        return new IntegerDataRow(this.size());
    }

    @Override
    public Vector<Integer> minus(Vector<Integer> other) {
        checkForSameSize(other);
        Vector<Integer> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, getValue(i) - other.getValue(i));
        }
        return res;
    }

    @Override
    public Vector<Integer> times(double scalar) {
        Vector<Integer> res = duplicate();
        for (int i = 0; i < this.size(); i++) {
            res.set(i, getValue(i) * scalar);
        }
        return res;
    }

    class InstanceValueIterator implements Iterator<Integer> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public Integer next() {
            index++;
            return intValue(index - 1);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from instance using the iterator.");

        }
    }

    @Override
    public Iterator<Integer> iterator() {
        if (it == null) {
            it = new IntegerDataRow.InstanceValueIterator();
        }
        return it;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        //instances having same values should not be considered as the same
        if (index >= 0) {
            result += (index + prime);
        }
        result += Arrays.hashCode(data);
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
        final IntegerDataRow other = (IntegerDataRow) obj;
        if (this.getIndex() != other.getIndex()) {
            return false;
        }
        return Arrays.equals(data, other.data);
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
        for (int i = 0; i < size(); i++) {
            result.append(i == 0 ? "" : ",").append(data[i]);
        }
        return result.toString();
    }
}
