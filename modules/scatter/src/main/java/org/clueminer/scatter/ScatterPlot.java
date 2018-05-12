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
package org.clueminer.scatter;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.StyleManager;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.XChartPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.gui.BPanel;

/**
 * Simple and fast 2D chart.
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class ScatterPlot<E extends Instance, C extends Cluster<E>> extends JPanel {

    private static final long serialVersionUID = 2083423601634918077L;

    private int markerSize = 10;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;
    private Chart currChart;
    private String title = null;
    private boolean simple = false;
    private final transient EventListenerList renderListeners = new EventListenerList();
    private LinkedList<BPanel> layers;
    private XChartPanel xchart;

    public ScatterPlot() {
        initComponents();
    }

    public ScatterPlot(MouseListener ml, MouseMotionListener mml) {
        initComponents();
        this.mouseListener = ml;
        this.mouseMotionListener = mml;
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setSize(new Dimension(800, 600));
        layers = new LinkedList<>();
    }

    /**
     * Updating chart might take a while, therefore it's safer to preform update
     * in EDT
     *
     * @param clustering
     */
    public void setClustering(final Clustering<E, C> clustering) {
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
                fireComponentRendered();
            }
        });
    }

    private JPanel clusteringPlot(final Clustering<E, C> clustering) {
        int attrX = 0;
        int attrY = 1;

        Chart chart = new Chart(getWidth(), getHeight(), ChartTheme.XChart);
        if (title != null) {
            chart.setChartTitle(title);
        }
        StyleManager sm = chart.getStyleManager();
        sm.setChartType(StyleManager.ChartType.Scatter);

        sm.setChartTitleVisible(false);
        if (simple) {
            // Customize Chart
            sm.setLegendVisible(false);
            sm.setAxisTitlesVisible(false);
            sm.setAxisTitlePadding(0);
            sm.setChartBackgroundColor(Color.WHITE);
            sm.setPlotBorderVisible(false);
            sm.setAxisTicksVisible(false);
        } else {
            sm.setLegendPosition(StyleManager.LegendPosition.OutsideE);
        }

        sm.setMarkerSize(markerSize);

        //update reference to current chart
        this.currChart = chart;

        for (Cluster<E> clust : clustering) {
            if (clust.size() > 0) {
                Series s = chart.addSeries(clust.getName(), clust.attrCollection(attrX), clust.attrCollection(attrY));
                s.setMarkerColor(clust.getColor());
            }
        }
        xchart = new XChartPanel(chart);
        if (mouseListener != null) {
            xchart.addMouseListener(mouseListener);
        }
        if (mouseMotionListener != null) {
            xchart.addMouseMotionListener(mouseMotionListener);
        }
        Iterator<BPanel> it = layers.iterator();
        while (it.hasNext()) {
            xchart.addLayer(it.next());
        }

        return xchart;
    }

    public void setClusterings(final Clustering<E, C> clusteringA, final Clustering<E, C> clusteringB) {
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

    public void setSimpleMode(boolean b) {
        this.simple = b;
    }

    public int getMarkerSize() {
        return markerSize;
    }

    public void setMarkerSize(int markerSize) {
        this.markerSize = markerSize;
        /*    revalidate();
        validate();
        repaint();*/
    }

    /**
     * Translate selected area into real values used in the dataset. Currently
     * only rectangular selection is supported
     *
     * @param shape
     * @return
     */
    public Rectangle.Double tranlateSelection(Shape shape) {
        if (currChart != null) {
            return currChart.translateSelection(shape.getBounds());
        }
        throw new RuntimeException("current chart not set");
    }

    public double[] translate(Point2D point) {
        if (currChart != null) {
            return currChart.translate(point);
        }
        throw new RuntimeException("current chart not set");
    }

    public Point2D posOnCanvas(double x, double y) {
        if (currChart != null) {
            return currChart.positionOnCanvas(x, y);
        }
        throw new RuntimeException("current chart not set");
    }

    public Rectangle.Double plotArea() {
        if (currChart != null) {
            return currChart.getPlotArea();
        }
        throw new RuntimeException("current chart not set");
    }

    public void setTitle(String title) {
        this.title = title;
        repaint();
    }

    public void addRenderListener(RenderingListener listener) {
        renderListeners.add(RenderingListener.class, listener);
    }

    public void fireComponentRendered() {
        RenderingListener[] listeners;

        listeners = renderListeners.getListeners(RenderingListener.class);
        for (RenderingListener listener : listeners) {
            listener.renderingFinished(this);
        }
    }

    public void addLayer(BPanel panel) {
        layers.add(panel);
    }

    public void resetCache() {
        if (xchart != null) {
            xchart.resetCache();
        }
        revalidate();
        validate();
        repaint();
    }

}
