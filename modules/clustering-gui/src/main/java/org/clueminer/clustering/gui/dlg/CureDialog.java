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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.clueminer.clustering.algorithm.cure.CURE;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.gui.ClusteringDialog;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = ClusteringDialog.class)
public class CureDialog extends JPanel implements ClusteringDialog {

    private static final String name = "CURE";

    private JSlider sliderK;
    private JTextField tfK;
    private JTextField tfMinRepresent;
    private JTextField tfShrink;

    public CureDialog() {
        initComponents();
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
        //min representatives
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Min representatives:"), c);
        tfMinRepresent = new JTextField("5", 5);
        c.gridx = 1;
        add(tfMinRepresent, c);

        //shrink factor
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Shrink factor:"), c);
        tfShrink = new JTextField("0.5", 5);
        c.gridx = 1;
        add(tfShrink, c);

    }

    @Override
    public Props getParams() {
        Props params = new Props();
        params.putInt(CURE.K, sliderK.getValue());
        params.putInt(CURE.MIN_REPRESENTATIVES, Integer.valueOf(tfMinRepresent.getText()));

        return params;
    }

    @Override
    public JPanel getPanel() {
        return this;
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
        return algorithm instanceof CURE;
    }

}
