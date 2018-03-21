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
package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphBuilder;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Partitioning;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * Basic recursive bisection algorithm, by default Fiduccia-Mattheyses bisection
 * is used.
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = Partitioning.class)
public class RecursiveBisection implements Partitioning {

    private int maxNodesInCluster;
    private Bisection bisection;

    public RecursiveBisection() {
        this(new FiducciaMattheyses());
    }

    public RecursiveBisection(Bisection bisection) {
        this.bisection = bisection;
    }

    @Override
    public void setBisection(Bisection bisection) {
        this.bisection = bisection;
    }

    @Override
    public String getName() {
        return "Recursive bisection";
    }

    /**
     * @param max    expected maximum partition size (for memory allocation)
     * @param g
     * @param params
     * @return connected subgraphs
     */
    @Override
    public ArrayList<ArrayList<Node>> partition(int max, Graph g, Props params) {
        maxNodesInCluster = max;
        int expectedSize = g.getNodeCount() / max;
        ArrayList<ArrayList<Node>> clusters;
        Graph clusteredGraph;

        if (g.getNodeCount() < maxNodesInCluster) {
            ArrayList<ArrayList<Node>> nodes = new ArrayList<>(expectedSize);
            nodes.add(new ArrayList<>(g.getNodes().toCollection()));
            return nodes;
        } else {
            clusters = recursivePartition(g, params, expectedSize);
        }
        boolean runFloodFill = params.getBoolean("flood-fill", true);
        if (runFloodFill) {
            if (g.suppportReferences()) {
                clusteredGraph = EdgeRemover.removeEdges(g, clusters);
            } else {
                clusteredGraph = EdgeRemover.safeRemoveEdges(g, clusters);
            }

            FloodFill f = new FloodFill();
            return f.findSubgraphs(clusteredGraph, max);
        }
        return clusters;
    }

    public ArrayList<ArrayList<Node>> recursivePartition(Graph g, Props params, int expectedSize) {
        ArrayList<ArrayList<Node>> output = new ArrayList<>(expectedSize);
        ArrayList<ArrayList<Node>> result = bisection.bisect(g, params);
        int i = 0;
        Graph newGraph;

        if (g.suppportReferences()) {
            while (i < 2) {
                if (result.get(i).size() <= maxNodesInCluster) {
                    output.add(result.get(i));
                } else {
                    newGraph = buildGraphFromCluster(g, result.get(i));
                    //System.out.println("new graph nodes: " + newGraph.getNodeCount() + " edges: " + newGraph.getEdgeCount());
                    output.addAll(recursivePartition(newGraph, params, expectedSize));
                }
                i++;
            }
        } else {
            while (i < 2) {
                if (result.get(i).size() <= maxNodesInCluster) {
                    output.add(result.get(i));
                } else {
                    newGraph = copyGraphFromCluster(g, result.get(i));
                    //System.out.println("new graph nodes: " + newGraph.getNodeCount() + " edges: " + newGraph.getEdgeCount());
                    output.addAll(recursivePartition(newGraph, params, expectedSize));
                }
                i++;
            }
        }

        return output;
    }

    private Graph buildGraphFromCluster(Graph graph, ArrayList<Node> nodes) {
        Graph newGraph = null;
        try {
            newGraph = graph.getClass().newInstance();
            newGraph.ensureCapacity(nodes.size());

            for (Node node : nodes) {
                newGraph.addNode(node);
            }
            for (int i = 0; i < nodes.size(); i++) {
                for (int j = i + 1; j < nodes.size(); j++) {
                    if (graph.isAdjacent(nodes.get(i), nodes.get(j))) {
                        newGraph.addEdge(graph.getEdge(nodes.get(i), nodes.get(j)));
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
        return newGraph;
    }

    private Graph copyGraphFromCluster(Graph graph, ArrayList<Node> nodes) {
        Graph ng = null;
        try {
            ng = graph.getClass().newInstance();
            ng.ensureCapacity(nodes.size());

            GraphBuilder f = ng.getFactory();
            Node neighbor;
            for (Node node : nodes) {
                neighbor = f.newNode(node.getInstance());
                ng.addNode(neighbor);
            }

            Node na, nb;
            for (int i = 0; i < nodes.size(); i++) {
                na = nodes.get(i);
                for (int j = i + 1; j < nodes.size(); j++) {
                    nb = nodes.get(j);
                    if (graph.isAdjacent(na, nb)) {
                        ng.addEdge(f.newEdge(na, nb));
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ng;
    }

}
