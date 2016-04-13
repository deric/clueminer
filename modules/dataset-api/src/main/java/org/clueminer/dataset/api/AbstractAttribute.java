/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.dataset.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractAttribute implements Attribute {

    private static final long serialVersionUID = -210441685616484746L;
    /**
     * The basic information about the attribute. Will only be shallowly cloned.
     */
    private AttributeDescription attributeDescription;
    /**
     * Contains all attribute statistics calculation algorithms.
     */
    protected List<Statistics> statistics = new LinkedList<>();

    protected HashMap<IStats, Statistics> statisticsProviders;

    protected Dataset<? extends Instance> dataset;

    protected AttributeRole role;

    private int attrIndex;
    protected boolean allowMissing = true;

    /**
     * Creates a simple attribute which is not part of a series and does not
     * provide a unit string. This constructor should only be used for
     * attributes which were not generated with help of a generator, i.e. this
     * attribute has no function arguments. Only the last transformation is
     * cloned, the other transformations are cloned by reference.
     *
     * @param other
     */
    protected AbstractAttribute(AbstractAttribute other) {
        this.statisticsProviders = new HashMap<>();
        this.attributeDescription = new AttributeDescription(other.getName(), other.getType(), 0.0d);

        // copy statistics
        this.statistics = new LinkedList<>();
        for (Statistics st : other.statistics) {
            this.statistics.add((Statistics) st.clone());
        }
        this.role = other.role;
    }

    protected AbstractAttribute(String name, AttributeType type, AttributeRole role) {
        this.statisticsProviders = new HashMap<>();
        this.attributeDescription = new AttributeDescription(name, type, 0.0d);
        this.role = role;
    }

    /**
     * Returns the name of the attribute.
     *
     * @return
     */
    @Override
    public String getName() {
        return this.attributeDescription.getName();
    }

    /**
     * Sets the name of the attribute.
     *
     * @param v
     */
    @Override
    public void setName(String v) {
        if (v.equals(this.attributeDescription.getName())) {
            return;
        }
        this.attributeDescription.setName(v);
    }

    /**
     * @return index of column in dataset
     */
    @Override
    public int getIndex() {
        return attrIndex;
    }

    @Override
    public void setIndex(int index) {
        this.attrIndex = index;
    }

    @Override
    public void setDataset(Dataset<? extends Instance> dataset) {
        this.dataset = dataset;
    }

    /**
     * Returns the type of this attribute.
     *
     * @return
     */
    public AttributeType getType() {
        return this.attributeDescription.getType();
    }

    public void setType(AttributeType type) {
        this.attributeDescription.setType(type);
    }

    /**
     * Returns the attribute statistics.
     *
     * @return
     */
    @Override
    public Iterator<Statistics> getAllStatistics() {
        return this.statistics.iterator();
    }

    @Override
    public void registerStatistics(Statistics statistics) {
        this.statistics.add(statistics);
        IStats[] stats = statistics.provides();
        for (IStats stat : stats) {
            statisticsProviders.put(stat, statistics);
        }
    }

    @Override
    public double statistics(IStats name) {
        if (statisticsProviders.containsKey(name)) {
            return statisticsProviders.get(name).statistics(name);
        }
        throw new RuntimeException("statistics " + name + " was not registered");
    }

    @Override
    public void updateStatistics(Object value) {
        for (Statistics s : statistics) {
            s.valueAdded(value);
        }
    }

    @Override
    public void setDefault(double value) {
        this.attributeDescription = (AttributeDescription) this.attributeDescription.clone();
        this.attributeDescription.setDefault(value);
    }

    @Override
    public double getDefault() {
        return this.attributeDescription.getDefault();
    }

    @Override
    public AttributeRole getRole() {
        return role;
    }

    @Override
    public void setRole(AttributeRole role) {
        this.role = role;
    }

    @Override
    public void resetStats() {
        for (Statistics stats : statisticsProviders.values()) {
            stats.reset();
        }
    }

    @Override
    public boolean allowMissing() {
        return allowMissing;
    }

    @Override
    public void setAllowMissing(boolean b) {
        this.allowMissing = b;
    }

    /**
     * Returns true if the given attribute has the same name and the same table
     * index.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractAttribute)) {
            return false;
        }
        AbstractAttribute a = (AbstractAttribute) o;
        return this.attributeDescription.equals(a.attributeDescription);
    }

    @Override
    public int hashCode() {
        return attributeDescription.hashCode();
    }

    /**
     * Returns a human readable string that describes this attribute.
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(": ");
        result.append(this.attributeDescription.getName());
        return result.toString();
    }
}
