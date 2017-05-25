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
package org.clueminer.clustering.gui;

import java.awt.Image;
import java.util.concurrent.Callable;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 * @param <R> resulting image
 */
public class VisualizationTask<E extends Instance, C extends Cluster<E>, R extends Image> implements Callable<R> {

    private final Clustering<E, C> clustering;
    private final Props prop;
    private final DendrogramVisualizationListener listener;
    private final DendrogramMapping mapping;
    private final ClusteringVisualization<R> renderer;

    public VisualizationTask(Clustering<E, C> clustering, Props prop, DendrogramVisualizationListener listener, DendrogramMapping mapping, ClusteringVisualization<R> renderer) {
        this.clustering = clustering;
        this.prop = prop;
        this.listener = listener;
        this.mapping = mapping;
        this.renderer = renderer;
    }

    public Clustering<E, C> getClustering() {
        return clustering;
    }

    public int getWidth() {
        return prop.getInt("visualize.img_width", 64);
    }

    public int getHeight() {
        return prop.getInt("visualize.img_height", 64);
    }

    public DendrogramVisualizationListener getListener() {
        return listener;
    }

    public DendrogramMapping getMapping() {
        return mapping;
    }

    public Props getProps() {
        return prop;
    }

    @Override
    public R call() throws Exception {
        return renderer.generateImage(this);
    }

}
