package org.clueminer.meta.h2.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 *
 * @author Tomas Barton
 */
public interface AlgorithmModel {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS algorithms("
            + "id INT auto_increment PRIMARY KEY,"
            + "name VARCHAR(255)"
            + ")")
    void createTable();

    @SqlQuery("select name from algorithms where id = :id")
    String findName(@Bind("id") int id);

}
