package org.clueminer.explorer.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.clueminer.clustering.api.factory.EvolutionExportFactory;
import org.clueminer.clustering.gui.EvolutionExport;

/**
 *
 * @author Tomas Barton
 */
public class ExportPanel extends JPanel {

    private JComboBox<String> cbType;
    private JPanel optPanel;
    private String selected = null;

    public ExportPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        EvolutionExportFactory factory = EvolutionExportFactory.getInstance();
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
        EvolutionExport exporter = factory.getProvider(expName);
        optPanel = exporter.getOptions();
        add(optPanel, c);
    }

    public EvolutionExport getExporter() {
        String expName = (String) cbType.getSelectedItem();
        return EvolutionExportFactory.getInstance().getProvider(expName);
    }

}
