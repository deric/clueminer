package org.clueminer.dgram.eval;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author deric
 */
public class SilhouetteDemo extends JFrame {

    private static final long serialVersionUID = 579590462477351303L;
    private SilhouettePlot sPanel;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);

    public SilhouetteDemo() throws IOException, CloneNotSupportedException {
        setLayout(new GridBagLayout());
        sPanel = new SilhouettePlot(true);

        Props props = new Props();
        final Clustering<Instance, Cluster<Instance>> data = FakeClustering.irisWrong();
        HClustResult hres = new HClustResult(data.getLookup().lookup(Dataset.class), props);
        hres.setClustering(data);
        hres.createMapping();

        sPanel.setClustering(hres, data);
        System.out.println("dataset size: " + data.size());
        add(sPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        SilhouetteDemo hmf = new SilhouetteDemo();
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
}
