package org.clueminer.meta.h2;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.sql.DataSource;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.factory.EvaluationFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.meta.api.MetaStorage;
import org.clueminer.meta.h2.dao.AlgorithmModel;
import org.clueminer.meta.h2.dao.DatasetModel;
import org.clueminer.meta.h2.dao.PartitioningModel;
import org.clueminer.meta.h2.dao.ResultModel;
import org.clueminer.meta.h2.dao.TemplateModel;
import org.clueminer.utils.FileUtils;
import org.clueminer.utils.Props;
import org.h2.jdbcx.JdbcConnectionPool;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.GeneratedKeys;
import org.skife.jdbi.v2.Handle;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = MetaStorage.class)
public class H2Store implements MetaStorage {

    private static H2Store instance;
    private Connection conn = null;
    private static final String dbName = "meta-db";
    private DBI dbi;
    private static final String name = "H2 store";

    public static H2Store getInstance() {
        if (instance == null) {
            instance = new H2Store();
        }
        return instance;
    }

    public H2Store() {
        String dir = getDbDir();

        File path = new File(dir);

        // if the directory does not exist, create it
        if (!path.exists()) {
            try {
                path.mkdir();
            } catch (SecurityException se) {
                //handle it
                Exceptions.printStackTrace(se);
            }
        }
    }

    public static String getDbDir() {
        return FileUtils.LocalFolder() + File.separatorChar + "db";
    }

    @Override
    public String getName() {
        return name;
    }

    public DBI db() {
        return db(dbName);
    }

