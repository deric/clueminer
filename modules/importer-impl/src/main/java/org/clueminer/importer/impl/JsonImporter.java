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
package org.clueminer.importer.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.exception.ParserError;
import org.clueminer.importer.Issue;
import org.clueminer.io.JsonLoader;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.io.importer.api.Report;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.spi.FileImporter;
import org.clueminer.types.FileType;
import org.clueminer.utils.progress.Progress;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Load data into provisional storage with weak typing (for checking parser issues).
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = FileImporter.class)
public class JsonImporter<E extends InstanceDraft> extends BaseImporter<E> implements FileImporter<E>, LongTask {

    private static final String NAME = "JSON";
    private static final Logger LOGGER = Logger.getLogger(JsonImporter.class.getName());

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean execute(Container<E> container, Reader reader, int limit) throws IOException {
        this.container = container;
        if (container.getFile() != null) {
            LOGGER.log(Level.INFO, "importing file {0}", container.getFile().getName());
        }
        container.reset(); //remove all previous instances
        container.setDataset(null);
        container.setNumberOfLines(0);
        this.report = new Report();
        importData(container, reader, limit);
        LOGGER.log(Level.INFO, "number of attributes = {0}", container.getAttributeCount());

        fireAnalysisFinished();

        return !cancel;
    }

    @Override
    public boolean isAccepting(Collection mimeTypes) {
        String mime = mimeTypes.toString();
        //this will match pretty much anything
        return mime.contains("text") || mime.contains("octet-stream");
    }

    @Override
    public FileType[] getFileTypes() {
        FileType ft = new FileType(".json", NbBundle.getMessage(getClass(), "fileType_JSON_Name"));
        return new FileType[]{ft};
    }

    @Override
    public boolean isMatchingImporter(FileObject fileObject) {
        String ext = fileObject.getExt();
        return ext.equalsIgnoreCase("json");
    }

    @Override
    public void reload(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reload(FileObject file, Reader reader) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean cancel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void importData(Container loader, Reader reader, int limit) throws IOException {
        //if it's not the first time we are trying to load the file,
        //number of lines will be known
        int numLines = loader.getNumberOfLines();
        if (numLines > 0) {
            //if we know number of lines
            Progress.switchToDeterminate(progressTicket, numLines);
        } else {
            Progress.start(progressTicket);
        }

        int count;
        int prev = -1;
        boolean reading = true;

        JsonLoader jsonLoader = new JsonLoader();
        try {
            //try parsing json and load into draft dataset
            jsonLoader.load(reader, (Dataset) loader);
        } catch (FileNotFoundException ex) {
            report.logIssue(new Issue(ex.getMessage(), Issue.Level.SEVERE));
        } catch (ParserError ex) {
            report.logIssue(new Issue(ex.getMessage(), Issue.Level.WARNING));
        }


        /* while (reader.ready() && reading) {
         * String line = reader.readLine(); count = reader.getLineNumber();
         * //logger.log(Level.INFO, "line {0}: {1}", new Object[]{count, line});
         * if (line != null && !line.isEmpty()) {
         * lineRead(loader, count, line);
         * }
         * //we should have read a next line, but we didn't
         * if (count == prev) {
         * reading = false;
         * logger.log(Level.WARNING, "exitting reading input because no data has been read. Got to line #{0}: {1}", new Object[]{count, line});
         * }
         * prev = count;
         * } */
        loader.setNumberOfLines(prev);
        //close the input
        reader.close();
        Progress.finish(progressTicket);
    }
}
