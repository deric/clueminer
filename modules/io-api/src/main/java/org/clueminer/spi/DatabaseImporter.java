package org.clueminer.spi;

import org.clueminer.io.importer.api.Database;

/**
 *
 * @author Tomas Barton
 */
public interface DatabaseImporter extends Importer {

    /**
     * Sets the database description, connexions details and queries
     *
     * @param database the database that is to be used to import
     */
    public void setDatabase(Database database);

    /**
     * Returns the current database description, connexions details and queries
     *
     * @return the database that is to be used to import
     */
    public Database getDatabase();
}