    public DBI db(String db) {
        if (dbi == null) {
            DataSource ds = JdbcConnectionPool.create("jdbc:h2:" + getDbDir() + File.separatorChar + db, "sa", "");
            dbi = new DBI(ds);

            try {
                initialize();
            } catch (SQLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return dbi;
    }

    /**
     * Delete database
     *
     * @param name
     */
    public void deleteDb(String name) {
        File f = new File(getDbDir() + File.separatorChar + name + ".db");
        if (f.exists()) {
            f.deleteOnExit();
        }
    }

    /**
     * Create tables for meta-storage
     */
    private void initialize() throws SQLException {
        try (Handle dh = dbi.open()) {
            dh.begin();

            DatasetModel dt = dh.attach(DatasetModel.class);
            dt.createTable();
            dh.commit();

            PartitioningModel pt = dh.attach(PartitioningModel.class);
            pt.createTable();
            dh.commit();

            //base algorithms
            AlgorithmModel at = dh.attach(AlgorithmModel.class);
            at.createTable();
            dh.commit();

            TemplateModel tt = dh.attach(TemplateModel.class);
            tt.createTable();
            dh.commit();

            ResultModel rt = dh.attach(ResultModel.class);
            rt.createTable();
            dh.commit();

            //update score names
            EvaluationFactory ef = EvaluationFactory.getInstance();
            for (String eval : ef.getProviders()) {
                //evaluators are in quotes, therefore names are case sensitive
                dh.execute("ALTER TABLE results ADD COLUMN IF NOT EXISTS \"" + eval + "\" DOUBLE");
            }

            dh.commit();

            /* getting all column names */
            /*   Statement statement = dh.getConnection().createStatement();
             ResultSet results = statement.executeQuery("SELECT * FROM results LIMIT 1");
             ResultSetMetaData metadata = results.getMetaData();
             int columnCount = metadata.getColumnCount();

             HashSet<String> hash = new HashSet<>(columnCount);
             for (int i = 1; i <= columnCount; i++) {
             hash.add(metadata.getColumnName(i));
             System.out.println("col " + i + ": " + metadata.getColumnName(i));
             }*/
        }
    }

    @Override
    public void add(Dataset<? extends Instance> dataset, Clustering<? extends Cluster> clustering) {
        add(fetchDataset(dataset), clustering);
    }

    public void add(int datasetId, Clustering<? extends Cluster> clustering) {
        int partitionId = fetchPartitioning(datasetId, clustering);
        Props p = clustering.getParams();
        int algId = fetchAlgorithm(p.get(AgglParams.ALG, "UNKNOWN"));
        int templateId = fetchTemplate(algId, p.toString());

        try (Handle h = db().open()) {
            ResultModel rm = h.attach(ResultModel.class);
            int rId = rm.insert(templateId, partitionId, datasetId);
            EvaluationTable evalTable = clustering.getLookup().lookup(EvaluationTable.class);
            if (evalTable != null) {
                StringBuilder sb = new StringBuilder("UPDATE results SET ");
                double val;
                int i = 0;
                for (Entry<String, Double> entry : evalTable.getAll().entrySet()) {
                    val = entry.getValue();
                    if (!Double.isNaN(val)) {
                        //evaluators are in quotes, therefore names are case sensitive
                        if (i > 0) {
                            sb.append(",");
                        }
                        sb.append("\"").append(entry.getKey()).append("\"='").append(val).append("'");
                        i++;
                    }
                    if (i > 0) {
                        sb.append(" WHERE id = ").append(rId);
                        h.execute(sb.toString());
                    }
                }
            }
        }
    }

    public int fetchDataset(String name) throws SQLException {
        int id = findDataset(name);

        if (id < 0) {
            try (Handle h = db().open()) {
                GeneratedKeys<Map<String, Object>> res = h.createStatement(
                        "INSERT INTO datasets(name, num_attr, num_inst) VALUES (:name)")
                        .bind("name", name)
                        .executeAndReturnGeneratedKeys();

                //TODO: this is rather complicated way of getting first value
                for (Entry<String, Object> e : res.first().entrySet()) {
                    id = (int) e.getValue();
                }
            }
        }
        return id;
    }

    protected int fetchDataset(Dataset<? extends Instance> dataset) {
        int id = findDataset(dataset.getName());

        if (id <= 0) {
            try (Handle h = db().open()) {
                GeneratedKeys<Map<String, Object>> res = h.createStatement(
                        "INSERT INTO datasets(name, num_attr, num_inst) VALUES (:name,:num_attr,:num_inst)")
                        .bind("name", dataset.getName())
                        .bind("num_attr", dataset.attributeCount())
                        .bind("num_inst", dataset.size()).executeAndReturnGeneratedKeys();
                //TODO: this is rather complicated way of getting first value
                for (Entry<String, Object> e : res.first().entrySet()) {
                    id = (int) e.getValue();
                }
            }
        }
        return id;
    }

    protected int findDataset(String name) {
        int id;
        try (Handle h = db().open()) {
            DatasetModel dt = h.attach(DatasetModel.class);
            id = dt.findId(name);
        }
        return id;
    }

    protected int findPartitioning(Clustering<? extends Cluster> clustering) {
        int id;
        try (Handle h = db().open()) {
            PartitioningModel pt = h.attach(PartitioningModel.class);
            id = pt.find(clustering.size(), clustering.hashCode());
        }
        return id;
    }

    protected int fetchPartitioning(int datasetId, Clustering<? extends Cluster> clustering) {
        int id = findPartitioning(clustering);

        if (id <= 0) {
            try (Handle h = db().open()) {
                PartitioningModel pm = h.attach(PartitioningModel.class);
                id = pm.insert(clustering.size(), clustering.hashCode(), datasetId);
            }
        }
        return id;
    }

    @Override
    public double findScore(Dataset<? extends Instance> dataset, Clustering<? extends Cluster> clustering, ClusterEvaluation eval) {
        double res = Double.NaN;
        int datasetId = fetchDataset(dataset);
        int partitioningId = fetchPartitioning(datasetId, clustering);

        try (Handle h = db().open()) {
            ResultModel rt = h.attach(ResultModel.class);
            res = rt.score(eval.getName(), partitioningId, datasetId);
        }

        return res;
    }

    protected int fetchAlgorithm(String name) {
        int id;
        try (Handle h = db().open()) {
            AlgorithmModel am = h.attach(AlgorithmModel.class);
            id = am.find(name);

            if (id <= 0) {
                id = am.insert(name);
            }
        }
        return id;
    }

    protected int fetchTemplate(int algId, String template) {
        int id;
        try (Handle h = db().open()) {
            TemplateModel tm = h.attach(TemplateModel.class);
            id = tm.find(algId, template);

            if (id <= 0) {
                id = tm.insert(algId, template);
            }
        }
        return id;
    }

    public void close() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @Override
    public Collection<? extends Evolution> getEvolutionaryAlgorithms() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
