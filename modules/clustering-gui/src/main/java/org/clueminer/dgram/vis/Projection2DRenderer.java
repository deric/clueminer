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
package org.clueminer.dgram.vis;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.StyleManager;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.gui.ClusteringVisualization;
import org.clueminer.clustering.gui.VisualizationTask;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.imgscalr.Scalr;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 * @param <E>
 * @param <C>
 * @param <R> resulting type
 */
@ServiceProvider(service = ClusteringVisualization.class)
public class Projection2DRenderer<E extends Instance, C extends Cluster<E>, R extends Image> implements ClusteringVisualization<R> {

    private static final String NAME = "Projection";
    private static final Logger LOG = LoggerFactory.getLogger(Projection2DRenderer.class);
    private int markerSize = 10;

    public Projection2DRenderer() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public R generateImage(VisualizationTask task) {
        Chart chart = createChart(task);
        Image img = generate(chart, task.getWidth(), task.getHeight());
        return (R) img;
    }

    private Chart createChart(VisualizationTask task) {
        Clustering<E, C> clustering = task.getClustering();
        Chart chart = new Chart(task.getWidth(), task.getHeight(), StyleManager.ChartTheme.XChart);
        StyleManager sm = chart.getStyleManager();
        sm.setChartType(StyleManager.ChartType.Scatter);

        sm.setChartTitleVisible(false);

        sm.setLegendVisible(false);
        sm.setAxisTitlesVisible(false);
        sm.setAxisTitlePadding(0);
        sm.setChartBackgroundColor(Color.WHITE);
        sm.setPlotBorderVisible(false);
        sm.setAxisTicksVisible(false);
        sm.setMarkerSize(markerSize);

        Props prop = task.getProps();
        int attrX = prop.getInt("visualize.x_attr", 0);
        int attrY = prop.getInt("visualize.y_attr", 1);

        for (Cluster<E> clust : clustering) {
            if (clust.size() > 0) {
                Series s = chart.addSeries(clust.getName(), clust.attrCollection(attrX), clust.attrCollection(attrY));
                s.setMarkerColor(clust.getColor());
            }
        }
        return chart;
    }

    /**
     * Generate image of given size
     *
     * @param width
     * @param height
     * @return
     */
    public Image generate(Chart chart, int width, int height) {
        LOG.debug("generating image {}x{}", width, height);
        BufferedImage image = new BufferedImage(chart.getWidth(), chart.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        chart.paint(g);

        if (image.getHeight() != height || image.getWidth() != width) {
            image = Scalr.resize(image, Scalr.Method.SPEED,
                    Scalr.Mode.AUTOMATIC,
                    width, height, Scalr.OP_ANTIALIAS);
        }
        g.dispose();
        return image;
    }

}
