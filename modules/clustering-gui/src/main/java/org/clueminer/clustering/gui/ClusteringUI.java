/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.clustering.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringFactory;
import org.clueminer.flow.api.FlowPanel;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class ClusteringUI extends JPanel implements FlowPanel {

    private JComboBox<String> comboAlg;
    private String selected = null;
    private ClusteringDialog dialog = null;
    private JPanel optPanel;

    public ClusteringUI() {
        initComponents();
    }

    @Override
    public Props getParams() {
        if (dialog != null) {
            return dialog.getParams();
        } else {
            throw new RuntimeException("missing dialog");
        }
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        ClusteringFactory factory = ClusteringFactory.getInstance();

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0.1;
        c.weighty = 0.5;
        c.insets = new java.awt.Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;

        JPanel firstRow = new JPanel();
        firstRow.setLayout(new FlowLayout(FlowLayout.LEFT));
        firstRow.add(new JLabel("Algorithm:"));

        //add(new JLabel("Algorithm:"), c);
        comboAlg = new JComboBox(factory.getProvidersArray());
        if (selected != null) {
            comboAlg.setSelectedItem(selected);
        }
        firstRow.add(comboAlg, c);
        add(firstRow, c);

        comboAlg.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selected = (String) comboAlg.getSelectedItem();
                removeAll();
                initComponents();
                repaint();
                revalidate();
            }
        });

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.fill = GridBagConstraints.NONE;
        String expName = (String) comboAlg.getSelectedItem();
        ClusteringAlgorithm alg = factory.getProvider(expName);
        optPanel = getUI(alg);
        add(optPanel, c);
    }

    private JPanel getUI(ClusteringAlgorithm alg) {
        for (ClusteringDialog dlg : ClusteringDialogFactory.getInstance().getAll()) {
            if (dlg.isUIfor(alg, null)) {
                dialog = dlg;
                return dlg.getPanel();
            }
        }
        //last resort
        return new JPanel();
    }

}
