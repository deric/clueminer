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
