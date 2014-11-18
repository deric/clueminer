package org.clueminer.dataset.plugin;

import java.util.HashMap;
import java.util.Map;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public abstract class BaseDataset<E extends Instance> extends AbstractDataset<E> implements Dataset<E> {


    private static final long serialVersionUID = 4300989082762007729L;
    protected Map<Integer, Attribute> attributes = new HashMap<>();

    @Override
    public int attributeCount() {
        return attributes.size();
    }

    /**
     * Get name of i-th attribute
     *
     * @param i
     * @return
     */
    @Override
    public Attribute getAttribute(int i) {
        return attributes.get(i);
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
    }

    @Override
    public void setAttributes(Map<Integer, Attribute> attr){
        this.attributes = attr;
    }

    /**
     *
     * @return reference to attribute map
     */
    @Override
    public Map<Integer, Attribute> getAttributes(){
        return attributes;
    }

    @Override
    public Attribute[] copyAttributes() {
        return attributes.values().toArray(new Attribute[attributeCount()]);
    }
}
