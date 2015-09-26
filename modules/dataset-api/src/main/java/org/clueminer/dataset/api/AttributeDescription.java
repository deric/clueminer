package org.clueminer.dataset.api;

import java.io.Serializable;

/**
 *
 * @author Tomas Barton
 */
public class AttributeDescription implements Serializable {

    private static final long serialVersionUID = 2262660608205211256L;
    private String name;
    /**
     * The default value for this Attribute.
     */
    private double defaultValue = 0.0;
    /**
     * Index of this attribute in its Dataset
     */
    private int index;
    private AttributeType type;

    public AttributeDescription(String name, AttributeType type, double defaultValue) {
        this.name = name;
        this.index = -1;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public AttributeDescription(int index, String name, AttributeType type, double defaultValue) {
        this.name = name;
        this.index = index;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    AttributeDescription(AttributeDescription other) {
        this.name = other.name;
        this.type = other.type;
        this.defaultValue = other.defaultValue;
        this.index = other.index;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Default value of an attribute
     *
     * @return
     */
    public double getDefault() {
        return defaultValue;
    }

    public AttributeType getType() {
        return type;
    }

    public void setDefault(double value) {
        defaultValue = value;
    }

    @Override
    public Object clone() {
        return new AttributeDescription(this);
    }

    /**
     * Returns true if the given attribute has the same name and the same table
     * index.
     *
     * @param o
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AttributeDescription)) {
            return false;
        }
        AttributeDescription a = (AttributeDescription) o;
        return this.name.equals(a.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ Integer.valueOf(this.index).hashCode();
    }
}
