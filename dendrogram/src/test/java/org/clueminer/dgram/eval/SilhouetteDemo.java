package org.clueminer.dgram.eval;

import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.fixtures.clustering.FakeClustering;
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
        setLayout(new BorderLayout());
        sPanel = new SilhouettePlot();

        final Clustering<? extends Cluster> data = FakeClustering.iris();
        sPanel.setClustering(data);
        System.out.println("dataset size: " + data.size());
        add(sPanel);
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
                    System.err.println(e);
                    e.printStackTrace();
                }
            }
        });
    }
}
