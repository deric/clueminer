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
package org.clueminer.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.Database;
import org.clueminer.processor.spi.Processor;
import org.clueminer.project.api.Workspace;
import org.clueminer.spi.DatabaseImporter;
import org.clueminer.spi.FileImporter;
import org.clueminer.spi.Importer;
import org.clueminer.spi.ImporterUI;
import org.clueminer.spi.ImporterWizardUI;
import org.clueminer.types.FileType;
import org.openide.filesystems.FileObject;

/**
 * Controls process of importing data from files.
 *
 * @author Tomas Barton
 */
public interface ImportController {

    /**
     * Preview of file being imported.
     *
     * @param fileObject
     * @return
     */
    ImportTask preload(FileObject fileObject);

    ImportTask preload(InputStream stream, String importerName);

    ImportTask preload(Reader reader, String importerName);

    Container importFile(File file) throws FileNotFoundException;

    Container importFile(FileObject file, FileImporter importer) throws FileNotFoundException;

    /**
     *
     * @param file
     * @param reader
     * @param importer
     * @param reload   true when reloading same file
     * @return
     */
    Container importFile(FileObject file, Reader reader, FileImporter importer, boolean reload);

    /**
     *
     * @param file
     * @param stream
     * @param importer
     * @param reload   true when reloading same file
     * @return
     */
    Container importFile(FileObject file, InputStream stream, FileImporter importer, boolean reload);

    FileImporter getFileImporter(FileObject file);

    FileImporter getFileImporter(String importerName);

    void process(Container container);

    void process(Container container, Processor processor, Workspace workspace);

    FileType[] getFileTypes();

    /**
     * Checks support by extension
     *
     * @param file
     * @return
     */
    boolean isFileSupported(File file);

    /**
     * Checks if importers support given MIME type
     *
     * @param file
     * @return true when MIME type is supported by at least one importer
     */
    boolean isAccepting(File file);

    boolean isAccepting(FileObject file);

    ImporterUI getUI(Importer importer);

    ImporterWizardUI getWizardUI(Importer importer);

    Container importDatabase(Database database, DatabaseImporter importer);
}
