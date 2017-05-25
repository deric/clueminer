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
import com.xeiam.xchart.StyleManager;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.gui.ClusteringVisualization;
import org.clueminer.clustering.gui.VisualizationTask;
import org.clueminer.dataset.api.Instance;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common methods for rendering data previews
 *
 * @author deric
 * @param <E>
 * @param <C>
 * @param <R>
 */
public abstract class AbstractRenderer<E extends Instance, C extends Cluster<E>, R extends Image> implements ClusteringVisualization<R> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRenderer.class);

    protected int markerSize = 20;
    protected int imgWidth = 600;
    protected int imgHeight = 600;

    protected Chart createChart(VisualizationTask task, int width, int height) {
        Chart chart = new Chart(width, height, StyleManager.ChartTheme.XChart);
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
        sm.setPlotGridLinesVisible(false);

        return chart;
    }

    /**
     * Generate image of given size
     *
     * @param chart
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
