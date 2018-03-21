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
