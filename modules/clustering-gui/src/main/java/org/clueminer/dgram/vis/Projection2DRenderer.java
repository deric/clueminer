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
import java.awt.Image;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.clueminer.clustering.gui.ClusteringVisualization;
import org.clueminer.clustering.gui.VisualizationTask;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
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
public class Projection2DRenderer<E extends Instance, C extends Cluster<E>, R extends Image>
        extends AbstractRenderer<E, C, R>
        implements ClusteringVisualization<R> {

    private static final String NAME = "Projection";
    private static final Logger LOG = LoggerFactory.getLogger(Projection2DRenderer.class);

    public Projection2DRenderer() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public R generateImage(VisualizationTask task) {
        Chart chart = createChart(task, imgWidth, imgHeight);
        addData(chart, task);
        Image img = generate(chart, task.getWidth(), task.getHeight());
        Props prop = task.getProps();
        LOG.debug("generated image using attrs {}, {}", prop.getInt("visualize.x_attr", 0), prop.getInt("visualize.y_attr", 1));

        DendrogramVisualizationListener listener = task.getListener();
        if (listener != null) {
            listener.clusteringFinished(task.getClustering());
            listener.previewUpdated(img);
        }
        return (R) img;
    }

    protected void addData(Chart chart, VisualizationTask task) {
        Props prop = task.getProps();
        int attrX = prop.getInt("visualize.x_attr", 0);
        int attrY = prop.getInt("visualize.y_attr", 1);
        Clustering<E, C> clustering = task.getClustering();

        for (Cluster<E> clust : clustering) {
            if (clust.size() > 0) {
                Series s = chart.addSeries(clust.getName(), clust.attrCollection(attrX), clust.attrCollection(attrY));
                s.setMarkerColor(clust.getColor());
            }
        }

    }

}
