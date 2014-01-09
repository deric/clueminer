package org.clueminer.dataset.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeRole;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.events.DatasetEvent;
import org.clueminer.events.DatasetListener;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public abstract class AbstractDataset<E extends Instance> extends ArrayList<E> implements Dataset<E> {

    private static final long serialVersionUID = -7361108601629091897L;
    transient protected EventListenerList datasetListener;
    protected String id;
    protected String name;
    protected ColorGenerator colorGenerator;
    protected Dataset<? extends Instance> parent = null;
    //default capacity same as with ArrayList
    private int capacity = 10;
    protected HashMap<String, Dataset<Instance>> children;

    public AbstractDataset() {
        //do nothing
    }

    public AbstractDataset(int capacity) {
        super(capacity);
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
    public Dataset<? extends Instance> getParent() {
        return parent;
    }

    @Override
    public void setParent(Dataset<? extends Instance> parent) {
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

    @Override
    public void setAttributeValue(int attrIdx, int instanceIdx, double value) {
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
    public void ensureCapacity(int size) {
        this.capacity = size;
        super.ensureCapacity(capacity);
    }

    @Override
    public void addChild(String key, Dataset<Instance> dataset) {
        if (children == null) {
            children = new HashMap<String, Dataset<Instance>>(5);
        }
        children.put(key, dataset);
    }

    @Override
    public Dataset<Instance> getChild(String key) {
        if (children == null) {
            return null;
        }
        return children.get(key);
    }

    @Override
    public boolean hasIndex(int idx) {
        return idx >= 0 && idx < size();
    }

    @Override
    public Attribute[] attributeByRole(AttributeRole role) {
        List<Attribute> list = new LinkedList<Attribute>();

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
}
