package org.clueminer.dataset.api;

/**
 *
 * @author Tomas Barton
 */
public abstract class DataRow<T extends Number> extends AbstractInstance<T> implements Instance<T> {

    private static final long serialVersionUID = 7076642394603104341L;
    protected Instance ancestor;

    public DataRow(Object classValue) {
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
        set(attribute.getIndex(), value);
    }

    /**
     * When preprocessing data sometimes we need to display reference to
     * original data
     *
     * @return Instance from which was this one derived
     */
    @Override
    public Instance getAncestor(){
        return ancestor;
    }

    /**
     * Set reference to original data row
     *
     * @param instance
     */
    @Override
    public void setAncestor(Instance instance){
        this.ancestor = instance;
    }
    
    @Override
    public String[] toStringArray(){
        String[] res = new String[size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = String.valueOf(getValue(i, Double.NaN));            
        }
        return res;
    }
}
