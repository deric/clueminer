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
package org.clueminer.dataset.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeRole;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.DataType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.api.MetaStore;
import org.clueminer.events.DatasetEvent;
import org.clueminer.events.DatasetListener;
import org.clueminer.math.Matrix;
import org.clueminer.utils.DMatrix;

/**
 * Common methods for data structure without making any assumptions on internal
 * storing structures.
 *
 * @author Tomas Barton
 * @param <E>
 */
public abstract class BaseDataset<E extends Instance> implements Dataset<E> {

    transient protected EventListenerList datasetListener;
    protected String id;
    protected String name;
    protected ColorGenerator colorGenerator;
    protected Dataset<E> parent = null;
    //default capacity same as with ArrayList
    private int capacity = 10;
    protected HashMap<String, Dataset<E>> children;
    protected Matrix matrix;
    protected InstanceBuilder<E> builder;
    protected DataType dataType = DataType.DISCRETE;
    protected MetaStore metaStore;

    public BaseDataset() {
        //do nothing
    }

    public BaseDataset(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Dataset<E> getParent() {
        return parent;
    }

    @Override
    public void setParent(Dataset<E> parent) {
        this.parent = parent;
    }

    @Override
    public boolean hasParent() {
        return this.parent != null;
    }

    @Override
    public void setColorGenerator(ColorGenerator cg) {
        this.colorGenerator = cg;
    }

    protected EventListenerList eventListenerList() {
        if (datasetListener == null) {
            datasetListener = new EventListenerList();
        }
        return datasetListener;
    }

    public void removeDataSetListener(DatasetListener listener) {
        eventListenerList().remove(DatasetListener.class, listener);
    }

    public void fireDatasetOpened(DatasetEvent evt) {
        DatasetListener[] listeners = eventListenerList().getListeners(DatasetListener.class);
        for (DatasetListener listener : listeners) {
            listener.datasetOpened(evt);
        }
    }

    public void addDatasetListener(DatasetListener listener) {
        eventListenerList().add(DatasetListener.class, listener);
    }

    @Override
    public double[][] arrayCopy() {
        double[][] res = new double[this.size()][attributeCount()];
        int cols = this.attributeCount();
        if (cols <= 0) {
            throw new ArrayIndexOutOfBoundsException("given dataset has width " + cols);
        }
        for (int i = 0; i < this.size(); i++) {
            Instance inst = instance(i);
            for (int j = 0; j < inst.size(); j++) {
                res[i][j] = inst.value(j);///scaleToRange((float)inst.value(j), min, max, -10, 10);
            }
        }
        return res;
    }

    /**
     * {@inheritDoc}
     *
     * @param instanceIdx
     * @param attrIdx
     * @param value
     */
    @Override
    public void set(int instanceIdx, int attrIdx, double value) {
        if (attrIdx > -1) {
            instance(instanceIdx).set(attrIdx, value);
        } else {
            throw new RuntimeException("Invalid attribute index: " + attrIdx);
        }
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void addChild(String key, Dataset<E> dataset) {
        if (children == null) {
            children = new HashMap<>(5);
        }
        children.put(key, dataset);
    }

    @Override
    public Dataset<E> getChild(String key) {
        if (children == null) {
            return null;
        }
        return children.get(key);
    }

    /**
     * Iterator over children datasets
     *
     * @return iterator over children keys
     */
    @Override
    public Iterator<String> getChildIterator() {
        if (children == null) {
            children = new HashMap<>(5);
        }
        return children.keySet().iterator();
    }

    @Override
    public boolean hasIndex(int idx) {
        return idx >= 0 && idx < size();
    }

    @Override
    public Attribute[] attributeByRole(AttributeRole role) {
        List<Attribute> list = new LinkedList<>();

        for (Attribute attr : getAttributes().values()) {
            if (attr.getRole() == role) {
                list.add(attr);
            }
        }

        if (list.size() > 0) {
            return list.toArray(new Attribute[list.size()]);
        } else {
            return new Attribute[0];
        }
    }

    /**
     * {@inheritDoc }
     *
     * @return
     */
    @Override
    public Matrix asMatrix() {
        if (matrix == null) {
            matrix = new DMatrix(this);
        }
        return matrix;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean succ = true;
        for (E elem : c) {
            succ &= add(elem);
        }
        return succ;
    }

    @Override
    public void setBuilder(InstanceBuilder<E> builder) {
        this.builder = builder;
    }

    @Override
    public void setDataType(DataType type) {
        this.dataType = type;
        if (dataType == DataType.XY_CONTINUOUS) {
            builder = new InstanceXYFactory<E>(this);
        }
    }

    @Override
    public DataType getDataType() {
        return this.dataType;
    }

    @Override
    public MetaStore getMetaStore() {
        return metaStore;
    }
}
