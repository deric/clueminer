/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.processor.spi;

import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.project.api.Workspace;

/**
 * Interface that define the way data are <b>unloaded</b> from container and
 * append to the workspace.
 * <p>
 * The purpose of processors is to unload data from
 * the import container and push it to the workspace, with various strategy. For
 * instance a processor could either create a new workspace or append data to
 * the current workspace, managing doubles.
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface Processor<E extends InstanceDraft> {

    /**
     * Process data <b>from</b> the container <b>to</b> the workspace. This task
     * is done after an importer pushed data to the container.
     *
     * @see Importer
     */
    void process();

    /**
     * Sets the data container. The processor's job is to get data from the
     * container and append it to the workspace.
     *
     * @param container the container where data are
     */
    void setContainer(Container<E> container);

    /**
     * Sets the destination workspace for the data in the container. If no
     * workspace is provided, the current workspace will be used.
     *
     * @param workspace the workspace where data are to be pushed
     */
    void setWorkspace(Workspace workspace);

    /**
     * Returns the processor name.
     *
     * @return the processor display name
     */
    String getDisplayName();
}
