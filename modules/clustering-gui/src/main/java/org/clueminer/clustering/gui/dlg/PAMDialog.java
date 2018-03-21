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
package org.clueminer.clustering.gui.dlg;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.algorithm.PAM;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.SeedSelectionFactory;
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
public class PAMDialog extends JPanel implements ClusteringDialog {

    private static final String NAME = "k-means";
    private static final long serialVersionUID = -4676433745154958224L;
    private JComboBox<String> comboDistance;
    private JComboBox<String> comboPrototypes;
    private JTextField tfRandom;
    private JButton btnRandom;
    private Random rand;
    private JTextField tfK;

    public PAMDialog() {
        initComponents();
    }

    @Override
    public String getName() {
        return NAME;
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

        //k
        tfK = new JTextField("4", 4);
        add(new JLabel("k:"), c);
        c.gridx = 1;
        add(tfK, c);

        //prototypes
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Prototypes selection:"), c);
        c.gridx = 1;
        comboPrototypes = new JComboBox(SeedSelectionFactory.getInstance().getProvidersArray());
        add(comboPrototypes, c);

        //random
        tfRandom = new JTextField("-1", 8);
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Random seed:"), c);
        c.gridx = 1;
        add(tfRandom, c);
        btnRandom = new JButton("Randomize");
        rand = new Random();
        btnRandom.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tfRandom.setText(String.valueOf(rand.nextInt()));
            }
        });
        c.gridx = 2;
        add(btnRandom, c);


        //distance measure
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Distance:"), c);
        c.gridx = 1;
        comboDistance = new JComboBox(DistanceFactory.getInstance().getProvidersArray());
        comboDistance.setSelectedItem("Euclidean");
        add(comboDistance, c);
    }

    @Override
    public Props getParams() {
        Props params = new Props();
        params.putInt(KMeans.K, Integer.valueOf(tfK.getText()));
        //params.putInt(KMeans.ITERATIONS, sliderIter.getValue());
        params.put(PAM.SEED_SELECTION, comboPrototypes.getSelectedItem());
        params.putInt(KMeans.SEED, Integer.valueOf(tfRandom.getText()));
        params.put(KMeans.DISTANCE, comboDistance.getSelectedItem());

        return params;
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public boolean isUIfor(ClusteringAlgorithm algorithm, Dataset dataset) {
        return algorithm instanceof PAM;
    }

}
