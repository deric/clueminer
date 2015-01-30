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

    /**
     * Create tables for meta-storage
     */
    private void initialize() throws SQLException {
        Statement st = conn.createStatement();
        st.execute("create table datasets(id integer, name varchar(255),"
                + "num_attr INTEGER, num_inst INTEGER)");
    }

    public static String getDbDir() {
        return FileUtils.LocalFolder() + File.separatorChar + "db";
    }

    public Connection getConnection() throws SQLException {
        return getConnection(dbName);
    }

    public Connection getConnection(String db) throws SQLException {
        if (conn == null) {
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

    @Override
    public void add(String datasetName, Clustering<? extends Cluster> clustering) {

    }

    public long fetchDataset(String dataset) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery("select id from datasets WHERE name=" + dataset);
        long id = -1;
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
                id = generatedKeys.getLong(1);
            } else {
                // Throw exception?
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
