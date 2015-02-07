package org.clueminer.meta.h2.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
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
            + "fingerprint CLOB," //sizes of clusters, e.g. [1,3,5]
            + "num_occur INT,"
            + "dataset_id INT,"
            + "FOREIGN KEY(dataset_id) REFERENCES public.datasets(id)"
            + ")")
    void createTable();

    @SqlQuery("select hash from partitionings where id = :id")
    int findHash(@Bind("id") int id);

    @SqlQuery("SELECT id from partitionings WHERE k = :k AND hash=:hash")
    int find(@Bind("k") int k, @Bind("hash") int hash);

    @SqlUpdate("insert into partitionings (k, hash, fingerprint, num_occur, dataset_id)"
            + " values (:k, :hash, :fingerprint, 1, :dataset_id)")
    @GetGeneratedKeys
    int insert(@Bind("k") int k, @Bind("hash") int hash,
            @Bind("fingerprint") String fingerprint, @Bind("dataset_id") int datasetId);

}
