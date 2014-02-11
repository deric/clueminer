package org.clueminer.importer.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.clueminer.importer.FileImporterFactory;
import org.clueminer.spi.FileImporter;

/**
 *
 * @author Tomas Barton
 */
public class ImportPanel extends JPanel {

    private File file;
    private JComboBox comboSeparator;
    private JComboBox comboImporter;
    private JTextField linesPreview;
    private JScrollPane scroller;
    private JPanel dataColumns;
    private FileImporter importer = new CsvImporter();

    public ImportPanel() {
        initComponents();
    }

    public void setFile(File file) {
        this.file = file;
        importer.setFile(file);
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0.0;
        c.weighty = 0.1;
        c.insets = new java.awt.Insets(0, 0, 0, 5);
        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel("Separator: "), c);
        c.weightx = 1.0;
        c.gridx = 1;
        comboSeparator = new JComboBox(new Object[]{",", ";", "\t", "|"});
        add(comboSeparator, c);

        //importer
        c.gridy = 1;
        c.gridx = 0;
        add(new JLabel("Importer: "), c);
        c.gridx = 1;
        comboImporter = new JComboBox(FileImporterFactory.getInstance().getProviders().toArray());
        add(comboImporter, c);

        //lines
        c.gridy = 2;
        c.gridx = 0;
        add(new JLabel("Number of lines: "), c);
        c.gridx = 1;
        linesPreview = new JTextField("10");
        add(linesPreview, c);

        c.gridy = 3;
        c.gridx = 0;
        dataColumns = new DataColumns();
        scroller = new JScrollPane(dataColumns);
        scroller.getViewport().setDoubleBuffered(true);
        scroller.setVisible(true);
        add(scroller, c);

    }

    public void setImporter(FileImporter importer) {
        this.importer = importer;
    }


}
