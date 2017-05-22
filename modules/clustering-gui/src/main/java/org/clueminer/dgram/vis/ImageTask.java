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
 */
public class ImageTask<E extends Instance, C extends Cluster<E>> {

    private final Clustering<E, C> clustering;
    private final Props prop;
    private final DendrogramVisualizationListener listener;
    private final DendrogramMapping mapping;

    public ImageTask(Clustering<E, C> clustering, Props prop, DendrogramVisualizationListener listener, DendrogramMapping mapping) {
        this.clustering = clustering;
        this.prop = prop;
        this.listener = listener;
        this.mapping = mapping;
    }

    public Clustering<E, C> getClustering() {
        return clustering;
    }

    public int getWidth() {
        return prop.getInt("vis_width", 300);
    }

    public int getHeight() {
        return prop.getInt("vis_height", 300);
    }

    public DendrogramVisualizationListener getListener() {
        return listener;
    }

    public DendrogramMapping getMapping() {
        return mapping;
    }

}
