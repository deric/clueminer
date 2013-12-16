package org.clueminer.importer.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import org.clueminer.types.FileType;
import org.clueminer.importer.ImportController;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.Database;
import org.clueminer.processor.spi.Processor;
import org.clueminer.project.api.Workspace;
import org.clueminer.spi.DatabaseImporter;
import org.clueminer.spi.FileImporter;
import org.clueminer.spi.Importer;
import org.clueminer.spi.ImporterUI;
import org.clueminer.spi.ImporterWizardUI;

/**
 *
 * @author Tomas Barton
 */
public class ImportControllerImpl implements ImportController {

    @Override
    public Container importFile(File file) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Container importFile(File file, FileImporter importer) throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Container importFile(Reader reader, FileImporter importer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Container importFile(InputStream stream, FileImporter importer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FileImporter getFileImporter(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FileImporter getFileImporter(String importerName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void process(Container container) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void process(Container container, Processor processor, Workspace workspace) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FileType[] getFileTypes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isFileSupported(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImporterUI getUI(Importer importer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ImporterWizardUI getWizardUI(Importer importer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Container importDatabase(Database database, DatabaseImporter importer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
