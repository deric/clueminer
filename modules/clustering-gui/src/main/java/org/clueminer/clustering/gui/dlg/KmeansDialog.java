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
package org.clueminer.clustering.gui.dlg;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.gui.ClusteringDialog;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = ClusteringDialog.class)
public class KmeansDialog extends JPanel implements ClusteringDialog {

    private static final long serialVersionUID = 7041147759279431292L;

    private JSlider sliderK;
    private JTextField tfK;
    private JTextField tfIterations;
    private JSlider sliderIter;
    private JTextField tfRandom;
    private JButton btnRandom;
    private Random rand;
    private JComboBox comboDistance;
    private static final String name = "k-means";

    public KmeansDialog() {
        initComponents();
        comboDistance.setSelectedItem("Euclidean");
    }

    @Override
    public String getName() {
        return name;
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        tfK = new JTextField("4", 4);
        sliderK = new JSlider(1, 1000, 4);
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

    @Override
    public Props getParams() {
        Props params = new Props();
        params.putInt(KMeans.K, sliderK.getValue());
        params.putInt(KMeans.ITERATIONS, sliderIter.getValue());
        params.putInt(KMeans.SEED, Integer.valueOf(tfRandom.getText()));
        params.put(KMeans.DISTANCE, (String) comboDistance.getSelectedItem());

        return params;
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public boolean isUIfor(ClusteringAlgorithm algorithm) {
        return algorithm instanceof KMeans;
    }

}
