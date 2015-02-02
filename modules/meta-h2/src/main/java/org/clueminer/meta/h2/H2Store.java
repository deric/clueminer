package org.clueminer.meta.h2;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.factory.EvaluationFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.meta.api.MetaStorage;
import org.clueminer.utils.FileUtils;
import org.h2.jdbcx.JdbcConnectionPool;
import org.openide.util.Exceptions;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

/**
 *
 * @author Tomas Barton
 */
public class H2Store implements MetaStorage {

    private static H2Store instance;
    private Connection conn = null;
    private static final String dbName = "meta-db";
    private DBI dbi;

    public static H2Store getInstance() {
        if (instance == null) {
            instance = new H2Store();
        }
        return instance;
    }

    private H2Store() {
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
        Handle dh = dbi.open();
        dh.begin();

        dh.execute("CREATE TABLE IF NOT EXISTS datasets("
                + "id INT auto_increment PRIMARY KEY,"
                + "name varchar(255),"
                + "num_attr INT,"
                + "num_inst INT"
                + ")");
        dh.commit();

        dh.execute("CREATE TABLE IF NOT EXISTS partitionings("
                + "id INT auto_increment PRIMARY KEY,"
                + "k INT," //number of clusters
                + "hash BIGINT,"
                + "num_occur INT,"
                + "dataset_id INT,"
                + "FOREIGN KEY(dataset_id) REFERENCES public.datasets(id)"
                + ")");
        dh.commit();

        //base algorithms
        dh.execute("CREATE TABLE IF NOT EXISTS algorithms("
                + "id INT auto_increment PRIMARY KEY,"
                + "name VARCHAR(255)"
                + ")");
        dh.commit();

        dh.execute("CREATE TABLE IF NOT EXISTS templates("
                + "id INT auto_increment PRIMARY KEY,"
                + "template CLOB,"
                + "algorithm_id INT,"
                + "FOREIGN KEY(algorithm_id) REFERENCES public.algorithms(id)"
                + ")");
        dh.commit();

        dh.execute("CREATE TABLE IF NOT EXISTS results("
                + "id INT auto_increment PRIMARY KEY,"
                + "template_id INT,"
                + "partitioning_id INT,"
                + "FOREIGN KEY(template_id) REFERENCES public.templates(id),"
                + "FOREIGN KEY(partitioning_id) REFERENCES public.partitionings(id)"
                + ")");
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
        dh.close();
    }

    @Override
    public void add(String datasetName, Clustering<? extends Cluster> clustering) {
        try {
            Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
            int datasetId;
            if (dataset == null) {
                datasetId = fetchDataset(datasetName);
            } else {
                datasetId = fetchDataset(dataset);
            }

            add(datasetId, clustering);
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void add(int datasetId, Clustering<? extends Cluster> clustering) {

    }

    public int fetchDataset(String name) throws SQLException {
        int id;
        id = findDataset(name);

        if (id < 0) {
            Handle h = db().open();
            h.createStatement(
                    "INSERT INTO datasets(name, num_attr, num_inst) VALUES (:name)")
                    .bind("name", name)
                    .execute();
            h.close();
        }
        //TODO: fix this if we can get inserted ID
        id = findDataset(name);

        return id;
    }

    protected int fetchDataset(Dataset<? extends Instance> dataset) {
        int id;
        id = findDataset(dataset.getName());

        if (id < 0) {
            Handle h = db().open();
            Statement st = h.createStatement(
                    "INSERT INTO datasets(name, num_attr, num_inst) VALUES (:name,:num_attr,:num_inst)")
                    .bind("name", dataset.getName())
                    .bind("num_attr", dataset.attributeCount())
                    .bind("num_inst", dataset.size()).getContext().getStatement();
            id = lastId(st);
            h.close();
        }
        return id;
    }

    /**
     * Retrieve last inserted ID
     *
     * @param st
     * @return
     */
    protected int lastId(Statement st) {
        int id = -1;
        try {
            st.getGeneratedKeys();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        return id;
    }

    protected int findDataset(String name) {
        int id = -1;
        Handle h = db().open();
        List<Map<String, Object>> rs = h.select("SELECT id from datasets WHERE name=?", name);
        if (rs.size() == 1) {
            Map<String, Object> row = rs.get(0);
            id = (Integer) row.get("id");
        }
        h.close();
        return id;
    }

    protected int findPartitioning(Clustering<? extends Cluster> clustering) {
        int id = -1;
        Handle h = db().open();
        List<Map<String, Object>> rs = h.select(
                "SELECT id from partitionings WHERE k = ? AND hash=?",
                clustering.size(), clustering.hashCode());
        if (rs.size() == 1) {
            Map<String, Object> row = rs.get(0);
            id = (Integer) row.get("id");
        }
        h.close();
        return id;
    }

    @Override
    public double findScore(String datasetName, Clustering<? extends Cluster> clustering, ClusterEvaluation eval) {
        double res = Double.NaN;
        int dataset_id = findDataset(datasetName);

        return res;
    }

    public void close() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

}
