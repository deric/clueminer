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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.clueminer.clustering.algorithm.DBSCAN;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
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
public class DBSCANDialog extends JPanel implements ClusteringDialog {

    private JSlider sliderMinPts;
    private JTextField tfMinPts;
    private JTextField tfRadius;
    private JSlider sliderRadius;
    private JComboBox comboDistance;
    private static final String name = "DBSCAN";

    public DBSCANDialog() {
        initComponents();
        comboDistance.setSelectedItem("Euclidean");
    }

    @Override
    public String getName() {
        return name;
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        tfMinPts = new JTextField("20", 4);
        sliderMinPts = new JSlider(2, 1000, 20);
        sliderMinPts.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tfMinPts.setText(String.valueOf(sliderMinPts.getValue()));
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
        add(new JLabel("min pts:"), c);
        c.gridx = 1;
        c.weightx = 0.9;
        add(sliderMinPts, c);
        c.gridx = 2;
        add(tfMinPts, c);
        tfMinPts.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                updateMinPtsSlider();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                updateMinPtsSlider();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateMinPtsSlider();
            }
        });

        //iterations
        c.gridx = 0;
        c.gridy++;
        add(new JLabel("Radius:"), c);
        sliderRadius = new JSlider(0, 1000);
        sliderRadius.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tfRadius.setText(String.valueOf(sliderRadius.getValue()));
            }
        });
        c.gridx = 1;
        add(sliderRadius, c);

        c.gridx = 2;
        tfRadius = new JTextField("100", 10);
        add(tfRadius, c);
        tfRadius.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                updateRadiusSlider();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                updateRadiusSlider();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateRadiusSlider();
            }
        });
        sliderRadius.setValue(5);

        //distance measure
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Distance:"), c);
        c.gridx = 1;
        comboDistance = new JComboBox(DistanceFactory.getInstance().getProvidersArray());
        add(comboDistance, c);
    }

    private void updateMinPtsSlider() {
        try {
            int val = Integer.valueOf(tfMinPts.getText());
            sliderMinPts.setValue(val);
        } catch (NumberFormatException ex) {
            // wrong input so we do not set the slider but also do not want to raise an exception
        }
    }

    private void updateRadiusSlider() {
        try {
            int val = Integer.valueOf(tfRadius.getText());
            sliderRadius.setValue(val);
        } catch (NumberFormatException ex) {
            // wrong input so we do not set the slider but also do not want to raise an exception
        }
    }

    @Override
    public Props getParams() {
        Props params = new Props();
        params.putInt(DBSCAN.MIN_PTS, sliderMinPts.getValue());
        params.putDouble(DBSCAN.RADIUS, sliderRadius.getValue());
        params.put(AbstractClusteringAlgorithm.DISTANCE, (String) comboDistance.getSelectedItem());

        return params;
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public boolean isUIfor(ClusteringAlgorithm algorithm) {
        return algorithm instanceof DBSCAN;
    }

}
