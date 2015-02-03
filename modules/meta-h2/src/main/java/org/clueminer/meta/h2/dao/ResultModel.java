package org.clueminer.meta.h2.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 *
 * @author Tomas Barton
 */
public interface ResultModel {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS results("
            + "id INT auto_increment PRIMARY KEY,"
            + "template_id INT,"
            + "partitioning_id INT,"
            + "dataset_id INT,"
            + "FOREIGN KEY(template_id) REFERENCES public.templates(id),"
            + "FOREIGN KEY(partitioning_id) REFERENCES public.partitionings(id),"
            + "FOREIGN KEY(dataset_id) REFERENCES public.datasets(id)"
            + ")")
    void createTable();

    @SqlQuery("SELECT \":score\" from results WHERE partitioning_id = :pid AND dataset_id = :did")
    double score(@Bind("score") String score, @Bind("pid") int partitionId, @Bind("did") int datasetId);

    @SqlUpdate("insert into results (template_id, partitioning_id, dataset_id)"
            + " values (:template_id, :partitioning_id, :dataset_id)")
    @GetGeneratedKeys
    int insert(@Bind("template_id") int templateId, @Bind("partitioning_id") int partitioningId, @Bind("dataset_id") int datasetId);

}
