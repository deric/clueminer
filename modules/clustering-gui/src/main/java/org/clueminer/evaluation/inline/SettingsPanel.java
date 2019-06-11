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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.clueminer.clustering.api.factory.RankEvaluatorFactory;

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

        chckUseMetricsMax = new JCheckBox("scale to current max value (on Y axis)");
        chckUseMetricsMax.setSelected(true);
        add(chckUseMetricsMax, c);

        c.gridy++;
        chckMedian = new JCheckBox("cross axes at median Y value");
        chckMedian.setSelected(true);
        add(chckMedian, c);

        c.gridy++;
        chckCorrelation = new JCheckBox("show correlation");
        chckCorrelation.setSelected(true);
        add(chckCorrelation, c);

        c.gridy++;
        c.gridx = 0;
        add(new JLabel("Ranking evaluation:"), c);
        comboCorrelation = new JComboBox(RankEvaluatorFactory.getInstance().getProvidersArray());
        c.gridx = 1;
        add(comboCorrelation, c);
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
        plot.setRank(RankEvaluatorFactory.getInstance().getProvider(comboCorrelation.getSelectedItem().toString()));
        plot.updateCorrelation();
    }

}
