package org.clueminer.properties;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class PropPanel extends JPanel {

    public PropPanel() {
        initialize();
    }

    private void initialize() {
        setLayout(new GridBagLayout());
        setMaximumSize(this.getPreferredSize());
    }

    public void setNodes(Collection<? extends AbstractNode> allInstances) {
        if (allInstances.size() == 1) {
            removeAll();
            System.out.println("size: " + getSize());
            AbstractNode node = allInstances.iterator().next();
            System.out.println("node: " + node.toString());
            Node.PropertySet[] sets = node.getPropertySets();
            System.out.println("prop sets: " + sets.length);
            int i = 0;
            for (Node.PropertySet set : sets) {
                i = addSet(set, i);
            }
            validate();
            repaint();
            revalidate();
        }
    }

    private int addSet(Node.PropertySet set, int i) {
        JLabel label = new JLabel("<strong>" + set.getName() + "</strong>");
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = i++;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(0, 0, 0, 0);
        add(label, c);

        for (Property p : set.getProperties()) {
            try {
                label = new JLabel(p.getName());
                c.gridx = 0;
                c.gridy = i;
                add(label, c);
                label = new JLabel(p.getValue().toString());
                c.gridx = 1;
                c.gridy = i++;
                add(label, c);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return i++;
    }

}
