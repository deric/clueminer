package org.clueminer.meta.h2.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
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

    @SqlUpdate("insert into runs (name) values (:name)")
    @GetGeneratedKeys
    int insert(@Bind("name") String name);

}
