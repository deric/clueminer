package org.clueminer.importer.impl;

import org.clueminer.io.importer.api.InstanceDraft;

/**
 *
 * @author Tomas Barton
 */
public class InstanceDraftImpl implements InstanceDraft {

    private String id;
    private String label;
    private Object type;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setType(Object type) {
        this.type = type;
    }

    @Override
    public Object getType() {
        return type;
    }

}
