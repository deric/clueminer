package org.clueminer.io.importer.api;

import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeRole;

/**
 * Draft is used during data import.
 *
 * @author Tomas Barton
 */
public interface AttributeDraft extends Attribute {

    String getName();

    void setName(String name);

    Class<?> getJavaType();

    void setType(Class<?> t);

    boolean isMeta();

    void setMeta(boolean b);

    boolean isNumerical();

    void setNumerical(boolean b);

    boolean isUnique();

    void setUnique(boolean b);

    Object getDefaultValue();

    void setDefaultValue(Object value);

    int getIndex();

    void setIndex(int index);

    /**
     * Role of the attribute input/meta/id etc.
     *
     * @return
     */
    AttributeRole getRole();

    void setRole(AttributeRole role);

    /**
     *
     * @return true when attribute shouldn't be imported
     */
    boolean isSkipped();

    void setSkipped(boolean b);

    AttributeParser getParser();

}
