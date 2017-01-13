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
package org.clueminer.partitioning.api;

import java.util.ArrayList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Bruna
 */
public interface Bisection {

    String getName();

    /**
     * Bisect the graph
     *
     * @param g graph to partition
     * @param params
     * @return list of nodes for each cluster
     */
    ArrayList<ArrayList<Node>> bisect(Graph g, Props params);

    /**
     * Bisect the graph
     *
     * @param params
     * @return list of nodes for each cluster
     */
    ArrayList<ArrayList<Node>> bisect(Props params);

    /**
     * Remove edges between clusters which were created by bisection
     *
     * @return bisected graph
     */
    Graph removeUnusedEdges();
}
