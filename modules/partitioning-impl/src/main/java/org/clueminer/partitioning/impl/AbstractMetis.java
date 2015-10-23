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
package org.clueminer.partitioning.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Partitioning;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public abstract class AbstractMetis implements Partitioning {

    /**
     * Run binary and return name of input file
     *
     * @param graph
     * @param k
     * @return
     */
    public abstract String runMetis(Graph graph, int k);

    protected Node[] createMapping(Graph graph) {
        Node[] nodeMapping = new Node[graph.getNodeCount()];
        for (Node node : graph.getNodes()) {
            nodeMapping[graph.getIndex(node)] = node;
        }
        return nodeMapping;
    }

    @Override
    public ArrayList<LinkedList<Node>> partition(int maxPartitionSize, Graph g) {
        int k = (int) Math.ceil(g.getNodeCount() / (double) maxPartitionSize);
        if (k == 1) {
            ArrayList<LinkedList<Node>> nodes = new ArrayList<>();
            nodes.add(new LinkedList<>(g.getNodes().toCollection()));
            return nodes;
        }
        Node[] nodeMapping = createMapping(g);
        String path = runMetis(g, k);
        ArrayList<LinkedList<Node>> clusters = importMetisResult(path, k, nodeMapping);
        Graph clusteredGraph = new EdgeRemover().removeEdges(g, clusters);
        FloodFill f = new FloodFill();
        return f.findSubgraphs(clusteredGraph);
    }

    protected ArrayList<LinkedList<Node>> importMetisResult(String path, int k, Node[] nodeMapping) {
        ArrayList<LinkedList<Node>> result = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            result.add(new LinkedList<Node>());
        }
        File file = new File(path + ".part." + String.valueOf(k));
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                int partitionNumber = Integer.parseInt(line);
                result.get(partitionNumber).add(nodeMapping[i]);
                i++;

            }
            file.delete();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return result;
    }
}
