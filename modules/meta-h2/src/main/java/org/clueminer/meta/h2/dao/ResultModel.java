package org.clueminer.meta.h2.dao;

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
            + "FOREIGN KEY(template_id) REFERENCES public.templates(id),"
            + "FOREIGN KEY(partitioning_id) REFERENCES public.partitionings(id)"
            + ")")
    void createTable();

}
