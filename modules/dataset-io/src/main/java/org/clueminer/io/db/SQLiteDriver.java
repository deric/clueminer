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
package org.clueminer.io.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 */
public class SQLiteDriver implements SQLDriver {

    public SQLiteDriver() {
        try {
            // load the sqlite-JDBC driver using the current class loader
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            LoggerFactory.getLogger(SQLiteDriver.class).warn("failed to load SQLite driver", ex);
        }
    }

    @Override
    public Connection getConnection(String connectionUrl, String username, String passwd) throws SQLException {
        String url = connectionUrl.substring(connectionUrl.indexOf("//") + 2);
        connectionUrl = "jdbc:sqlite:" + url.substring(0, url.length() - 2);
        return DriverManager.getConnection(connectionUrl, username, passwd);
    }

    @Override
    public String getPrefix() {
        return "sqlite";
    }

    @Override
    public String toString() {
        return "SQLite";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SQLiteDriver) {
            return ((SQLiteDriver) obj).getPrefix().equals(getPrefix());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getPrefix().hashCode();
    }
}
