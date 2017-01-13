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
package org.clueminer.graph.knn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.graph.api.AbsGraphConvertor;
import static org.clueminer.graph.api.AbsGraphConvertor.DIST_TO_EDGE;
import org.clueminer.graph.api.DIST2EDGE;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.GraphBuilder;
import org.clueminer.graph.api.GraphConvertor;
import org.clueminer.graph.api.Node;
import org.clueminer.math.matrix.SymmetricMatrix;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Construct Nearest neighbor graph
 *
 * @author Tomas Bruna
 * See {@link KnnInitializator} for alternative implementation.
 *
 * @param <E>
 */
@ServiceProvider(service = GraphConvertor.class)
public class KNNGraphBuilder<E extends Instance> extends AbsGraphConvertor<E> implements GraphConvertor<E> {

    /**
     * Triangular distance matrix
     */
    private SymmetricMatrix distance;
    public static final String NAME = "k-NN-builder";

    public KNNGraphBuilder() {
        dm = new EuclideanDistance();
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Find k neighbors of all items in the dataset
     *
     * @param dataset input dataset
     * @return
     */
    private int[][] findNeighbors(Dataset<E> dataset, int k) {
        if (k >= dataset.size()) {
            throw new RuntimeException("Too many neighbours, not enough nodes in dataset");
        }
        buildDistanceMatrix(dataset);
        int[][] nearests = new int[dataset.size()][k];
        for (int i = 0; i < dataset.size(); i++) {
            //put first k neighbours into array and sort them
            int firsts = k;
            int index = 0;
            for (int j = 0; j < firsts; j++) {
                //skip self as neighbour
                if (i == j) {
                    firsts++;
                    continue;
                }
                nearests[i][index] = j;
                insert(nearests, index, i);
                index++;
            }
            //neighbour array full, find closer neighbours from the rest of the dataset
            for (int j = firsts; j < dataset.size(); j++) {
                //skip self as neighbour
                if (i == j) {
                    continue;
                }
                //if distance to central node is smaller then of the furthest current neighbour, add this node to neighbours
                if (distance.get(i, j) < distance.get(i, nearests[i][k - 1])) {
                    nearests[i][k - 1] = j;
                    insert(nearests, k - 1, i);
                }
            }
        }
        return nearests;
    }

    /**
     * Sort neighbors in ascending order by distance to central node
     *
     * @param pos Position of the last element in array with neighbors
     * @param i   Number of central cluster to which neighbors are assigned
     */
    private void insert(int[][] nearests, int pos, int i) {
        while (pos > 0 && distance.get(i, nearests[i][pos]) < distance.get(i, nearests[i][pos - 1])) {
            int temp = nearests[i][pos];
            nearests[i][pos] = nearests[i][pos - 1];
            nearests[i][pos - 1] = temp;
            pos--;
        }
    }

    private void insert(int[] nearests, int pos, int i) {
        while (pos > 0 && distance.get(i, nearests[pos]) < distance.get(i, nearests[pos - 1])) {
            int temp = nearests[pos];
            nearests[pos] = nearests[pos - 1];
            nearests[pos - 1] = temp;
            pos--;
        }
    }

    private void buildDistanceMatrix(Dataset<E> dataset) {
        distance = new SymmetricMatrix(dataset.size(), dataset.size());
        for (int i = 0; i < dataset.size(); i++) {
            for (int j = i + 1; j < dataset.size(); j++) {
                distance.set(i, j, dm.measure(dataset.instance(i), dataset.instance(j)));
            }
        }
    }

    public int[][] getNeighborArray(Dataset<E> dataset, int k) {
        return findNeighbors(dataset, k);
    }

    @Override
    public void createEdges(Graph graph, Dataset<E> dataset, Long[] mapping, Props params) {
        int k = params.getInt("k", 5);
        if (k >= dataset.size()) {
            throw new RuntimeException("Too many neighbours, not enough nodes in dataset");
        }
        buildDistanceMatrix(dataset);
        GraphBuilder f = graph.getFactory();
        for (int i = 0; i < dataset.size(); i++) {
            int[] nearests = new int[k];
            //put first k neighbours into array and sort them
            int firsts = k;
            int index = 0;
            for (int j = 0; j < firsts; j++) {
                //skip self as neighbour
                if (i == j) {
                    firsts++;
                    continue;
                }
                nearests[index] = j;
                insert(nearests, index, i);
                index++;
            }
            //neighbour array full, find closer neighbours from the rest of the dataset
            for (int j = firsts; j < dataset.size(); j++) {
                //skip self as neighbour
                if (i == j) {
                    continue;
                }
                //if distance to central node is smaller then of the furthest current neighbour, add this node to neighbours
                if (distance.get(i, j) < distance.get(i, nearests[k - 1])) {
                    nearests[k - 1] = j;
                    insert(nearests, k - 1, i);
                }
            }
            Node<E> nodeB, nodeA = graph.getNode(mapping[i]);
            E b, a = nodeA.getInstance();
            double dist;
            DIST2EDGE methd = DIST2EDGE.valueOf(params.get(DIST_TO_EDGE, "INVERSE"));
            for (int j = 0; j < k; j++) {
                nodeB = graph.getNode(mapping[nearests[j]]);
                b = nodeB.getInstance();
                dist = dm.measure(a, b);
                graph.addEdge(f.newEdge(nodeA, nodeB, 1, convertDistance(dist, methd), false)); //max val
            }

        }
    }

    /**
     * {@inheritDoc }
     *
     * @param graph
     * @param dataset
     * @param params
     */
    @Override
    public void buildGraph(Graph graph, Dataset<E> dataset, Props params) {
        GraphBuilder gb = graph.getFactory();
        Long[] mapping = gb.createNodesFromInput(dataset, graph);
        createEdges(graph, dataset, mapping, params);
        graph.lookupAdd(dataset);
    }

    /**
     * Create graph where connected nodes are neighbors
     *
     * @param dataset input dataset
     * @param g       graph where output will be stored
     * @param k
     * @return neighbor graph
     */
    public Graph getNeighborGraph(Dataset<E> dataset, Graph g, int k) {
        int[][] nearests = findNeighbors(dataset, k);
        GraphBuilder f = g.getFactory();
        if (g.getNodeCount() == 0) {
            ArrayList<Node> nodes = f.createNodesFromInput(dataset);
            g.addAllNodes(nodes);
        }
        g.addEdgesFromNeigborArray(nearests, k);
        g.lookupAdd(dataset);
        return g;
    }

    @Override
    public void buildGraph(Graph graph, Dataset<E> dataset, Props params, List<E> noise) {
        GraphBuilder gb = graph.getFactory();
        HashSet<Integer> hash = new HashSet<>(noise.size());
        for (E inst : noise) {
            hash.add(inst.getIndex());
        }

        Long[] mapping = gb.createNodesFromInput(dataset, graph, hash);
        createEdges(graph, dataset, mapping, params);
        graph.lookupAdd(dataset);
    }

}
