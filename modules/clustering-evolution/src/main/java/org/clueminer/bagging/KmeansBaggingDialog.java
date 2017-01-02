/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.bagging;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.factory.ConsensusFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.api.factory.LinkageFactory;
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
public class KmeansBaggingDialog extends JPanel implements ClusteringDialog {

    private static final long serialVersionUID = -2318143730430154971L;

    private JSlider sliderK;
    private JTextField tfK;
    private JTextField tfIterations;
    private JSlider sliderIter;
    private JSlider sliderBagging;
    private JSlider sliderRelax;
    private JTextField tfBagging;
    private JTextField tfRandom;
    private JTextField tfRelax;
    private JButton btnRandom;
    private Random rand;
    private JComboBox comboDistance;
    private JComboBox comboMethod;
    private JComboBox comboLinkage;
    private JComboBox comboConsensus;
    private JComboBox comboObjective1;
    private JComboBox comboObjective2;
    private JCheckBox chckRandK;
    private static final String name = "k-means bagging";

    public KmeansBaggingDialog() {
        initComponents();
        //disable combos, if necessary
        methodChanged();
        comboDistance.setSelectedItem("Euclidean");
    }

    @Override
    public String getName() {
        return name;
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        tfK = new JTextField("4", 4);
        sliderK = new JSlider(2, 1000, 4);
        sliderK.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tfK.setText(String.valueOf(sliderK.getValue()));
            }
        });
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0.1;
        c.weighty = 1.0;
        c.insets = new java.awt.Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel("k:"), c);
        c.gridx = 1;
        c.weightx = 0.9;
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

        //iterations
        c.gridx = 0;
        c.gridy++;
        add(new JLabel("Iterations:"), c);
        sliderIter = new JSlider(10, 2000);
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
        sliderIter.setValue(100);

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
        add(comboDistance, c);

        //bagging
        c.gridx = 0;
        c.gridy++;
        add(new JLabel("Bagging:"), c);
        sliderBagging = new JSlider(2, 100);
        sliderBagging.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tfBagging.setText(String.valueOf(sliderBagging.getValue()));
            }
        });
        c.gridx = 1;
        add(sliderBagging, c);

        c.gridx = 2;
        tfBagging = new JTextField("5", 4);
        add(tfBagging, c);
        tfBagging.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                updateBaggingSlider();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                updateBaggingSlider();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateBaggingSlider();
            }
        });
        sliderBagging.setValue(5);

        //initialization method
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Mapping method:"), c);
        c.gridx = 1;
        comboMethod = new JComboBox(new String[]{"RANDOM", "MO"});
        add(comboMethod, c);
        comboMethod.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                methodChanged();
            }
        });

        //objectives (only for MO)
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Objective 1:"), c);
        c.gridx = 1;
        comboObjective1 = new JComboBox(InternalEvaluatorFactory.getInstance().getProvidersArray());
        add(comboObjective1, c);
        comboObjective1.setSelectedItem("AIC");

        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Objective 2:"), c);
        c.gridx = 1;
        comboObjective2 = new JComboBox(InternalEvaluatorFactory.getInstance().getProvidersArray());
        add(comboObjective2, c);
        comboObjective2.setSelectedItem("SD index");

        //rand k
        c.gridy++;
        c.gridx = 1;
        chckRandK = new JCheckBox("fixed k");
        add(chckRandK, c);

        //consensus function
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Consensus:"), c);
        c.gridx = 1;
        comboConsensus = new JComboBox(ConsensusFactory.getInstance().getProvidersArray());
        add(comboConsensus, c);

        //linkage (only for MO)
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Linkage:"), c);
        c.gridx = 1;
        comboLinkage = new JComboBox(LinkageFactory.getInstance().getProvidersArray());
        add(comboLinkage, c);

        //relax function
        //TODO: only for COMUSA consensus
        // can we display double values?
        c.gridx = 0;
        c.gridy++;
        add(new JLabel("Relax:"), c);
        sliderRelax = new JSlider(0, 20);
        sliderRelax.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tfRelax.setText(String.valueOf(sliderRelax.getValue()));
            }
        });
        c.gridx = 1;
        add(sliderRelax, c);

        c.gridx = 2;
        tfRelax = new JTextField("5", 4);
        add(tfRelax, c);
        tfRelax.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                updateRelaxSlider();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                updateRelaxSlider();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateRelaxSlider();
            }
        });
        sliderRelax.setValue(5);
    }

    private void updateKSlider() {
        try {
            int val = Integer.valueOf(tfK.getText());
            sliderK.setValue(val);
        } catch (NumberFormatException ex) {
            // wrong input so we do not set the slider but also do not want to raise an exception
        }
    }

    private void updateIterSlider() {
        try {
            int val = Integer.valueOf(tfIterations.getText());
            sliderIter.setValue(val);
        } catch (NumberFormatException ex) {
            // wrong input so we do not set the slider but also do not want to raise an exception
        }
    }

    private void updateBaggingSlider() {
        try {
            int val = Integer.valueOf(tfBagging.getText());
            sliderBagging.setValue(val);
        } catch (NumberFormatException ex) {
            // wrong input so we do not set the slider but also do not want to raise an exception
        }
    }

    private void updateRelaxSlider() {
        try {
            int val = Integer.valueOf(tfRelax.getText());
            sliderRelax.setValue(val);
        } catch (NumberFormatException ex) {
            // wrong input so we do not set the slider but also do not want to raise an exception
        }
    }

    @Override
    public Props getParams() {
        Props params = new Props();
        params.putInt(KMeans.K, sliderK.getValue());
        params.putInt(KMeans.ITERATIONS, sliderIter.getValue());
        params.putInt(KMeans.SEED, Integer.valueOf(tfRandom.getText()));
        params.put(KMeans.DISTANCE, (String) comboDistance.getSelectedItem());
        params.putInt(KMeansBagging.BAGGING, Integer.valueOf(tfBagging.getText()));
        params.put(KMeansBagging.INIT_METHOD, comboMethod.getSelectedItem());
        params.put(AlgParams.LINKAGE, comboLinkage.getSelectedItem());
        if (comboConsensus.getSelectedItem().equals(COMUSA.name)) {
            params.putDouble(COMUSA.RELAX, sliderRelax.getValue() / 10.0);
        }
        if (comboObjective1.isEnabled()) {
            params.put("mo_1", comboObjective1.getSelectedItem());
        }
        if (comboObjective2.isEnabled()) {
            params.put("mo_2", comboObjective2.getSelectedItem());
        }
        if (chckRandK.isSelected()) {
            params.put(KMeansBagging.FIXED_K, true);
        }
        params.put(KMeansBagging.CONSENSUS, comboConsensus.getSelectedItem());

        return params;
    }

    private void methodChanged() {
        switch (comboMethod.getSelectedItem().toString()) {
            case "MO":
                comboObjective1.setEnabled(true);
                comboObjective2.setEnabled(true);
                break;
            default:
                comboObjective1.setEnabled(false);
                comboObjective2.setEnabled(false);
                break;
        }
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public boolean isUIfor(ClusteringAlgorithm algorithm, Dataset dataset) {
        return algorithm instanceof KMeansBagging;
    }

}
