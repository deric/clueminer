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
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
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
    List<Instance> initialize(ArrayList<LinkedList<Node<E>>> clusterList, Graph graph, Bisection bisection, Props params);

    List<Instance> initialize(ArrayList<LinkedList<Node<E>>> clusterList, Graph graph, Bisection bisection, Props params, List<Instance> noise);

    /**
     * Merge clusters while creating a hierarchical structure (dendrogram)
     *
     * @param dataset
     * @param pref
     * @return
     */
    HierarchicalResult getHierarchy(Dataset<E> dataset, Props pref);

    PriorityQueue getQueue(Props pref);
}
