package org.clueminer.dendrogram;

import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.aggl.HAC;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.openide.util.RequestProcessor;

/**
 *
 * @author deric
 */
public class DendroDemo extends JFrame {

    private static final long serialVersionUID = 579590462477351303L;
    private HclDendroPanel dendroPanel;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);

    public DendroDemo() throws IOException, CloneNotSupportedException {
        setLayout(new BorderLayout());
        dendroPanel = new HclDendroPanel();

        final Dataset<? extends Instance> data = FakeDatasets.schoolData();

        System.out.println("dataset size: " + data.size());
        RP.execute(new Runnable() {
            @Override
            public void run() {
                dendroPanel.setDataset(data);
                dendroPanel.setAlgorithm(new HAC());
                //dendroPanel.setAlgorithm(new HCL());
                //dendroPanel.setAlgorithm(new HierarchicalAgglomerativeClustering());
                dendroPanel.execute();
            }
        });
        add(dendroPanel);
    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        DendroDemo hmf = new DendroDemo();
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
