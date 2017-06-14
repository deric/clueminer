/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.clustering.struct;

import com.google.common.collect.Sets;
import java.awt.Color;
import java.util.Set;
import org.clueminer.attributes.AttributeFactoryImpl;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.api.StatsNum;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.dataset.impl.DoubleArrayFactory;

/**
 * A cluster representation with hard assignments (data point belong just to one
 * cluster).
 *
 * @author Tomas Barton
 * @param <E>
 */
public class BaseCluster<E extends Instance> extends ArrayDataset<E> implements Cluster<E>, Set<E> {

    private static final long serialVersionUID = -6931127664256794411L;
    private int clusterId;
    private Color color;
    private E centroid;
    private final Set<Integer> mapping = Sets.newHashSet();

    public BaseCluster(int capacity) {
        super(capacity, 5);
    }

    public BaseCluster(int capacity, int attrSize) {
        super(capacity, attrSize);
    }

    public BaseCluster(Dataset<E> dataset) {
        //some guess about future cluster size
        super((int) Math.sqrt(dataset.size()), dataset.attributeCount());
        setAttributes(dataset.getAttributes());
    }

    @Override
    public boolean add(Instance inst) {
        if (super.add(inst)) {
            mapping.add(inst.getIndex());
            centroid = null;
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(int origId) {
        return mapping.contains(origId);
    }

    @Override
    public void setClusterId(int id) {
        this.clusterId = id;
    }

    @Override
    public int getClusterId() {
        return clusterId;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Centroid is computed from stored cluster statistics
     *
     * @return artificial instance representing center of cluster
     */
    @Override
    public E getCentroid() {
        if (centroid == null) {
            if (size() == 1) {
                //one instance is an centroid to itself
                return (E) get(0);
            }
            int attrCount = this.attributeCount();
            if (attrCount == 0) {
                throw new RuntimeException("number of attributes should not be 0");
            }
            double value;
            Instance avg = this.builder().build(attrCount);
            for (int i = 0; i < attrCount; i++) {
                //use pre-computed average for each attribute
                value = getAttribute(i).statistics(StatsNum.MEAN);
                avg.set(i, value);
            }
            centroid = (E) avg;
        }

        return centroid;
    }

    @Override
    public void setCentroid(E centroid) {
        this.centroid = centroid;
    }

    /**
     * Counting is based in instance index which must be unique in dataset
     *
     * @param c
     * @return
     */
    @Override
    public int countMutualElements(Cluster<E> c) {
        int mutual = 0;
        for (Instance inst : this) {
            //System.out.println("looking for: " + inst.getIndex() + " found? " + c.contains(inst.getIndex()));
            if (c.contains(inst.getIndex())) {
                mutual++;
            }
        }
        return mutual;
    }

    @Override
    public InstanceBuilder<E> builder() {
        if (builder == null) {
            builder = new DoubleArrayFactory<>(this, '.');
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
     * Deep copy of a cluster
     *
     * @return
     */
    @Override
    public Cluster<E> copy() {
        Cluster<E> out = (Cluster<E>) duplicate();
        for (int i = 0; i < size(); i++) {
            out.set(i, (E) get(i).copy());
        }
        return out;
    }

    /**
     * Copies attributes but not data itself
     *
     * @return copy of dataset structure
     */
    @Override
    public Dataset<E> duplicate() {
        BaseCluster<E> copy = new BaseCluster<>(this.size(), this.attributeCount());
        int i = 0;
        for (Attribute attribute : attributes) {
            if (attribute == null) {
                throw new RuntimeException("null attribute at position " + i);
            }
            copy.attributeBuilder().create(attribute.getName(), BasicAttrType.NUMERIC, attribute.getRole());
            i++;
        }
        copy.setParent(this);
        return copy;
    }

    @Override
    public boolean isNoise() {
        return getName().equals(Algorithm.OUTLIER_LABEL);
    }

    @Override
    public boolean remove(Object o) {
        E inst = (E) o;
        mapping.remove(inst.getIndex());
        return super.remove(o);
    }

    /**
     * Hash code for clusters should not depend on order of elements in the
     * cluster.
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 0;
        for (E elem : this) {
            hash += elem.hashCode();
        }
        return hash * (size() + 1) >>> 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BaseCluster<?> other = (BaseCluster<?>) obj;
        if (this.size() != other.size()) {
            return false;
        }
        return this.hashCode() == other.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("BaseCluster ");
        sb.append(getName());
        sb.append(" (").append(size()).append(") ");
        sb.append(" [ ");
        E elem;
        for (int i = 0; i < this.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            elem = this.get(i);
            sb.append(elem.getIndex());
        }
        sb.append(" ]");
        return sb.toString();
    }
}
