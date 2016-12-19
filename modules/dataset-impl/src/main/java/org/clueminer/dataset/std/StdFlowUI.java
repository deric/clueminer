/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.dataset.std;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.clueminer.dataset.api.DataStandardizationFactory;
import org.clueminer.flow.api.FlowPanel;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class StdFlowUI extends JPanel implements FlowPanel {

    private JComboBox<String> comboStd;

    public StdFlowUI() {
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
        add(new JLabel("Standardization:"), c);
        c.gridx = 1;
        c.weightx = 0.9;
        comboStd = new JComboBox(DataStandardizationFactory.getInstance().getProvidersArray());
        add(comboStd, c);
    }

    @Override
    public Props getParams() {
        Props params = new Props();
        params.put("std", comboStd.getSelectedItem());
        return params;
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

}
