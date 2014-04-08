package org.clueminer.io.importer.api;

import org.clueminer.dataset.api.AttributeRole;

/**
 *
 * @author Tomas Barton
 */
public interface AttributeDraft {

    String getName();

    void setName(String name);

    Object getType();

    void setType(Object t);

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
