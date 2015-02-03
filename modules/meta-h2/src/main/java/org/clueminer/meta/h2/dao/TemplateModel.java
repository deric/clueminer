package org.clueminer.meta.h2.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 *
 * @author Tomas Barton
 */
public interface TemplateModel {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS templates("
            + "id INT auto_increment PRIMARY KEY,"
            + "template CLOB,"
            + "algorithm_id INT,"
            + "FOREIGN KEY(algorithm_id) REFERENCES public.algorithms(id)"
            + ")")
    void createTable();

    @SqlQuery("select name from templates where id = :id")
    String findTemplate(@Bind("id") int id);

    @SqlQuery("select id from    templates where algorithm_id = :alg AND template = :template")
    int find(@Bind("alg") int algId, @Bind("template") String template);

    @SqlUpdate("insert into templates (template, algorithm_id)"
            + " values (:template, :alg)")
    @GetGeneratedKeys
    int insert(@Bind("alg") int algId, @Bind("template") String template);

}
