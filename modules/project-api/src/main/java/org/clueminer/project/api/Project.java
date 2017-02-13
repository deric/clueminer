/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
