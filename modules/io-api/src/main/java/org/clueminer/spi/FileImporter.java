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
package org.clueminer.spi;

import java.io.File;
import java.io.Reader;
import java.util.Collection;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.types.FileType;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface FileImporter<E extends InstanceDraft> extends Importer<E> {

    /**
     * Return true if importer supports given MIME type
     *
     * @param mimeTypes Collection of String, String[] and MimeType objects
     * @return
     */
    boolean isAccepting(Collection mimeTypes);

    /**
     * Get default file types this importer can deal with.
     *
     * @return an array of file types this importer can read
     */
    FileType[] getFileTypes();

    /**
     * Returns <code>true</code> if this importer can import
     * <code>fileObject</code>. Called from
     * controllers to identify dynamically which importers can be used for a
     * particular file format.
     * <p>
     * Use <code>FileObject.getExt()</code> to retrieve file extension. Matching
     * can be done not only with
     * metadata but also with file content. The <code>fileObject</code> can be
     * read in that way.
     *
     * @param fileObject the file in input
     * @return <code>true</code> if the importer is compatible with
     *         <code>fileObject</code> or <code>false</code>
     *         otherwise
     */
    boolean isMatchingImporter(FileObject fileObject);

    /**
     * Reload import (with new importer settings)
     *
     * @param file
     */
    void reload(File file);

    /**
     *
     * @param file
     * @param reader
     */
    void reload(final FileObject file, Reader reader);

    /**
     * Add listener to events invoked by importer (pre-loading data finished
     * etc.)
     *
     * @param listener
     */
    void addAnalysisListener(AnalysisListener listener);

    /**
     * Remove listener
     *
     * @param listener
     */
    void removeListener(AnalysisListener listener);

    /**
     * Check whether data has already been imported
     *
     * @return true when data are available
     */
    boolean hasData();

}
