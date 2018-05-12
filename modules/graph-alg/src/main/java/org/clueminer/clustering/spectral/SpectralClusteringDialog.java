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
package org.clueminer.clustering.spectral;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JCheckBox;
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
 * @author mikusmi1
 */
@ServiceProvider(service = ClusteringDialog.class)
public class SpectralClusteringDialog extends JPanel implements ClusteringDialog {

    private JTextField tfSigma;
    private JSlider sliderK;
    private JTextField tfK;
    private JTextField tfKMeansIterations;
    private JSlider sliderKMeansIter;
    private JComboBox comboMatrixConv;
    private JComboBox comboSpAlg;
    private JSlider sliderKnnK;
    private JTextField tfKnnK;
    private JTextField tfEps;
    private JCheckBox chckMutual;

    public SpectralClusteringDialog() {
        initComponents();
    }

    @Override
    public String getName() {
        return SpectralClustering.NAME;
    }

    @Override
    public Props getParams() {
        Props params = new Props();
        params.putInt(SpectralClustering.K, sliderK.getValue());
        params.putInt(SpectralClustering.KMEANS_ITERATIONS, Integer.valueOf(tfKMeansIterations.getText()));
        params.putDouble(SpectralClustering.SIGMA, Double.valueOf(tfSigma.getText()));
        String matrix_conv = (String) comboMatrixConv.getSelectedItem();
        params.put(SpectralClustering.MATRIX_CONV, matrix_conv);

        switch (matrix_conv) {
            case "epsilon-neighborhood matrix":
                params.putDouble("Eps", Double.valueOf(tfEps.getText()));
                break;
            case "directed k-neighborhood matrix":
                params.putInt("KnnK", sliderKnnK.getValue());
                break;
            case "undirected k-neighborhood matrix":
                params.putInt("KnnK", sliderKnnK.getValue());
                if (!chckMutual.isSelected()) {
                    params.put("Mutual", false);
                } else {
                    params.put("Mutual", true);
                }
                break;
            default:
                break;
        }

        params.put(SpectralClustering.SP_ALG, (String) comboSpAlg.getSelectedItem());

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

        // Spectral clustering alg initialization
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Spectral clustering alg initialization:"), c);
        c.gridx = 1;
        String[] sp_algs = SpectralEigenVectorsFactory.getInstance().getProvidersArray();
        comboSpAlg = new JComboBox(sp_algs);
        comboSpAlg.setSelectedItem(sp_algs[2]);
        add(comboSpAlg, c);

        // change k param for KMeans (only applies for KMeans initialization)
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("K-means k:"), c);
        c.gridx = 1;
        c.weightx = 0.9;
        tfK = new JTextField("3", 4);
        sliderK = new JSlider(0, 50, 3);
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

        //K-means iterations
        c.gridx = 0;
        c.gridy++;
        add(new JLabel("Iterations:"), c);
        sliderKMeansIter = new JSlider(0, 2000);
        sliderKMeansIter.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tfKMeansIterations.setText(String.valueOf(sliderKMeansIter.getValue()));
            }
        });
        c.gridx = 1;
        add(sliderKMeansIter, c);

        c.gridx = 2;
        tfKMeansIterations = new JTextField("110", 4);
        add(tfKMeansIterations, c);
        tfKMeansIterations.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                updateKMeansIterSlider();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                updateKMeansIterSlider();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateKMeansIterSlider();
            }
        });
        sliderKMeansIter.setValue(50);

        // Similarity matrix (Sigma)
        c.gridx = 0;
        c.gridy++;
        add(new JLabel("Similarity matrix (Sigma):"), c);
        c.gridx = 1;
        tfSigma = new JTextField("2", 4);
        add(tfSigma, c);

        // Neighborhood graph matrix initialization
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Neighborhood graph matrix initialization:"), c);
        c.gridx = 1;
        String[] ngmis = GraphMatrixConvertorFactory.getInstance().getProvidersArray();
        comboMatrixConv = new JComboBox(ngmis);
        comboMatrixConv.setSelectedItem(ngmis[1]);
        comboMatrixConv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String matrix_conv = (String) comboMatrixConv.getSelectedItem();

                if (matrix_conv.equals("epsilon-neighborhood matrix")) {
                    sliderKnnK.setEnabled(false);
                    tfKnnK.setEnabled(false);
                    chckMutual.setEnabled(false);
                    tfEps.setEnabled(true);
                } else if (matrix_conv.equals("directed k-neighborhood matrix")) {
                    sliderKnnK.setEnabled(true);
                    tfKnnK.setEnabled(true);
                    chckMutual.setEnabled(false);
                    tfEps.setEnabled(false);
                } else if (matrix_conv.equals("undirected k-neighborhood matrix")) {
                    sliderKnnK.setEnabled(true);
                    tfKnnK.setEnabled(true);
                    chckMutual.setEnabled(true);
                    tfEps.setEnabled(false);
                }
            }
        });

        add(comboMatrixConv, c);

        // change k param for k-neighborhood matrix
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("K-neighborhood k:"), c);
        c.gridx = 1;
        c.weightx = 0.9;
        tfKnnK = new JTextField("2", 4);
        sliderKnnK = new JSlider(0, 50, 4);
        sliderKnnK.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tfKnnK.setText(String.valueOf(sliderKnnK.getValue()));
            }
        });
        add(sliderKnnK, c);
        c.gridx = 2;
        add(tfKnnK, c);
        tfKnnK.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                updateKnnKSlider();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                updateKnnKSlider();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateKnnKSlider();
            }
        });

        // Epsilon-neighborhood matrix (Epsilon)
        c.gridx = 0;
        c.gridy++;
        add(new JLabel("Epsilon-neighborhood matrix (Epsilon):"), c);
        c.gridx = 1;
        tfEps = new JTextField("0.3", 4);
        add(tfEps, c);

        // Undirected k-neighborhood matrix (Mutual)
        chckMutual = new JCheckBox("mutual", false);
        c.gridx++;
        add(chckMutual, c);

        // Default settings
        sliderKnnK.setEnabled(false);
        tfKnnK.setEnabled(false);
        chckMutual.setEnabled(false);
        tfEps.setEnabled(true);
    }

    private void updateKSlider() {
        try {
            int val = Integer.valueOf(tfK.getText());
            sliderK.setValue(val);
        } catch (NumberFormatException ex) {
            // wrong input so we do not set the slider but also do not want to raise an exception
        }
    }

    private void updateKMeansIterSlider() {
        try {
            int val = Integer.valueOf(tfKMeansIterations.getText());
            sliderKMeansIter.setValue(val);
        } catch (NumberFormatException ex) {
            // wrong input so we do not set the slider but also do not want to raise an exception
        }
    }

    private void updateKnnKSlider() {
        try {
            int val = Integer.valueOf(tfKnnK.getText());
            sliderKnnK.setValue(val);
        } catch (NumberFormatException ex) {
            // wrong input so we do not set the slider but also do not want to raise an exception
        }
    }

    @Override
    public boolean isUIfor(ClusteringAlgorithm algorithm, Dataset dataset) {
        return algorithm.getName().contains("Spectral");
    }

}
