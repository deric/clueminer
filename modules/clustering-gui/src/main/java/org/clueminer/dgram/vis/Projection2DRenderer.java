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

import java.awt.Image;
import org.clueminer.clustering.gui.ClusteringVisualization;
import org.clueminer.clustering.gui.VisualizationTask;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 * @param <R> resulting type
 */
@ServiceProvider(service = ClusteringVisualization.class)
public class Projection2DRenderer<R extends Image> implements ClusteringVisualization<R> {

    private static final String NAME = "Projection";
    private static final Logger LOG = LoggerFactory.getLogger(Projection2DRenderer.class);
    private final ClusteringPlot plot;

    public Projection2DRenderer() {
        plot = new ClusteringPlot();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public R generateImage(VisualizationTask task) {
        plot.setClustering(task.getClustering());
        Image img = plot.generate(task.getWidth(), task.getHeight());

        return (R) img;
    }

}
