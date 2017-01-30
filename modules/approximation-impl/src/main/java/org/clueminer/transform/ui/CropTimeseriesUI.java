/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.transform.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.flow.api.FlowPanel;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class CropTimeseriesUI extends JPanel implements FlowPanel {

    private TimeSelectionPlot plot;

    public CropTimeseriesUI() {
        initComponents();
    }

    @Override
    public Props getParams() {
        Props params = new Props();
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
        c.weightx = 0.05;
        c.weighty = 0.05;
        c.insets = new java.awt.Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel("Select data area:"), c);
        c.gridy = 1;
        c.weightx = 0.95;
        c.weighty = 0.95;
        c.fill = GridBagConstraints.BOTH;
        plot = new TimeSelectionPlot();
        add(plot, c);

    }

    public void setDataset(Timeseries<? extends ContinuousInstance> dataset) {
        plot.setDataset(dataset);
    }

}
