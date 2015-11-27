package org.clueminer.dendrogram;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dgram.DgViewer;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;

/**
 * Should serve for testing DendroView component
 *
 * @author deric
 */
public class DendroView2 extends JFrame {

    private DendroViewer frame;
    private DendroToolbar toolbar;

    public DendroView2() {
        setLayout(new GridBagLayout());
        initComponents();

        final Dataset<? extends Instance> data = FakeDatasets.schoolData();

        Executor exec = new ClusteringExecutorCached();

        //cluster both rows and columns
        DendrogramMapping mapping = exec.clusterAll(data, new Props());
        frame.setDataset(mapping);

    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        DendroView2 hmf = new DendroView2();
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
                    Exceptions.printStackTrace(e);
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
    }

}
