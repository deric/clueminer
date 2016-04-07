package org.clueminer.io.importer.api;

import org.clueminer.io.db.SQLDriver;

/**
 *
 * @author Tomas Barton
 */
public interface Database {

    public String getName();

    public SQLDriver getSQLDriver();

    public String getHost();

    public int getPort();

    public String getUsername();

    public String getPasswd();

    public String getDBName();

    public void setName(String name);

    public void setSQLDriver(SQLDriver driver);

    public void setHost(String host);

    public void setPort(int port);

    public void setUsername(String username);

    public void setPasswd(String passwd);

    public void setDBName(String dbName);

}
