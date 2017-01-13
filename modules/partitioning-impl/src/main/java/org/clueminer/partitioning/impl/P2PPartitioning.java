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
package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Partitioning;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = Partitioning.class)
public class P2PPartitioning implements Partitioning {

    private Vertex[] nodes;
    private int nodeCount;
    private boolean[] used;
    private int usedCount;
    private int k;
    private Node[] graphNodes;

    private Graph graph;

    private ArrayList<LinkedList<Node>> clusters;
    private ArrayList<LinkedList<Vertex>> currentNodes;
    private ArrayList<LinkedList<Vertex>> futureNodes;

    public P2PPartitioning() {

    }

    @Override
    public String getName() {
        return "P2P partitioning";
    }

    @Override
    public ArrayList<LinkedList<Node>> partition(int k, Graph g, Props params) {
        initialize(k, g);

        used = new boolean[nodeCount];
        Arrays.fill(used, false);

        findSourceNodes();

        prepareFutureNodes();

        while (usedCount < nodeCount) {
            sortByDegree(currentNodes);
            processCurrentNodes();
            currentNodes = futureNodes;
            prepareFutureNodes();
        }
        return clusters;
    }

    private void initialize(int k, Graph g) {
        graph = g;
        this.k = k;
        nodeCount = graph.getNodeCount();
        graphNodes = graph.getNodes().toArray();
        nodes = new Vertex[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            nodes[i] = new Vertex(graph.getIndex(graphNodes[i]), g.getDegree(graphNodes[i]));
        }
    }

    public Graph removeUnusedEdges() {
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < nodeCount; j++) {
                if (nodes[i].cluster != nodes[j].cluster) {
                    Edge e = graph.getEdge(graphNodes[i], graphNodes[j]);
                    if (e != null) {
                        graph.removeEdge(e);
                    }
                }
            }
        }
        return graph; // deep copy or new graph needed
    }

    private void printClusters() {
        for (int i = 0; i < k; i++) {
            System.out.print("Cluster " + i + ": ");
            for (Node n : clusters.get(i)) {
                System.out.print(graph.getIndex(n) + ", ");
            }
            System.out.println("");
        }
    }

    private void prepareFutureNodes() {
        futureNodes = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            futureNodes.add(new LinkedList<Vertex>());
        }
    }

    //Go through current nodes and process all of them
    private void processCurrentNodes() {
        boolean allEmpty = false;
        while (!allEmpty) {
            allEmpty = true;
            for (int i = 0; i < k; i++) {
                if (usedCount >= nodeCount) {
                    return;
                }
                if (currentNodes.get(i).size() > 0) {
                    allEmpty = false;
                    addNeighbors(graphNodes[currentNodes.get(i).getFirst().index], i);
                    currentNodes.get(i).removeFirst();
                    System.out.println("");
                }
            }
        }
    }

    //Add all neighbors of node, which arent anywhere to cluster and future nodes
    private void addNeighbors(Node n, int cluster) {
        Iterator<Node> neighbors = graph.getNeighbors(n).iterator();
        while (neighbors.hasNext()) {
            Node neighbor = neighbors.next();
            if (used[graph.getIndex(neighbor)] == false) {
                used[graph.getIndex(neighbor)] = true;
                clusters.get(cluster).add(neighbor);
                futureNodes.get(cluster).add(nodes[graph.getIndex(neighbor)]);
                nodes[graph.getIndex(neighbor)].cluster = cluster;
                usedCount++;
            }
        }

    }

    private void findSourceNodes() {
        Vertex[] allSorted = new Vertex[nodeCount];
        System.arraycopy(nodes, 0, allSorted, 0, nodeCount);
        sortByDegree(allSorted);
        clusters = new ArrayList<>(k);
        currentNodes = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            clusters.add(new LinkedList<Node>());
            currentNodes.add(new LinkedList<Vertex>());
            clusters.get(i).add(graphNodes[allSorted[i].index]);
            currentNodes.get(i).add(allSorted[i]);
            nodes[allSorted[i].index].cluster = i;
            used[allSorted[i].index] = true;
            usedCount++;
        }
    }

    public void printNodes() {
        for (int i = 0; i < nodeCount; i++) {
            System.out.println(nodes[i].index);
        }
    }

    private void sortByDegree(Vertex[] set) {
        Arrays.sort(set);
    }

    private void sortByDegree(ArrayList<LinkedList<Vertex>> set) {
        for (int i = 0; i < k; i++) {
            Collections.sort(set.get(i));
        }
    }

    public String printDegrees() {
        String result = "";
        for (int i = 0; i < nodeCount; i++) {
            result += ("Node " + nodes[i].index + ": " + nodes[i].degree + "\n");
        }
        return result;
    }

    @Override
    public void setBisection(Bisection bisection) {
        //nothing to do
    }

    public class Vertex implements Comparable<Vertex> {

        public int degree;
        public int index;
        public boolean used;
        public int cluster;

        public Vertex(int index, int degree) {
            this.degree = degree;
            this.index = index;
            used = false;
        }

        @Override
        public int compareTo(Vertex compareVertex) {
            int compareDegree = compareVertex.degree;
            return compareDegree - this.degree;
        }
    }

}
