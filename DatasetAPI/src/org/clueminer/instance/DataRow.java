package org.clueminer.instance;

import org.clueminer.dataset.Attribute;

/**
 *
 * @author Tomas Barton
 */
public abstract class DataRow extends AbstractInstance implements Instance {

    private static final long serialVersionUID = 7076642394603104341L;
    
    
    public DataRow(Object classValue){
        setClassValue(classValue);
    }

    /**
     * Returns the value for the given index.
     */
    protected abstract double getValue(int index, double defaultValue);

    /**
     * Sets the given data for the given index.
     */
    protected abstract void setValue(int index, double value, double defaultValue);

    
    /**
     * Trims the number of columns to the actually needed number.
     */
    public abstract void trim();

    /**
     * Returns a string representation for this data row.
     */
    @Override
    public abstract String toString();

    /**
     * Returns the value stored at the given {@link Attribute}'s index. Returns
     * Double.NaN if the given attribute is null.
     */
    public double getValue(Attribute attribute) {
        if (attribute == null) {
            return Double.NaN;
        } else {
            try {
                return this.value(attribute.getIndex());
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ArrayIndexOutOfBoundsException("DataRow: of Attribute " + attribute.getName() + " is out of bounds.");
            }
        }
    }

    /**
     * Sets the value of the {@link Attribute} to
     * <code>value</code>.
     */
    public void setValue(Attribute attribute, double value) {
        put(attribute.getIndex(), value);
    }
}
