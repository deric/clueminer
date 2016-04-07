package org.clueminer.io.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Tomas Barton
 */
public interface SQLDriver {

    public String getPrefix();

    public Connection getConnection(String connectionUrl, String username, String passwd) throws SQLException;

    @Override
    public String toString();
}
