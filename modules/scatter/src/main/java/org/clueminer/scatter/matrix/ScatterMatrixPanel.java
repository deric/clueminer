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
package org.clueminer.scatter.matrix;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.StyleManager;
import com.xeiam.xchart.XChartPanel;
import com.xeiam.xchart.internal.markers.Marker;
import com.xeiam.xchart.internal.style.SeriesColorMarkerLineStyleCycler;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author deric
 */
public class ScatterMatrixPanel extends JPanel {

    private static final long serialVersionUID = 4957672836007726620L;

    private static final Logger logger = Logger.getLogger(ScatterMatrixPanel.class.getName());
    private Legend legend;

    public ScatterMatrixPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        legend = new Legend();
        setSize(new Dimension(800, 600));
    }

    public static Table<Integer, String, LegendEntry> newTable() {
        return Tables.newCustomTable(
                Maps.<Integer, Map<String, LegendEntry>>newHashMap(),
                new Supplier<Map<String, LegendEntry>>() {
                    @Override
                    public Map<String, LegendEntry> get() {
                        return Maps.newHashMap();
                    }
                });
    }

    public void setClustering(final Clustering<Cluster> clustering) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                removeAll();

                JPanel chart;
                GridBagConstraints c = new GridBagConstraints();
                c.fill = GridBagConstraints.BOTH;
                c.anchor = GridBagConstraints.CENTER;
                c.weightx = 1.0;
                c.weighty = 1.0;

                if (clustering != null && clustering.size() > 0) {
                    Cluster first = clustering.get(0);
                    if (first.size() > 0) {
                        int attrCnt = first.attributeCount();

                        for (int i = 0; i < attrCnt; i++) {
                            for (int j = 0; j < i; j++) {
                                chart = clusteringPlot(clustering, j, i);
                                c.gridx = j;
                                c.gridy = i - 1;
                                add(chart, c);
                            }
                        }
                        //place legend
                        c.gridx = attrCnt - 2;
                        c.gridy = 0;
                        c.fill = GridBagConstraints.BOTH;
                        int i = 0;
                        Table<Integer, String, LegendEntry> labels = newTable();
                        SeriesColorMarkerLineStyleCycler generator = new SeriesColorMarkerLineStyleCycler();
                        for (Cluster<Instance> clust : clustering) {
                            Marker m = generator.getNextSeriesColorMarkerLineStyle().getMarker();
                            labels.put(i, clust.getName(), new LegendEntry(clust.getName(), clust.getColor(), m));
                            i++;
                        }
                        legend.setLabels(labels);
                        add(legend, c);

                    } else {
                        logger.log(Level.SEVERE, "empty cluster");
                    }
                }

                revalidate();
                validate();
                repaint();
            }
        });

    }

    private JPanel clusteringPlot(final Clustering<Cluster> clustering, int attrX, int attrY) {
        Chart chart = new Chart(getWidth(), getHeight());
        chart.getStyleManager().setChartType(StyleManager.ChartType.Scatter);

        // Customize Chart
        chart.getStyleManager().setChartTitleVisible(false);
        chart.getStyleManager().setLegendVisible(false);
        chart.getStyleManager().setMarkerSize(10);

        for (Cluster<Instance> clust : clustering) {
            Series s = chart.addSeries(clust.getName(), clust.attrCollection(attrX), clust.attrCollection(attrY));
            s.setMarkerColor(clust.getColor());
        }

        return new XChartPanel(chart);
    }

}
