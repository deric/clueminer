package org.clueminer.io.importer.api;

/**
 *
 * @author Tomas Barton
 */
public interface InstanceDraft {

    public String getId();

    public void setId(String id);

    public int size();

    public void setType(Object type);

    public Object getType();

    /**
     * Value for attribute specified by the key
     *
     * @param key attribute identification
     * @return
     */
    public Object getValue(String key);

    /**
     * Value of ith attribute
     *
     * @param i th attribute
     * @return
     */
    public Object getValue(int i);

    public void setValue(String key, Object value);

    public void setValue(int index, Object value);

}
