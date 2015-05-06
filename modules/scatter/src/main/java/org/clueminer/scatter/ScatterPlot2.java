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

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.StyleManager;
import com.xeiam.xchart.XChartPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author deric
 */
public class ScatterPlot2 extends JPanel {

    public ScatterPlot2() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setSize(new Dimension(800, 600));
    }

    /**
     * Updating chart might take a while, therefore it's safer to preform update
     * in EDT
     *
     * @param clustering
     */
    public void setClustering(final Clustering<Cluster> clustering) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                removeAll();

                add(clusteringPlot(clustering),
                        new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                                GridBagConstraints.NORTHWEST,
                                GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                revalidate();
                validate();
                repaint();
            }
        });
    }

    private JPanel clusteringPlot(final Clustering<Cluster> clustering) {
        int attrX = 0;
        int attrY = 1;

        Chart chart = new Chart(getWidth(), getHeight());
        chart.getStyleManager().setChartType(StyleManager.ChartType.Scatter);

        // Customize Chart
        chart.getStyleManager().setChartTitleVisible(false);
        chart.getStyleManager().setLegendPosition(StyleManager.LegendPosition.InsideSW);
        chart.getStyleManager().setMarkerSize(16);

        for (Cluster<Instance> clust : clustering) {
            chart.addSeries(clust.getName(), clust.attrCollection(attrX), clust.attrCollection(attrY));
        }

        return new XChartPanel(chart);
    }

}
