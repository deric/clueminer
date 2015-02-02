package org.clueminer.meta.h2.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 *
 * @author Tomas Barton
 */
public interface DatasetModel {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS datasets(id INT auto_increment PRIMARY KEY,"
            + "name varchar(255),"
            + "num_attr INT, "
            + "num_inst INT)")
    void createTable();

    @SqlQuery("select name from datasets where id = :id")
    String findName(@Bind("id") int id);

}
