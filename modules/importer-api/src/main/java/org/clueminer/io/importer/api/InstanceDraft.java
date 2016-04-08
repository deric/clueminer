package org.clueminer.io.importer.api;

/**
 * Stores data during import before casted to actual types.
 *
 * @author Tomas Barton
 */
public interface InstanceDraft {

    String getId();

    void setId(String id);

    int size();

    void setType(Object type);

    Object getType();

    /**
     * Value for attribute specified by the key
     *
     * @param key attribute identification
     * @return
     */
    Object getValue(String key);

    /**
     * Value of ith attribute
     *
     * @param i th attribute
     * @return
     */
    Object getObject(int i);

    void setValue(String key, Object value);

    void setValue(int index, Object value);

}
