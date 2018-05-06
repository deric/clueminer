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

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 *
 * @author Tomas Barton
 */
public interface RunModel {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS runs("
            + "id INT auto_increment PRIMARY KEY,"
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "evolution_id INT,"
            + "dataset_id INT,"
            + "FOREIGN KEY(evolution_id) REFERENCES public.evolutions(id),"
            + "FOREIGN KEY(dataset_id) REFERENCES public.datasets(id)"
            + ")")
    void createTable();

    @SqlUpdate("insert into runs (evolution_id, dataset_id) values (:evo_id, :dataset_id)")
    @GetGeneratedKeys
    int insert(@Bind("evo_id") int evolutionId, @Bind("dataset_id") int datasetId);

    //check that given run exists
    @SqlQuery("SELECT dataset_id from runs WHERE id = :id")
    int find(@Bind("id") int id);
}
