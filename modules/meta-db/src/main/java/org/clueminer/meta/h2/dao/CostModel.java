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
import org.clueminer.meta.api.CostMeasurement;
import org.clueminer.meta.h2.CostMeasurementMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

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

    @SqlQuery("SELECT c.id,name,measure,value,params FROM cost_measures AS m"
            + " LEFT JOIN costs c"
            + " ON c.cost_measure_id = m.id"
            + " WHERE m.name = :name AND m.measure = :measure")
    @Mapper(CostMeasurementMapper.class)
    List<CostMeasurement> findAll(@Bind("name") String name, @Bind("measure") String measure);
}
