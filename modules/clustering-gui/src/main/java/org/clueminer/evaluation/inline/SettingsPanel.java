/*
 * Copyright (C) 2011-2019 clueminer.org
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
package org.clueminer.evaluation.inline;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.factory.ExternalEvaluatorFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.api.factory.RankEvaluatorFactory;
import org.clueminer.clustering.api.factory.RankFactory;

/**
 *
 * @author deric
 */
public class SettingsPanel extends JPanel {

    private static final long serialVersionUID = -4605487939434178912L;

    private JCheckBox chckUseMetricsMax;
    private JCheckBox chckMedian;
    private JCheckBox chckCorrelation;
    private JComboBox comboCorrelation;
    private JComboBox comboRanking;
    private JComboBox comboExternal;
    private JCheckBox[] chckEvals;

    public SettingsPanel() {
        initComponents();
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

        chckUseMetricsMax = new JCheckBox("Scale to current max value (on Y axis)");
        chckUseMetricsMax.setSelected(true);
        add(chckUseMetricsMax, c);

        c.gridy++;
        chckMedian = new JCheckBox("Cross axes at median Y value");
        chckMedian.setSelected(true);
        add(chckMedian, c);

        c.gridy++;
        chckCorrelation = new JCheckBox("Show correlation");
        chckCorrelation.setSelected(true);
        add(chckCorrelation, c);

        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Ranking evaluation:"), c);
        comboCorrelation = new JComboBox(RankEvaluatorFactory.getInstance().getProvidersArray());
        c.gridx = 1;
        add(comboCorrelation, c);


        c.gridy++;
        c.gridx = 0;
        add(new JLabel("External evaluation:"), c);
        comboExternal = new JComboBox(ExternalEvaluatorFactory.getInstance().getProvidersArray());
        c.gridx = 1;
        add(comboExternal, c);
        comboExternal.setSelectedItem("NMI-sqrt");

        //ranking strategy
        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Ranking strategy:"), c);
        comboRanking = new JComboBox(RankFactory.getInstance().getProvidersArray());
        c.gridx = 1;
        add(comboRanking, c);
        comboRanking.setSelectedItem("SO Rank");

        //show only internal evaluations
        List<String> providers = InternalEvaluatorFactory.getInstance().getProviders();
        chckEvals = new JCheckBox[providers.size()];
        int i = 0;
        int modulo = 6;
        for (String provider : providers) {
            chckEvals[i] = new JCheckBox(provider);
            c.weighty = 0.9;
            c.gridx = i % modulo;
            if (i % modulo == 0) {
                c.gridy++;
            }
            add(chckEvals[i], c);
            i++;
        }

    }

    /**
     * Called after user clicking on OK button
     *
     * @param plot
     */
    void updatePlot(ScorePlot plot) {
        plot.setUseSupervisedMetricMax(chckUseMetricsMax.isSelected());
        plot.setCrossAxisAtMedian(chckMedian.isSelected());
        plot.setShowCorrelation(chckCorrelation.isSelected());
        plot.setRankEvaluator(RankEvaluatorFactory.getInstance().getProvider(comboCorrelation.getSelectedItem().toString()));
        plot.setRank(RankFactory.getInstance().getProvider(comboRanking.getSelectedItem().toString()));
        plot.setEvaluatorX(ExternalEvaluatorFactory.getInstance().getProvider(comboExternal.getSelectedItem().toString()));
        List<ClusterEvaluation> objectives = getObjectives();
        //TODO: validate number of objectives
        plot.setObjectives(objectives);
        plot.computeRanking();
    }

    private List<ClusterEvaluation> getObjectives() {
        List<ClusterEvaluation> objectives = new LinkedList();
        InternalEvaluatorFactory ief = InternalEvaluatorFactory.getInstance();
        for (JCheckBox chck : chckEvals) {

            if (chck.isSelected()) {
                objectives.add((ClusterEvaluation) ief.getProvider(chck.getText()));
            }
        }

        return objectives;
    }

}
