/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.dendrogram;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.clueminer.clustering.api.factory.ClusteringExportFactory;
import org.clueminer.clustering.gui.ClusteringExportGui;

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
        ClusteringExportGui exporter = factory.getProvider(expName);
        optPanel = exporter.getOptions();
        add(optPanel, c);
    }

    public ClusteringExportGui getExporter() {
        String expName = (String) cbType.getSelectedItem();
        ClusteringExportGui exporter = ClusteringExportFactory.getInstance().getProvider(expName);
        return exporter;
    }

}
