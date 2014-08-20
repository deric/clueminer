package org.clueminer.dendrogram;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.aggl.HAC;
import org.clueminer.clustering.algorithm.HCL;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.OptimalTreeOrder;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dgram.eval.MOLO;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author deric
 */
public class ClustFight extends JFrame {

    private static final long serialVersionUID = -8137741651924993813L;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);
    private DendroPanel panel1;
    private DendroPanel panel2;

    public ClustFight() throws Exception {
        super("ClustFight Frame");
        initComponents();

        RP.execute(new Runnable() {
            @Override
            public void run() {
                Dataset<? extends Instance> dataset = FakeDatasets.schoolData();
                panel1.setDataset(dataset);
                panel1.setAlgorithm(new HCL());
                panel2.setDataset(dataset);
                panel2.setAlgorithm(new HAC());
                panel1.execute();
                HierarchicalResult res = panel2.execute();
                OptimalTreeOrder order = new MOLO();
                order.optimize(res);
            }
        });

        //dendro.setDataset(new ClusterDataset(generateRandom(15, 5)));
        setVisible(true);
    }

    private void initComponents() {
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints c = new GridBagConstraints();

        panel1 = new HclDendroPanel();
        panel2 = new HclDendroPanel();

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHEAST;
        c.weightx = c.weighty = 8.0; //ratio for filling the frame space
        gbl.setConstraints(panel1, c);
        this.add(panel1, c);

        c.gridx = 1;
        gbl.setConstraints(panel2, c);
        this.add(panel2, c);
        setVisible(true);
    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        ClustFight hmf = new ClustFight();
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
