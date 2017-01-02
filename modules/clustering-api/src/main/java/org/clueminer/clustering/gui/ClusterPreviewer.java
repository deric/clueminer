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

import java.awt.Graphics;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;

/**
 * General interface for cluster visualization which could be looked up and then
 * exported.
 *
 * Previewer should be a paint-able graphic component (e.g. JPanel)
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public interface ClusterPreviewer<E extends Instance, C extends Cluster<E>> {

    public void setClustering(Clustering<E, C> clustering);

    /**
     * ClusterPreviewer should inherit from JComponent
     *
     * @param g
     */
    public void paint(Graphics g);
}
