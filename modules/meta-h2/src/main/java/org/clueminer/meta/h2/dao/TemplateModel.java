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
 * @author Tomas Barton
 */
public interface TemplateModel {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS templates("
            + "id INT auto_increment PRIMARY KEY,"
            + "template CLOB,"
            + "algorithm_id INT,"
            + "FOREIGN KEY(algorithm_id) REFERENCES public.algorithms(id)"
            + ")")
    void createTable();

    @SqlQuery("select name from templates where id = :id")
    String findTemplate(@Bind("id") int id);

    @SqlQuery("select id from    templates where algorithm_id = :alg AND template = :template")
    int find(@Bind("alg") int algId, @Bind("template") String template);

    @SqlUpdate("insert into templates (template, algorithm_id)"
            + " values (:template, :alg)")
    @GetGeneratedKeys
    int insert(@Bind("alg") int algId, @Bind("template") String template);

}
