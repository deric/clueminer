package org.clueminer.meta.h2.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
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

    @SqlQuery("select id from datasets where name = :name")
    int findId(@Bind("name") String name);

    @SqlUpdate("insert into datasets (name, num_attr, num_inst) values (:name,:num_attr, :num_inst)")
    @GetGeneratedKeys
    int insert(@Bind("name") String name, @Bind("num_attr") int numAttr, @Bind("num_attr") int numInst);

}
