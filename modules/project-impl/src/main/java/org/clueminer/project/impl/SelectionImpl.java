package org.clueminer.project.impl;

import java.io.Serializable;
import org.clueminer.project.api.Selection;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Tomas Barton
 */
public class SelectionImpl implements Selection, Lookup.Provider, Serializable {

    private static final long serialVersionUID = 7035905164152877230L;
    //Lookup
    private transient InstanceContent instanceContent;
    private transient AbstractLookup lookup;

    public SelectionImpl() {
        init();
    }

    private void init() {
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void add(Object object) {
        instanceContent.add(object);
    }

    @Override
    public void remove(Object object) {
        instanceContent.remove(object);
    }
}
