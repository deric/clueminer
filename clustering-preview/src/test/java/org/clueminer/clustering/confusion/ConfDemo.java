package org.clueminer.clustering.confusion;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import javax.swing.JFrame;
import org.clueminer.fixtures.clustering.FakeClustering;

public class ConfDemo extends JFrame {

    private static final long serialVersionUID = 861272115283587449L;
    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private ConfusionTable confTable;



    public ConfDemo() throws IOException {
        initComponents();

        confTable.setClusterings(FakeClustering.iris(), FakeClustering.irisWrong());
    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        ConfDemo gui = new ConfDemo();
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
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = WEST_INSETS;

        confTable = new ConfusionTable();
        this.getContentPane().add(confTable, c);
        this.pack();
    }

}
