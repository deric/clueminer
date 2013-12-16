package org.clueminer.importer.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author deric
 */
public class Gui extends JFrame {
    private ImportPanel importPanel;

    public Gui() throws IOException {
        initComponents();

        File dir = new File(getClass().getProtectionDomain().getCodeSource().
                getLocation().getFile() + "/../../../../_data");
        String path = dir.getCanonicalPath() + "/" + "csv/Data_Milka_20131211_101069.csv";
        System.out.println("path: " + path);
        File file = new File(path);
        System.out.println("file exists? " + (new File(path)).exists());
        importPanel.setFile(file);

    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        Gui gui = new Gui();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(500, 500);
        gui.setVisible(true);
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
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new java.awt.Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.gridx = 0;

        importPanel = new ImportPanel();
        this.getContentPane().add(importPanel, c);
        this.pack();
    }

}
