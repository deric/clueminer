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
package org.clueminer.scatter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.fixtures.clustering.FakeClustering;

public class ScatterTest2 extends JPanel {

    private ScatterPlot2 plot;

    public ScatterTest2() {
        setPreferredSize(new Dimension(800, 600));
        initComponents();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new ScatterTest2().showInFrame();
            }
        });
    }

    public String getTitle() {
        return "Scatter test";
    }

    protected JFrame showInFrame() {
        JFrame frame = new JFrame(getTitle());
        frame.getContentPane().add(this, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(500, 500));
        //frame.setSize(getPreferredSize());
        frame.setVisible(true);
        return frame;
    }

    private void initComponents() {
        setSize(500, 500);
        setLayout(new BorderLayout());
        plot = new ScatterPlot2();
        Clustering clusters = FakeClustering.iris();
        plot.setClustering(clusters);
        add(plot);
    }

}
