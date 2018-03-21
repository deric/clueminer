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
package edu.umn.cluto;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
public class ClutoDialog extends JPanel implements ClusteringDialog {

    private static final String name = "CLUTO";
    private static final long serialVersionUID = -3597832454334820059L;
    private JSlider sliderK;
    private JTextField tfK;
    private JTextField tfM;
    private JComboBox cmbMethod;
    private JComboBox cmbDist;
    private JComboBox cmbCrfun;
    private Dataset currDataset;

    public ClutoDialog() {
        initComponents();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Props getParams() {
        Props params = new Props();
        params.putInt(Cluto.K, sliderK.getValue());
        params.put(Cluto.CLMETHOD, cmbMethod.getSelectedItem().toString());
        params.put(Cluto.SIM, cmbDist.getSelectedItem().toString());
        params.put(Cluto.CRFUN, cmbCrfun.getSelectedItem().toString());
        params.put(Cluto.AGGLOFROM, tfM.getText());

        return params;
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

        //clmethod
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("method:"), c);
        c.gridx = 1;
        c.weightx = 0.9;
        cmbMethod = new JComboBox(new String[]{"graph", "rb", "rbr", "direct", "agglo", "bagglo"});
        cmbMethod.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cmbMethod.getSelectedItem().equals("graph")) {
                    cmbDist.setEnabled(false);
                } else {
                    cmbDist.setEnabled(true);
                }
            }
        });
        add(cmbMethod, c);

        //dist
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("distance:"), c);
        c.gridx = 1;
        c.weightx = 0.9;
        cmbDist = new JComboBox(new String[]{"dist", "cos", "corr", "jacc"});
        cmbDist.setEnabled(false);
        add(cmbDist, c);

        //crfun
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("criterion:"), c);
        c.gridx = 1;
        c.weightx = 0.9;
        cmbCrfun = new JComboBox(new String[]{"i1", "i2", "e1", "g1", "g1p", "h1", "h2", "slink", "wslink", "clink", "wclink", "upgma"});
        add(cmbCrfun, c);

        //agglofrom
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("m:"), c);
        c.gridx = 1;
        c.weightx = 0.9;
        tfM = new JTextField("30", 5);
        add(tfM, c);

    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public boolean isUIfor(ClusteringAlgorithm algorithm, Dataset dataset) {
        if (algorithm instanceof Cluto) {
            if (dataset != null && currDataset != dataset) {
                int clsSize = dataset.getClasses().size();
                clsSize = clsSize > 0 ? clsSize : 4;
                tfK.setText(String.valueOf(clsSize));
                sliderK.setValue(clsSize);
                //make sure user can update value of k
                currDataset = dataset;
            }
            return true;
        }
        return false;
    }

    private void updateKSlider() {
        try {
            int val = Integer.valueOf(tfK.getText());
            sliderK.setValue(val);
            int m = Integer.valueOf(tfM.getText());
            if (val < m) {
                tfM.setText(String.valueOf(m));
            }
        } catch (NumberFormatException ex) {
            // wrong input so we do not set the slider but also do not want to raise an exception
        }
    }

}
