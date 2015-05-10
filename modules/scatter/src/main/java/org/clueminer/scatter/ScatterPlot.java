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
import com.xeiam.xchart.Series;
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
public class ScatterPlot extends JPanel {

    private static final long serialVersionUID = 2083423601634918077L;

    private int markerSize = 10;

    public ScatterPlot() {
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
    public void setClustering(final Clustering<? extends Cluster> clustering) {
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

    private JPanel clusteringPlot(final Clustering<? extends Cluster> clustering) {
        int attrX = 0;
        int attrY = 1;

        Chart chart = new Chart(getWidth(), getHeight());
        chart.getStyleManager().setChartType(StyleManager.ChartType.Scatter);

        // Customize Chart
        chart.getStyleManager().setChartTitleVisible(false);
        chart.getStyleManager().setLegendPosition(StyleManager.LegendPosition.OutsideE);
        chart.getStyleManager().setMarkerSize(markerSize);

        for (Cluster<Instance> clust : clustering) {
            Series s = chart.addSeries(clust.getName(), clust.attrCollection(attrX), clust.attrCollection(attrY));
            s.setMarkerColor(clust.getColor());
        }

        return new XChartPanel(chart);
    }

    public void setClusterings(final Clustering<Cluster> clusteringA, final Clustering<Cluster> clusteringB) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                removeAll();

                GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0);

                // Add plot to Swing component
                add(clusteringPlot(clusteringA), c);
                c.gridx = 1;
                add(clusteringPlot(clusteringB), c);
                revalidate();
                validate();
                repaint();
            }
        });
    }

    public int getMarkerSize() {
        return markerSize;
    }

    public void setMarkerSize(int markerSize) {
        this.markerSize = markerSize;
        revalidate();
        validate();
        repaint();
    }

}
