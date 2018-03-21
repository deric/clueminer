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
package org.clueminer.export.sim;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.prefs.Preferences;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author Tomas Barton
 */
public class MatrixOptions extends JPanel {

    private JCheckBox chckIncludeHeader;

    public static final String INCLUDE_HEADER = "include_header";

    public MatrixOptions() {
        initComponets();
    }

    void updatePreferences(Preferences p) {
        p.putBoolean(INCLUDE_HEADER, chckIncludeHeader.isSelected());
    }

    private void initComponets() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;

        chckIncludeHeader = new JCheckBox("include header");
        chckIncludeHeader.setSelected(true);
        add(chckIncludeHeader, c);

    }

}
