package org.clueminer.project.api;

import org.openide.util.Lookup;

/**
 * Represent current selection in context of project, a single instance or
 * multiple instances of different classes could be selected at the same time
 *
 * @author Tomas Barton
 */
public interface Selection extends Lookup.Provider {

    /**
     * Add object to current selection
     *
     * @param instance
     */
    public void add(Object object);

    /**
     * Removes object from current selection
     *
     * @param object
     */
    public void remove(Object object);

    @Override
    public Lookup getLookup();
}
