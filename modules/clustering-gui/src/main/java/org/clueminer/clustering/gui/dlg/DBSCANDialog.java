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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.clueminer.clustering.algorithm.DBSCAN;
import org.clueminer.clustering.algorithm.DBSCANParamEstim;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.gui.ClusteringDialog;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.utils.Props;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = ClusteringDialog.class)
public class DBSCANDialog<E extends Instance, C extends Cluster<E>> extends JPanel implements ClusteringDialog<E, C>, TaskListener {

    private static final long serialVersionUID = 7956135045201178826L;

    private JSlider sliderMinPts;
    private JTextField tfMinPts;
    private JTextField tfRadius;
    private JComboBox comboDistance;
    private JButton btnEstimate;
    private JLabel info;
    private static final String name = "DBSCAN";
    private Dataset<E> dataset;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);
    private Props params = new Props();
    private DBSCANParamEstim<E> estimator;

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

        tfMinPts = new JTextField("4", 10);
        sliderMinPts = new JSlider(1, 1000, 4);
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
        add(new JLabel("Epsilon:"), c);
        c.gridx = 1;
        tfRadius = new JTextField("2.0", 10);
        add(tfRadius, c);

        btnEstimate = new JButton("Estimate");
        final TaskListener tl = this;
        btnEstimate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (dataset != null) {
                    btnEstimate.setEnabled(false);
                    final RequestProcessor.Task task = RP.create(new Runnable() {

                        @Override
                        public void run() {
                            estimator = new DBSCANParamEstim<>();
                            estimator.estimate(dataset, params);
                        }

                    });
                    task.addTaskListener(tl);
                    task.schedule(0);
                }
            }
        });
        c.gridx = 2;
        add(btnEstimate, c);
        c.gridy++;
        c.gridx = 1;
        info = new JLabel("");
        add(info, c);

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

    @Override
    public Props getParams() {
        params.putInt(DBSCAN.MIN_PTS, Integer.valueOf(tfMinPts.getText()));
        params.putDouble(DBSCAN.EPS, Double.parseDouble(tfRadius.getText()));
        params.put(AbstractClusteringAlgorithm.DISTANCE, (String) comboDistance.getSelectedItem());

        return params;
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public boolean isUIfor(ClusteringAlgorithm<E, C> algorithm, Dataset<E> dataset) {
        if (algorithm instanceof DBSCAN) {
            this.dataset = dataset;
            return true;
        }
        return false;
    }

    @Override
    public void taskFinished(Task task) {
        if (estimator != null) {
            tfRadius.setText(String.valueOf(estimator.getEps()));
            info.setText(String.format("eps range %.4f and %.4f", estimator.getMinEps(), estimator.getMaxEps()));
            btnEstimate.setEnabled(true);
        }
    }

}
