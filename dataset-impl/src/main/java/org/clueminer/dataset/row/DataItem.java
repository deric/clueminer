package org.clueminer.dataset.row;

import java.io.Serializable;
import org.clueminer.math.Numeric;

/**
 * A wrapper for numeric value, useful when one value is computed from more
 * values etc.
 *
 * @author Tomas Barton
 */
public class DataItem extends Number implements Serializable, Numeric {

    private static final long serialVersionUID = 6001059665840018727L;
    protected double value = Double.NaN;

    public DataItem() {
    }

    public DataItem(double value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public double getValue() {
        return value;
    }

    protected void setValue(double value){
        this.value = value;
    }

    @Override
    public int compareTo(double d) {
        if (this.getValue() == d) {
            return 0;
        } else if (this.getValue() > d) {
            return 1;
        }
        return -1;
    }

    @Override
    public int compareTo(Numeric that) {
        if (this.getValue() == that.getValue()) {
            return 0;
        } else if (this.getValue() > that.getValue()) {
            return 1;
        }
        return -1;
    }

    @Override
    public String toString() {
        return ("DataItem[" + this.value + "]");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DataItem)) {
            return false;
        }
        DataItem that = (DataItem) obj;

        if (this.value != that.value) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        return hash;
    }
}