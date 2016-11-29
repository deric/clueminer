package org.clueminer.dendrogram;

import com.google.common.collect.ImmutableMap;
import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.chameleon.Chameleon;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tomas Bruna
 */
public class DendroChameleon extends JFrame {

    private static final long serialVersionUID = 579590462477351303L;
    private HacDendroPanel dendroPanel;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);

    public DendroChameleon() throws IOException, CloneNotSupportedException {
        setLayout(new BorderLayout());

        ImmutableMap<String, Dataset<? extends Instance>> map = new ImmutableMap.Builder<String, Dataset<? extends Instance>>()
                .put("iris", FakeDatasets.irisDataset())
                .put("US arrests", FakeDatasets.usArrestData())
                .build();

        dendroPanel = new HacDendroPanel(map);

        RP.execute(new Runnable() {
            @Override
            public void run() {

                dendroPanel.setAlgorithm(new Chameleon());
                //dendroPanel.setAlgorithm(new HAC());
                //dendroPanel.setAlgorithm(new HCL());
                //dendroPanel.setAlgorithm(new HierarchicalAgglomerativeClustering());
                dendroPanel.execute();
            }
        });
        add(dendroPanel);
    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        DendroChameleon hmf = new DendroChameleon();
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
                    Exceptions.printStackTrace(e);
                }
            }
        });
    }
}
