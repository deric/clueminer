package org.clueminer.scatter.matrix;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import javax.swing.JFrame;
import org.clueminer.fixtures.clustering.FakeClustering;

/**
 *
 * @author deric
 */
public class MatrixDemoGral extends JFrame {

    private static final long serialVersionUID = 861272115283587449L;
    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private ScatterMatrixGralPanel matrix;

    public MatrixDemoGral() throws IOException {
        initComponents();

        matrix.setClustering(FakeClustering.iris());
    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        MatrixDemoGral gui = new MatrixDemoGral();
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
                    System.err.println(e);
                    e.printStackTrace();
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

        matrix = new ScatterMatrixGralPanel();
        this.getContentPane().add(matrix, c);
        this.pack();
    }

}
