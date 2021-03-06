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
package org.clueminer.meta.h2.dao;

import java.util.List;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 *
 * @author Tomas Barton
 */
public interface EvolutionModel {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS evolutions("
            + "id INT auto_increment PRIMARY KEY,"
            + "name VARCHAR(255)"
            + ")")
    void createTable();

    @SqlUpdate("insert into evolutions (name) values (:name)")
    @GetGeneratedKeys
    int insert(@Bind("name") String name);

    @SqlQuery("select id from evolutions where name = :name")
    int find(@Bind("name") String name);

    @SqlQuery("select name from evolutions")
    List<String> findAll();

}
