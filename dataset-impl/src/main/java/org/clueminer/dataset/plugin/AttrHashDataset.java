package org.clueminer.dataset.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 * Keep HashMap of attributes' names for fast lookup
 *
 * @author Tomas Barton
 */
public class AttrHashDataset<E extends Instance> extends SampleDataset<E> implements Dataset<E> {

    private static final long serialVersionUID = -240111429000747905L;
    protected HashMap<String, Integer> attrNames = new HashMap<String, Integer>();

    public AttrHashDataset(int capacity) {
        super(capacity);
    }

    /**
     * Set i-th attribute (column)
     *
     * @param i
     * @param attr
     */
    @Override
    public void setAttribute(int i, Attribute attr) {
        attr.setIndex(i);
        attributes.put(i, attr);
        attrNames.put(attr.getName(), i);
    }

    @Override
    public void setAttributes(Map<Integer, Attribute> attr) {
        this.attributes = attr;
        for (Entry<Integer, Attribute> item : attr.entrySet()) {
            setAttribute(item.getKey(), item.getValue());
        }
    }

    /**
     *
     * @param attributeName
     * @param instanceIdx
     * @return
     */
    @Override
    public double getAttributeValue(String attributeName, int instanceIdx) {
        int index = attrNames.get(attributeName).intValue();
        if (index > -1) {
            return getAttributeValue(index, instanceIdx);
        }
        throw new RuntimeException("attribute " + attributeName + " not found");
    }

    @Override
    public void setAttributeValue(String attributeName, int instanceIdx, double value) {
        int index = attrNames.get(attributeName).intValue();
        if (index > -1) {
            instance(instanceIdx).put(index, value);
        } else {
            throw new RuntimeException("attribute " + attributeName + " not found");
        }
    }

    @Override
    public Attribute getAttribute(String attributeName) {
        int idx = attrNames.get(attributeName);
        return attributes.get(idx);
    }

    @Override
    public Dataset<E> duplicate() {
        AttrHashDataset<E> copy = new AttrHashDataset<E>(this.size());
        copy.attrNames = this.attrNames;
        copy.setAttributes(attributes);
        return copy;
    }
}
