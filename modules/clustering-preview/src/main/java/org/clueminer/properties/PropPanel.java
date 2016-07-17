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
package org.clueminer.properties;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class PropPanel extends JPanel {

    private static final long serialVersionUID = 4857071066201255289L;

    private JScrollPane scrollPane;
    private JPanel content;

    public PropPanel() {
        initialize();
    }

    private void initialize() {
        //setLayout(new GridBagLayout());
        //setMaximumSize(this.getPreferredSize());
        content = new JPanel();
        content.setLayout(new GridBagLayout());
        //content.setMaximumSize(this.getPreferredSize());
        content.setSize(new Dimension(800, 600));
        scrollPane = new JScrollPane(content);
        scrollPane.setLayout(new ScrollPaneLayout());
        scrollPane.setHorizontalScrollBarPolicy(scrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(scrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.getViewport().add(content);
        add(scrollPane, new GridBagConstraints(
                0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0),
                0, 0)
        );

        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                scrollPane.setPreferredSize(getSize());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
                scrollPane.setPreferredSize(getSize());
            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }

    public void setNodes(Collection<? extends AbstractNode> allInstances) {
        if (allInstances.size() == 1) {
            content.removeAll();
            AbstractNode node = allInstances.iterator().next();
            Node.PropertySet[] sets = node.getPropertySets();
            int i = 0;
            for (Node.PropertySet set : sets) {
                i = addSet(set, i);
            }
            validate();
            repaint();
            revalidate();
            scrollPane.getViewport().revalidate();
        }

    }

    private int addSet(Node.PropertySet set, int i) {
        JLabel label = new JLabel("<html><b>" + set.getName() + "</b></html>");
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = i++;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(0, 0, 0, 0);
        content.add(label, c);

        for (Property p : set.getProperties()) {
            try {
                label = new JLabel(p.getName());
                c.gridx = 0;
                c.gridy = i;
                content.add(label, c);
                label = new JLabel(p.getValue().toString());
                c.gridx = 1;
                c.gridy = i++;
                content.add(label, c);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return i++;
    }

}
