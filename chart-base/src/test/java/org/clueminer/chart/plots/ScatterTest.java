package org.clueminer.chart.plots;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;

/**
 * Should serve for testing DendroView component
 *
 * @author deric
 */
public class ScatterTest extends JFrame {

    private ScatterPlot frame;

    public ScatterTest() {
        setLayout(new GridBagLayout());
        initComponents();

        final Dataset<? extends Instance> data = FakeDatasets.schoolData();
        frame.setDataset(data);

    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        ScatterTest hmf = new ScatterTest();
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
        frame = new ScatterPlot();
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

    }

}
