package org.clueminer.io.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Tomas Barton
 */
public class MySQLDriver implements SQLDriver {

    @Override
    public Connection getConnection(String connectionUrl, String username, String passwd) throws SQLException {
        return DriverManager.getConnection(connectionUrl, username, passwd);
    }

    @Override
    public String getPrefix() {
        return "mysql";
    }

    @Override
    public String toString() {
        return "MySQL";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MySQLDriver) {
            return ((MySQLDriver) obj).getPrefix().equals(getPrefix());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getPrefix().hashCode();
    }
}
