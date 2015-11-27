package org.clueminer.dendrogram;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.InputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dgram.DgViewer;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.report.MemInfo;
import org.clueminer.utils.Props;

/**
 * Should serve for testing DendroView component
 *
 * @author deric
 */
public class DendroView extends JFrame {

    private DendroViewer frame;
    private DendroToolbar toolbar;
    private static final Logger logger = Logger.getLogger(DendroView.class.getName());

    public DendroView() {
        setLayout(new GridBagLayout());
        Logger.getGlobal().setLevel(Level.FINEST);

        // Log a FINE tracing message
        logger.fine("doing stuff");
        try {
            initComponents();
        } catch (Exception ex) {
            // Log the exception
            logger.log(Level.WARNING, "trouble sneezing", ex);
        }
        logger.fine("done");

        final Dataset<? extends Instance> data = FakeDatasets.irisDataset();

        Executor exec = new ClusteringExecutorCached();

        Props prop = new Props();
        prop.put(AgglParams.LINKAGE, SingleLinkage.name);
        MemInfo mem = new MemInfo();
        Clustering clust = exec.clusterRows(data, prop);
        mem.report();
        frame.setClustering(clust);
    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("logging.properties");
//        LogManager.getLogManager().readConfiguration(is);

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINER);
        Logger.getAnonymousLogger().addHandler(consoleHandler);

        DendroView hmf = new DendroView();
        hmf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        hmf.setSize(500, 500);
        hmf.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    createAndShowGUI();
                } catch (Exception e) {
                    System.err.println(e);
                    e.printStackTrace();
                }
            }
        });
    }

    private void initComponents() {
        frame = new DgViewer();
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(0, 0, 0, 0);
        add((Component) frame, c);

        toolbar = new DendroToolbar(frame);
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0;
        add(toolbar, c);
        pack();
    }

}
