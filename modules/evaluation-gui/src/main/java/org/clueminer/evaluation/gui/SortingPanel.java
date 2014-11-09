package org.clueminer.evaluation.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.factory.EvaluationFactory;

/**
 *
 * @author Tomas Barton
 */
public class SortingPanel extends JPanel {

    private Collection<? extends Clustering> clusterings;
    private ClusterEvaluator evaluatorX;
    private ClusterEvaluator evaluatorY;
    private JComboBox comboEvaluatorX;
    private JComboBox comboEvaluatorY;
    private SortingPlot plot;

    public SortingPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        //setSize(new Dimension(800, 600));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;
        //component in last row should be streatched to fill space at the bottom
        c.weighty = 0.1;
        c.insets = new java.awt.Insets(5, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 10);

        comboEvaluatorX = new JComboBox();
        comboEvaluatorX.setModel(new EvaluatorComboBox());
        comboEvaluatorX.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboEvaluatorXActionPerformed(evt);
            }
        });
        add(comboEvaluatorX, c);

        comboEvaluatorY = new JComboBox();
        comboEvaluatorY.setModel(new EvaluatorComboBox());
        comboEvaluatorY.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboEvaluatorYActionPerformed(evt);
            }
        });
        c.insets = new Insets(5, 0, 5, 5);
        c.gridx = 1;
        add(comboEvaluatorY, c);

        plot = new SortingPlot();
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridx = 0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 0);
        add(plot, c);
    }

    private void comboEvaluatorXActionPerformed(java.awt.event.ActionEvent evt) {
        String item = (String) comboEvaluatorX.getSelectedItem();
        if (item != null) {
            plot.setEvaluatorX(EvaluationFactory.getInstance().getProvider(item));
        }
    }

    private void comboEvaluatorYActionPerformed(java.awt.event.ActionEvent evt) {
        String item = (String) comboEvaluatorY.getSelectedItem();
        if (item != null) {
            plot.setEvaluatorY(EvaluationFactory.getInstance().getProvider(item));
        }
    }

    public void setClusterings(Collection<? extends Clustering> clusterings) {
        this.clusterings = clusterings;
        clusteringChanged();
    }

    public void setEvaluatorX(String eval) {
        clusteringChanged();
    }

    public void setEvaluatorY(String eval) {
        clusteringChanged();
    }

    private void clusteringChanged() {
        //this.removeAll();

    }

}
