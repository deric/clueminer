package org.clueminer.meta.h2.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 *
 * @author Tomas Barton
 */
public interface RunModel {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS runs("
            + "id INT auto_increment PRIMARY KEY,"
            + "evolution_id INT,"
            + "dataset_id INT,"
            + "FOREIGN KEY(evolution_id) REFERENCES public.evolutions(id),"
            + "FOREIGN KEY(dataset_id) REFERENCES public.datasets(id)"
            + ")")
    void createTable();

    @SqlUpdate("insert into runs (evolution_id, dataset_id) values (:evo_id, :dataset_id)")
    @GetGeneratedKeys
    int insert(@Bind("evo_id") int evolutionId, @Bind("dataset_id") int datasetId);

    //check that given run exists
    @SqlQuery("SELECT dataset_id from runs WHERE id = :id")
    int find(@Bind("k") int id);
}
