package org.clueminer.clustering.confusion;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import javax.swing.JFrame;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class ConfSingleDataset extends JFrame {

    private static final long serialVersionUID = 861272115283587449L;
    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private ConfusionMatrix confMatrix;

    public ConfSingleDataset() throws IOException {
        initComponents();

        confMatrix.setClustering(FakeClustering.irisWrong4());

    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        ConfSingleDataset gui = new ConfSingleDataset();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(500, 500);
        gui.setVisible(true);
    }

    public static void main(String[] args) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    createAndShowGUI();
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
            }
        });
        t.start();
    }

    private void initComponents() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = WEST_INSETS;

        confMatrix = new ConfusionMatrix();
        this.getContentPane().add(confMatrix, c);
        this.pack();
    }

}
