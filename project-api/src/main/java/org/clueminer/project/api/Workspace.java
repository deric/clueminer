package org.clueminer.project.api;

import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public interface Workspace extends Lookup.Provider {

    /**
     * Adds an instance to this workspaces lookup.
     *
     * @param instance the instance that is to be pushed to the lookup
     */
    public void add(Object instance);

    /**
     * Removes an instance from this workspaces lookup.
     *
     * @param instance the instance that is to be removed from the lookup
     */
    public void remove(Object instance);

}
