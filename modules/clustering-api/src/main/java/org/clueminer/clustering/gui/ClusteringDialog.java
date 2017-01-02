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

import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 * Each algorithm can have a GUI panel which should implement this interface
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public interface ClusteringDialog<E extends Instance, C extends Cluster<E>> {

    /**
     * For lookup purposes, should be unique
     *
     * @return
     */
    String getName();

    /**
     * Parameters configured by user (or default ones)
     *
     * @return
     */
    Props getParams();

    /**
     * GUI which will be embedded into another dialog (should not contain any
     * OK/Cancel buttons)
     *
     * @return
     */
    JPanel getPanel();

    /**
     * Return true when UI is compatible with given algorithm
     *
     * @param algorithm
     * @param dataset in some cases dataset could be used for setting boundaries
     * of parameters
     * @return
     */
    boolean isUIfor(ClusteringAlgorithm<E, C> algorithm, Dataset<E> dataset);
}
