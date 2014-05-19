package org.clueminer.dendrogram;

import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.algorithm.HCL;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.fixtures.CommonFixture;
import org.clueminer.io.CsvLoader;
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

        final Dataset<Instance> data = new SampleDataset<Instance>();
        CommonFixture tf = new CommonFixture();

        CsvLoader loader = new CsvLoader();
        loader.setClassIndex(4);
        loader.setSeparator(' ');
        loader.setDataset(data);
        loader.addNameAttr(4);
        loader.load(tf.schoolData());

        System.out.println("dataset size: " + data.size());
        RP.execute(new Runnable() {
            @Override
            public void run() {
                dendroPanel.setDataset(data);
                dendroPanel.setAlgorithm(new HCL());
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
