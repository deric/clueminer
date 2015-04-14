package org.clueminer.meta.h2.dao;

import java.util.List;
import org.clueminer.meta.api.MetaResult;
import org.clueminer.meta.h2.MetaResultMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/**
 *
 * @author Tomas Barton
 */
public interface ResultModel {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS results("
            + "id INT auto_increment PRIMARY KEY,"
            + "template_id INT NOT NULL,"
            + "partitioning_id INT NOT NULL,"
            + "dataset_id INT NOT NULL,"
            + "run_id INT,"
            + "FOREIGN KEY(template_id) REFERENCES public.templates(id),"
            + "FOREIGN KEY(partitioning_id) REFERENCES public.partitionings(id),"
            + "FOREIGN KEY(dataset_id) REFERENCES public.datasets(id),"
            + "FOREIGN KEY(run_id) REFERENCES public.runs(id)"
            + ")")
    void createTable();

    @SqlQuery("SELECT :score from results WHERE partitioning_id = :pid AND dataset_id = :did")
    double score(@Bind("score") String score, @Bind("pid") int partitionId, @Bind("did") int datasetId);

    @SqlUpdate("insert into results (template_id, partitioning_id, dataset_id)"
            + " values (:template_id, :partitioning_id, :dataset_id)")
    @GetGeneratedKeys
    int insert(@Bind("template_id") int templateId, @Bind("partitioning_id") int partitioningId, @Bind("dataset_id") int datasetId);

    @SqlUpdate("insert into results (template_id, partitioning_id, dataset_id, run_id)"
            + " values (:template_id, :partitioning_id, :dataset_id, :run_id)")
    @GetGeneratedKeys
    int insert(@Bind("template_id") int templateId, @Bind("partitioning_id") int partitioningId,
            @Bind("dataset_id") int datasetId, @Bind("run_id") int runId);

    //:score must be quoted
    @SqlQuery("SELECT p.k, t.template, r.* FROM results AS r"
            + " LEFT JOIN templates t"
            + " ON r.template_id = t.id"
            + " LEFT JOIN partitionings p"
            + " ON r.partitioning_id = p.id"
            + " LEFT JOIN runs ru"
            + " ON r.run_id = ru.id"
            + " WHERE r.dataset_id = :dataset_id AND ru.evolution_id = :evolution_id")
    @Mapper(MetaResultMapper.class)
    List<MetaResult> findAll(@Bind("dataset_id") int datasetId,
            @Bind("evolution_id") int evolutionId, @Bind("score") String score);

}
