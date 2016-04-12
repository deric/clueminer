package org.clueminer.io.importer.api;

import org.clueminer.io.db.SQLDriver;

/**
 *
 * @author Tomas Barton
 */
public interface Database {

    String getName();

    SQLDriver getSQLDriver();

    String getHost();

    int getPort();

    String getUsername();

    String getPasswd();

    String getDBName();

    void setName(String name);

    void setSQLDriver(SQLDriver driver);

    void setHost(String host);

    void setPort(int port);

    void setUsername(String username);

    void setPasswd(String passwd);

    void setDBName(String dbName);

}
