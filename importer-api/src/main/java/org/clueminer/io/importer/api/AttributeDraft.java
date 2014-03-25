package org.clueminer.io.importer.api;

/**
 *
 * @author Tomas Barton
 */
public interface AttributeDraft {

    public String getName();

    public void setName(String name);

    public Object getType();

    public void setType(Object t);

    public boolean isMeta();

    public void setMeta(boolean b);

    public boolean isNumerical();

    public void setNumerical(boolean b);

    public boolean isUnique();

    public void setUnique(boolean b);

    public Object getDefaultValue();

    public void setDefaultValue(Object value);

}
