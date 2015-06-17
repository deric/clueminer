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
package org.clueminer.evaluation.inline;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author deric
 */
public class SettingsPanel extends JPanel {

    private JCheckBox chckUseMetricsMax;

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

        chckUseMetricsMax = new JCheckBox("use metrics max value (Y axis)");
        chckUseMetricsMax.setSelected(true);
        add(chckUseMetricsMax, c);

        c.gridy++;
    }

    /**
     * Called after user clicking on OK button
     *
     * @param plot
     */
    void updatePlot(ScorePlot plot) {
        plot.setUseSupervisedMetricMax(chckUseMetricsMax.isSelected());
    }

}
