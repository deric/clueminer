package org.clueminer.dendrogram;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.clueminer.clustering.api.factory.ClusteringExportFactory;
import org.clueminer.clustering.gui.ClusteringExport;

/**
 *
 * @author Tomas Barton
 */
public class FileExportDialog extends JPanel {

    private static final long serialVersionUID = 7587292725427287296L;

    private JComboBox<String> cbType;
    private JPanel optPanel;
    private String selected = null;

    public FileExportDialog() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        ClusteringExportFactory factory = ClusteringExportFactory.getInstance();
        cbType = new JComboBox<>(factory.getProvidersArray());
        if (selected != null) {
            cbType.setSelectedItem(selected);
        }
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 1;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.NONE;
        add(cbType, c);
        cbType.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selected = (String) cbType.getSelectedItem();
                removeAll();
                initComponents();
                repaint();
                revalidate();
            }
        });

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        String expName = (String) cbType.getSelectedItem();
        ClusteringExport exporter = factory.getProvider(expName);
        optPanel = exporter.getOptions();
        add(optPanel, c);
    }

    public ClusteringExport getExporter() {
        String expName = (String) cbType.getSelectedItem();
        ClusteringExport exporter = ClusteringExportFactory.getInstance().getProvider(expName);
        return exporter;
    }

}
