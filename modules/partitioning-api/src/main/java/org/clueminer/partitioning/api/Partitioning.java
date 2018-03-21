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
package org.clueminer.partitioning.api;

import java.util.ArrayList;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.utils.Props;

/**
 * Partitioning algorithm should divide given graph into equally sized coherent
 * components.
 *
 * @author Tomas Bruna
 * @param <E>
 */
public interface Partitioning<E extends Instance> {

    String getName();

    /**
     * Partition the graph
     *
     * @param k
     * @param g graph to partition
     * @param params optional parameters
     * @return list of nodes for each cluster
     */
    ArrayList<ArrayList<Node<E>>> partition(int k, Graph g, Props params);

    /**
     * Algorithm for bisection
     *
     * @param bisection
     */
    void setBisection(Bisection bisection);
}
