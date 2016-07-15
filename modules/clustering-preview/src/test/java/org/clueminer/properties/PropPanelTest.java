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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.openide.nodes.AbstractNode;

/**
 * Simple UI test.
 *
 * @author deric
 */
public class PropPanelTest<E extends Instance, C extends Cluster<E>> {

    private final PropPanel subject;

    public PropPanelTest() {
        subject = new PropPanel();
        subject.setNodes(createTestData());
    }

    protected JFrame showInFrame() {
        JFrame frame = new JFrame("test properties");
        frame.getContentPane().add(subject, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(800, 600));
        //frame.setSize(getPreferredSize());
        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new PropPanelTest().showInFrame();
            }
        });
    }

    private Collection<? extends AbstractNode> createTestData() {
        Clustering clustering = FakeClustering.irisMostlyWrong();


        Collection<AbstractNode> res = new ArrayList<>();
        ClusterNode node = new ClusterNode(clustering);
        res.add(node);
        return res;
    }

}
