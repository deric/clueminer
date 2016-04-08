package org.clueminer.io.importer.api;

import org.clueminer.dataset.api.Attribute;

/**
 * Draft is used during data import.
 *
 * @author Tomas Barton
 */
public interface AttributeDraft extends Attribute {

    Class<?> getJavaType();

    void setJavaType(Class<?> t);

    void setMeta(boolean b);

    void setNumerical(boolean b);

    boolean isUnique();

    void setUnique(boolean b);

    Object getDefaultValue();

    void setDefaultValue(Object value);

    /**
     *
     * @return true when attribute shouldn't be imported
     */
    boolean isSkipped();

    void setSkipped(boolean b);

    AttributeParser getParser();

}
