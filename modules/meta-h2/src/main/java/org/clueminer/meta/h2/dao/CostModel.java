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
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Store computing costs of algorithms
 *
 * @author deric
 */
public interface CostModel {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS costs(id INT auto_increment PRIMARY KEY,"
            + "cost_measure_id INT,"
            + "value DOUBLE,"
            + "params CLOB," //JSON {param1:value,param2:value}
            + "FOREIGN KEY(cost_measure_id) REFERENCES public.cost_measures(id)"
            + ")")
    void createTable();

    @SqlUpdate("insert into costs (cost_measure_id,value,params) values (:cost_measure_id,:value,:params)")
    @GetGeneratedKeys
    int insert(@Bind("cost_measure_id") int costMeasureId, @Bind("value") double value, @Bind("params") String params);
}
