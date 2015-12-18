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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.clueminer.ap.AffinityPropagation;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.gui.ClusteringDialog;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = ClusteringDialog.class)
public class AffinityPropagationDialog extends JPanel implements ClusteringDialog {

    private static final long serialVersionUID = 1327676005267510122L;

    private JTextField tfLambda;
    private JTextField tfMaxIter;

    public AffinityPropagationDialog() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0.1;
        c.weighty = 1.0;
        c.insets = new java.awt.Insets(5, 5, 5, 5);

        //lambda
        c.gridx = 0;
        c.gridy = 0;
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

    }

    @Override
    public String getName() {
        return "Affinity propagation";
    }

    @Override
    public Props getParams() {
        Props params = new Props();
        params.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        params.put(AffinityPropagation.DAMPING, Double.valueOf(tfLambda.getText()));
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
