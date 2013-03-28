package org.clueminer.spi;

/**
 *
 * @author Tomas Barton
 */
public interface DatabaseImporterBuilder extends ImporterBuilder {

    /**
     * Builds a new database importer instance, ready to be used.
     *
     * @return a new database importer
     */
    @Override
    public DatabaseImporter buildImporter();
}
