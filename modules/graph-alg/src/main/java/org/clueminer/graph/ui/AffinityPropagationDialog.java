/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.graph.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.clueminer.ap.AffinityPropagation;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.gui.ClusteringDialog;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = ClusteringDialog.class)
public class AffinityPropagationDialog extends JPanel implements ClusteringDialog {

    private static final long serialVersionUID = 1327676005267510122L;

    private JTextField tfPreference;
    private JTextField tfLambda;
    private JTextField tfMaxIter;
    private JTextField tfMaxConvIter;
    private JComboBox comboDistance;
    private JCheckBox chckPref;

    public AffinityPropagationDialog() {
        initComponents();
        comboDistance.setSelectedItem("Euclidean");
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0.1;
        c.weighty = 1.0;
        c.insets = new java.awt.Insets(5, 5, 5, 5);

        c.gridx = 0;
        c.gridy = 0;

        //distance measure
        add(new JLabel("Distance:"), c);
        c.gridx = 1;
        comboDistance = new JComboBox(DistanceFactory.getInstance().getProvidersArray());
        add(comboDistance, c);

        //preference
        c.gridx = 0;
        c.gridy++;
        add(new JLabel("Preference:"), c);
        c.gridx++;
        tfPreference = new JTextField("-50", 4);
        tfPreference.setEnabled(false);
        add(tfPreference, c);
        chckPref = new JCheckBox("auto", true);
        c.gridx++;
        add(chckPref, c);
        chckPref.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tfPreference.setEnabled(!chckPref.isSelected());
            }
        });

        //lambda
        c.gridx = 0;
        c.gridy++;
        add(new JLabel("Damping (lambda):"), c);
        c.gridx = 1;
        tfLambda = new JTextField("0.5", 4);
        add(tfLambda, c);

        //max iterations
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Max. iterations:"), c);
        tfMaxIter = new JTextField("100", 4);
        c.gridx = 1;
        add(tfMaxIter, c);

        //max convergence iterations
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Max. convergence iter.:"), c);
        tfMaxConvIter = new JTextField("100", 4);
        c.gridx = 1;
        add(tfMaxConvIter, c);

    }

    @Override
    public String getName() {
        return "Affinity propagation";
    }

    @Override
    public Props getParams() {
        Props params = new Props();
        params.put(AffinityPropagation.DAMPING, Double.valueOf(tfLambda.getText()));
        params.put(AffinityPropagation.MAX_ITERATIONS, Integer.valueOf(tfMaxIter.getText()));
        params.put(AffinityPropagation.CONV_ITER, Integer.valueOf(tfMaxConvIter.getText()));
        params.put(Algorithm.DISTANCE, (String) comboDistance.getSelectedItem());
        if (!chckPref.isSelected()) {
            params.put(AffinityPropagation.PREFERENCE, Double.valueOf(tfPreference.getText()));
        }
        return params;
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public boolean isUIfor(ClusteringAlgorithm algorithm, Dataset dataset) {
        return algorithm instanceof AffinityPropagation;
    }

}
