package org.clueminer.importer.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import javax.swing.JFrame;
import org.clueminer.importer.impl.CsvImporter;
import org.clueminer.importer.impl.ImportControllerImpl;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.Report;

/**
 *
 * @author deric
 */
public class Gui2 extends JFrame {

    private ReportPanel reportPanel;

    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);

    public Gui2() throws IOException {
        initComponents();

        File dir = new File(getClass().getProtectionDomain().getCodeSource().
                getLocation().getFile() + "/../../../../../_data");
        String path = dir.getCanonicalPath() + "/" + "csv/Data_Milka_20131219_100260.csv";
        File file = new File(path);
        System.out.println("file exists? " + file.exists());
        System.out.println("file " + file.getAbsolutePath());

        //importPanel.setFile(file);
        BufferedReader br = new BufferedReader(new FileReader(file));
        LineNumberReader reader = new LineNumberReader(br);

        ImportControllerImpl controller = new ImportControllerImpl();
        Container container = controller.importFile(reader, new CsvImporter(), false);
        Report report = container.getReport();
        reportPanel.setData(report, container);
    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        Gui2 gui = new Gui2();
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

        reportPanel = new ReportPanel();
        this.getContentPane().add(reportPanel, c);
        this.pack();
    }

}
