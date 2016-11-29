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
package org.clueminer.flow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.openide.util.ImageUtilities;

/**
 *
 * @author deric
 */
public class FlowToolbar extends JToolBar {

    private JButton btnRun;
    private FlowFrame frame;

    public FlowToolbar(FlowFrame frame) {
        super(SwingConstants.HORIZONTAL);
        this.frame = frame;
        initComponents();
    }

    private void initComponents() {
        this.setFloatable(false);
        this.setRollover(true);

        btnRun = new JButton(ImageUtilities.loadImageIcon("org/clueminer/flow/play16.png", false));
        btnRun.setToolTipText("Execute flow");

        btnRun.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        add(btnRun);
        addSeparator();

    }

}
