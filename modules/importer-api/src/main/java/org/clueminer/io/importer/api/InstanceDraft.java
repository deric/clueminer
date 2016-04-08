package org.clueminer.io.importer.api;

import org.clueminer.dataset.api.Instance;

/**
 * Stores data during import before casted to actual types.
 *
 * @author Tomas Barton
 */
public interface InstanceDraft extends Instance {

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

    void setObject(String key, Object value);

    void setObject(int index, Object value);

}
