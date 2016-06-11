/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.spi;

import java.io.IOException;
import java.io.Reader;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.InstanceDraft;
import org.openide.filesystems.FileObject;

/**
 * Interface for classes which imports data from files, databases, streams or
 * other sources.
 * <p>
 * Importers are built from {@link ImporterBuilder} services and can be
 * configured
 * by {@link ImporterUI} classes.
 *
 * @param <E>
 * @see ImportController
 */
public interface Importer<E extends InstanceDraft> {

    /**
     * Should be unique importer ID
     *
     * @return importer identification
     */
    String getName();

    /**
     * Tries to import data from reader into container. Then user can modify customize
     * import setting before container unloading.
     *
     * @param container
     * @param reader
     * @return
     * @throws java.io.IOException
     */
    boolean execute(Container<E> container, Reader reader) throws IOException;

    /**
     * Tries to import data from reader into container. Then user can modify customize
     * import setting before container unloading.
     *
     * @param container
     * @param reader
     * @param limit     when > 1 number of lines read will be limited
     * @return
     * @throws java.io.IOException
     */
    boolean execute(Container<E> container, Reader reader, int limit) throws IOException;

    /**
     * Tries to import data from FileObject into container. Then user can modify customize
     * import setting before container unloading.
     *
     * @param container
     * @param file
     * @return
     * @throws IOException
     */
    boolean execute(Container<E> container, FileObject file) throws IOException;

    /**
     * Returns the import container. The container is the import "result", all
     * data found during import are being pushed to the container.
     *
     * @return the import container
     */
    Container getContainer();

}
