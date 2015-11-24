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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.clueminer.chinesewhispers.ChineseWhispers;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.gui.ClusteringDialog;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.graph.api.GraphConvertorFactory;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = ClusteringDialog.class)
public class ChineseWhispersDialog extends JPanel implements ClusteringDialog {

    private static final long serialVersionUID = 3326362418875717835L;

    private JTextField tfIterations;
    private JSlider sliderIter;
    private JComboBox comboDistance;
    private JComboBox comboGraphConv;
    private JSlider sliderK;
    private JTextField tfK;

    public ChineseWhispersDialog() {
        initComponents();
        comboDistance.setSelectedItem("Euclidean");
        comboGraphConv.setSelectedItem("k-NN");
    }

    @Override
    public String getName() {
        return "Chinese Whispers dialog";
    }

    @Override
    public Props getParams() {
        Props params = new Props();
        params.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        if (sliderIter.getValue() > 0) {
            params.putInt(ChineseWhispers.MAX_ITERATIONS, sliderIter.getValue());
        }
        params.put(ChineseWhispers.DISTANCE, (String) comboDistance.getSelectedItem());
        params.put(ChineseWhispers.GRAPH_CONV, (String) comboGraphConv.getSelectedItem());
        params.putInt("k", sliderK.getValue());
        return params;
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0.1;
        c.weighty = 1.0;
        c.insets = new java.awt.Insets(5, 5, 5, 5);

        //iterations
        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel("Iterations:"), c);
        sliderIter = new JSlider(0, 2000);
        sliderIter.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tfIterations.setText(String.valueOf(sliderIter.getValue()));
            }
        });
        c.gridx = 1;
        add(sliderIter, c);

        c.gridx = 2;
        tfIterations = new JTextField("100", 4);
        add(tfIterations, c);
        tfIterations.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                updateIterSlider();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                updateIterSlider();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateIterSlider();
            }
        });
        sliderIter.setValue(50);
        //distance measure
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Distance:"), c);
        c.gridx = 1;
        comboDistance = new JComboBox(DistanceFactory.getInstance().getProvidersArray());
        add(comboDistance, c);

        //distance measure
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Graph initialization:"), c);
        c.gridx = 1;
        comboGraphConv = new JComboBox(GraphConvertorFactory.getInstance().getProvidersArray());
        add(comboGraphConv, c);

        //change k param for k-NN (only applies for k-nn initialization)
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("k:"), c);
        c.gridx = 1;
        c.weightx = 0.9;
        tfK = new JTextField("4", 4);
        sliderK = new JSlider(0, 1000, 4);
        sliderK.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tfK.setText(String.valueOf(sliderK.getValue()));
            }
        });
        add(sliderK, c);
        c.gridx = 2;
        add(tfK, c);
        tfK.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                updateKSlider();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                updateKSlider();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateKSlider();
            }
        });

    }

    private void updateIterSlider() {
        try {
            int val = Integer.valueOf(tfIterations.getText());
            sliderIter.setValue(val);
        } catch (NumberFormatException ex) {
            // wrong input so we do not set the slider but also do not want to raise an exception
        }
    }

    private void updateKSlider() {
        try {
            int val = Integer.valueOf(tfK.getText());
            sliderK.setValue(val);
        } catch (NumberFormatException ex) {
            // wrong input so we do not set the slider but also do not want to raise an exception
        }
    }

    @Override
    public boolean isUIfor(ClusteringAlgorithm algorithm, Dataset dataset) {
        return algorithm instanceof ChineseWhispers;
    }

}
