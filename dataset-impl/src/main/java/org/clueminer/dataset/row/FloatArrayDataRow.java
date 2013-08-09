package org.clueminer.dataset.row;

import java.util.Arrays;
import java.util.Iterator;
import org.clueminer.dataset.api.DataRow;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.math.Vector;

/**
 * Implementation of DataRow that is backed by a float array. Please note that
 * for most applications the precision of floats should be high enough. The
 * highest precision is provided by {@link DoubleArrayDataRow}s but these need
 * the double amount compared to these float representations which are therefore
 * a good trade-off between precision and memory usage.
 *
 * @author Tomas Barton
 */
public class FloatArrayDataRow extends DataRow<Float> implements Iterable<Float> {

    private static final long serialVersionUID = -9049887710056073109L;
    private int last = 0;
    /**
     * Holds the data for all attributes.
     */
    private float[] data;

    /**
     * Creates a new data row backed by an primitive array.
     */
    public FloatArrayDataRow(float[] data) {
        super(null);
        set(data);
    }

    /**
     * Create float row with given capacity
     *
     * @param capacity
     */
    public FloatArrayDataRow(int capacity) {
        super(null);
        set(new float[capacity]);
    }

    public FloatArrayDataRow(int capacity, Object classValue) {
        super(classValue);
        set(new float[capacity]);
    }

    @Override
    public String getFullName() {
        return getId() + " - " + getName();
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

    /**
     * Add value to row and increment counter of last used value
     *
     * @param value
     * @return
     */
    @Override
    public int put(double value) {
        data[last] = (float) value;
        return last++;
    }

    @Override
    public void set(int index, double value) {
        data[index] = (float) value;
    }

    public final void set(float[] data) {
        this.data = data;
        last = data.length;
    }

    /**
     * Remove i-th attribute
     *
     * @param i
     */
    @Override
    public void remove(int i) {
        float[] tmp = data.clone();
        this.data = new float[tmp.length - 1];
        System.arraycopy(tmp, 0, data, 0, i);
        System.arraycopy(tmp, i + 1, data, i, tmp.length - i - 1);
    }

    /**
     * Sets the given data for the given index.
     */
    @Override
    protected void setValue(int index, double value, double defaultValue) {
        data[index] = (float) value;
    }

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
     */
    @Override
    public void setCapacity(int numberOfColumns) {
        if (data.length >= numberOfColumns) {
            return;
        }
        float[] newData = new float[numberOfColumns];
        System.arraycopy(data, 0, newData, 0, data.length);
        data = newData;
    }

    @Override
    public int getCapacity() {
        return this.size();
    }

    @Override
    public void trim() {
    }

    @Override
    public double[] arrayCopy() {
        double[] res = new double[size()];
        for (int i = 0; i < size(); i++) {
            res[i] = value(i);
        }
        return res;
    }

    @Override
    public Plotter getPlotter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public Number getValue(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double magnitude() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set(int index, Number value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Float> add(Vector<Float> other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    class InstanceValueIterator implements Iterator<Float> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public Float next() {
            index++;
            return (float) value(index - 1);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from instance using the iterator.");

        }
    }

    @Override
    public Iterator<Float> iterator() {
        return new InstanceValueIterator();
    }

    @Override
    public Instance copy() {
        FloatArrayDataRow copy = new FloatArrayDataRow(this.size());
        for (int i = 0; i < this.size(); i++) {
            copy.set(i, this.value(i));
        }
        return copy;
    }

    @Override
    public int hashCode() {
        final int prime = 29;
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
        final FloatArrayDataRow other = (FloatArrayDataRow) obj;
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
