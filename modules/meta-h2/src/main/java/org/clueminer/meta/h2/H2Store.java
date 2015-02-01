package org.clueminer.meta.h2;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.meta.api.MetaStorage;
import org.clueminer.utils.FileUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class H2Store implements MetaStorage {

    private static H2Store instance;
    private Connection conn = null;
    private static final String dbName = "meta-db";

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

    public Connection getConnection() throws SQLException {
        return getConnection(dbName);
    }

    public Connection getConnection(String db) throws SQLException {
        if (conn == null || conn.isClosed()) {
            try {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection("jdbc:h2:" + getDbDir() + File.separatorChar + db, "sa", "");
                initialize();
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return conn;
    }

    /**
     * Create tables for meta-storage
     */
    private void initialize() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS datasets("
                    + "id INT PRIMARY KEY, name varchar(255),"
                    + "num_attr INTEGER,"
                    + "num_inst INTEGER)");

            st.execute("CREATE TABLE IF NOT EXISTS partitionings("
                    + "id INT PRIMARY KEY,"
                    + "k INT," //number of clusters
                    + "hash BIGINT,"
                    + "num_occur INT,"
                    + "dataset_id INT,"
                    + "FOREIGN KEY(dataset_id) REFERENCES public.datasets(id)"
                    + ")");

            st.execute("CREATE TABLE IF NOT EXISTS templates("
                    + "id INT PRIMARY KEY,"
                    + "template CLOB,"
                    + "algorithm_id INT,"
                    + "FOREIGN KEY(algorithm_id) REFERENCES public.algorithms(id)"
                    + ")");

            //base algorithms
            st.execute("CREATE TABLE IF NOT EXISTS algorithms("
                    + "id INT PRIMARY KEY,"
                    + "name VARCHAR(255)"
                    + ")");

            st.execute("CREATE TABLE IF NOT EXISTS results("
                    + "id INT PRIMARY KEY,"
                    + "template_id INT,"
                    + "partitioning_id INT,"
                    + "FOREIGN KEY(template_id) REFERENCES public.templates(id),"
                    + "FOREIGN KEY(partitioning_id) REFERENCES public.partitionings(id)"
                    + ")");
        }
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

    public int fetchDataset(String dataset) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery("select id from datasets WHERE name=" + dataset);
        int id = -1;
        int rowcount = 0;
        while (rset.next()) {
            rowcount++;
            id = rset.getInt(1);
        }
        if (rowcount == 0) {
            String sql = "INSERT INTO datasets(col) VALUES (?)";
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, dataset);
            st.executeUpdate();
            ResultSet generatedKeys = st.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            } else {
                throw new RuntimeException("insert into datasets failed");
            }

        }
        return id;
    }

    private int fetchDataset(Dataset<? extends Instance> dataset) throws SQLException {
        String sql = "SELECT id from datasets WHERE name=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, dataset.getName());
        ResultSet rset = stmt.executeQuery();
        int id = -1;
        int rowcount = 0;
        while (rset.next()) {
            rowcount++;
            id = rset.getInt(1);
        }
        if (rowcount == 0) {
            sql = "INSERT INTO datasets(col,num_attr, num_inst) VALUES (?,?,?)";
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, dataset.getName());
            st.setInt(2, dataset.attributeCount());
            st.setInt(3, dataset.size());
            st.executeUpdate();
            ResultSet generatedKeys = st.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            } else {
                throw new RuntimeException("insert into datasets failed");
            }
        }
        return id;
    }

    @Override
    public double findScore(String datasetName, Clustering<? extends Cluster> clustering, ClusterEvaluation eval) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void close() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

}
