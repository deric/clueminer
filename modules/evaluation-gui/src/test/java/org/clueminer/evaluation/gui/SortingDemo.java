package org.clueminer.evaluation.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JFrame;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class SortingDemo extends JFrame {

    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private SortingPanel evalPlot;

    public SortingDemo() throws IOException {
        initComponents();

        Collection<Clustering> clusterings = new HashSet<>(4);
        clusterings.add(FakeClustering.irisWrong4());
        clusterings.add(FakeClustering.irisWrong());
        clusterings.add(FakeClustering.irisWrong2());
        clusterings.add(FakeClustering.irisWrong5());

        evalPlot.setClusterings(clusterings);
    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        SortingDemo gui = new SortingDemo();
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

        evalPlot = new SortingPanel();
        this.getContentPane().add(evalPlot, c);
        this.pack();
    }

}
