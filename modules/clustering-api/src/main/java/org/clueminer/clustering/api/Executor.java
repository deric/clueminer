/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.clustering.api;

import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public interface Executor<E extends Instance, C extends Cluster<E>> {

    ClusteringAlgorithm<E, C> getAlgorithm();

    void setAlgorithm(ClusteringAlgorithm<E, C> algorithm);

    /**
     * Run hierarchical clustering of rows in the given dataset
     *
     * @param dataset
     * @param params
     * @return
     */
    HierarchicalResult hclustRows(Dataset<E> dataset, Props params);

    /**
     * Run hierarchical clustering of columns in the given dataset
     *
     * @param dataset
     * @param params
     * @return
     */
    HierarchicalResult hclustColumns(Dataset<E> dataset, Props params);

    /**
     *
     * @param dataset
     * @param params
     * @return
     */
    Clustering<E, C> clusterRows(Dataset<E> dataset, Props params);

    /**
     *
     * @param dataset
     * @param params
     * @return
     */
    DendrogramMapping clusterAll(Dataset<E> dataset, Props params);

}
