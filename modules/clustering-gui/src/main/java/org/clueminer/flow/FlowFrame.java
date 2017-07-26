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
package org.clueminer.flow;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;

/**
 *
 * @author deric
 */
public class FlowFrame extends JPanel {

    private final transient ExplorerManager mgr = new ExplorerManager();
    private final FlowNodeRoot root;
    private BeanTreeView treeView;
    private final FlowNodeFactory factory;

    public FlowFrame() {
        initComponents();
        factory = new FlowNodeFactory();
        root = new FlowNodeRoot(factory);
        mgr.setRootContext(root);
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(0, 0, 0, 0);

        treeView = new BeanTreeView();
        treeView.setRootVisible(false);
        add(treeView, c);
    }

    public ExplorerManager getExplorerManager() {
        return mgr;
    }

}
