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

}
