package org.clueminer.meta.h2.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 *
 * @author Tomas Barton
 */
public interface PartitioningModel {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS partitionings("
            + "id INT auto_increment PRIMARY KEY,"
            + "k INT," //number of clusters
            + "hash INT,"
            + "num_occur INT,"
            + "dataset_id INT,"
            + "FOREIGN KEY(dataset_id) REFERENCES public.datasets(id)"
            + ")")
    void createTable();

    @SqlQuery("select hash from partitionings where id = :id")
    int findHash(@Bind("id") int id);

}
