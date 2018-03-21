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
package org.clueminer.dgram.vis;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import java.awt.Color;
import java.awt.Image;
import java.util.Collection;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.clueminer.clustering.gui.ClusteringVisualization;
import org.clueminer.clustering.gui.VisualizationTask;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.project.api.ProjectController;
import org.clueminer.projection.Projection;
import org.clueminer.projection.ProjectionFactory;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import org.openide.util.Lookup;
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

        DendrogramVisualizationListener listener = task.getListener();
        if (listener != null) {
            listener.clusteringFinished(task.getClustering());
            listener.previewUpdated(img);
        }
        return (R) img;
    }

    protected void addData(Chart chart, VisualizationTask task) {
        Props prop = task.getProps();
        Clustering<E, C> clustering = task.getClustering();

        ProjectionFactory pf = ProjectionFactory.getInstance();
        String provider = prop.get(PropType.VISUAL, "projection", "PCA");
        LOG.debug("projection: {}", provider);
        Projection projection = pf.getProvider(provider);
        Dataset<E> dataset = clustering.getLookup().lookup(Dataset.class);

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        if (dataset != null) {
            projection = findCached(pc, projection);
            if (!projection.hasData()) {
                //project into 2D
                LOG.debug("computing {} ", provider);
                projection.initialize(dataset, 2);
                pc.getCurrentProject().add(projection);
            }

            double[] proj;
            for (Cluster<E> clust : clustering) {
                if (clust.size() > 0) {
                    double x[] = new double[clust.size()];
                    double y[] = new double[clust.size()];
                    for (int i = 0; i < clust.size(); i++) {
                        proj = projection.transform(clust.get(i));
                        x[i] = proj[0];
                        y[i] = proj[1];
                    }
                    Series s = chart.addSeries(clust.getName(), x, y);
                    Color c = clust.getColor();
                    if (c == null) {
                        LOG.warn("missing cluster's color");
                        c = Color.GRAY;
                    }
                    s.setMarkerColor(c);
                }
            }
        } else {
            LOG.error("missing dataset!");
        }
    }

    private Projection findCached(ProjectController pc, Projection projection) {
        Collection<? extends Projection> projections = pc.getCurrentProject().getLookup().lookupAll(Projection.class);
        LOG.debug("found {} existing projections", projections.size());
        for (Projection proj : projections) {
            if (proj.getName().equals(projection.getName())) {
                return proj;
            }
        }
        return projection;
    }

}
