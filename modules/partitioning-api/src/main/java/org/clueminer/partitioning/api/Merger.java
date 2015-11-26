/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.partitioning.api;

import java.util.ArrayList;
import java.util.PriorityQueue;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 * @param <E>
 */
public interface Merger<E extends Instance> {

    /**
     * A unique name of the method
     *
     * @return method identification
     */
    String getName();

    /**
     *
     * @param clusterList
     * @param graph
     * @param bisection
     * @param params
     * @return
     */
    ArrayList<E> initialize(ArrayList<ArrayList<Node<E>>> clusterList, Graph<E> graph, Bisection bisection, Props params);

    ArrayList<E> initialize(ArrayList<ArrayList<Node<E>>> clusterList, Graph<E> graph, Bisection bisection, Props params, ArrayList<E> noise);

    /**
     * Merge clusters while creating a hierarchical structure (dendrogram)
     *
     * @param dataset
     * @param pref
     * @return
     */
    HierarchicalResult getHierarchy(Dataset<E> dataset, Props pref);

    PriorityQueue getQueue(Props pref);

    /**
     * List of clusters
     *
     * @return clusters to merge
     */
    Clustering<E, ? extends Cluster<E>> getClusters();

    /**
     * Set distance measure used for computation of similarity graph
     *
     * @param dm a distance function
     */
    void setDistanceMeasure(Distance dm);
}
