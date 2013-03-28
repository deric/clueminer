package org.clueminer.project.api;

import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public interface Project extends Lookup.Provider {

    /**
     * Adds an abilities to this project.
     *
     * @param instance the instance that is to be added to the lookup
     */
    public void add(Object instance);

    /**
     * Removes an abilities to this project.
     *
     * @param instance the instance that is to be removed from the lookup
     */
    public void remove(Object instance);

    /**
     * Get project's title
     *
     * @return project's name
     */
    public String getName();

    @Override
    public Lookup getLookup();
    
    /** 
     * Lookup for currently selected objects
     * @return 
     */
    public Selection getSelection();
}
