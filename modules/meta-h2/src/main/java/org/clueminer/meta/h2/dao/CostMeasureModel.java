/*
 * Copyright (C) 2011-2017 clueminer.org
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

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 *
 * @author deric
 */
public interface CostMeasureModel {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS cost_measures(id INT auto_increment PRIMARY KEY,"
            + "name varchar(255),"
            + "measure varchar(255)"
            + ")")
    void createTable();

    @SqlQuery("SELECT id from cost_measures WHERE name = :name AND measure=:measure")
    int find(@Bind("name") String name, @Bind("measure") String measure);

    @SqlUpdate("insert into cost_measures (name,measure) values (:name,:measure)")
    @GetGeneratedKeys
    int insert(@Bind("name") String name, @Bind("measure") String measure);

}
