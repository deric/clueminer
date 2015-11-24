package org.clueminer.partitioning.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.graph.api.NodeIterable;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = Bisection.class)
public class FiducciaMattheyses implements Bisection {

    private Vertex[] vertexes;
    private Graph g;
    private int nodeCount;
    private int maxDegree;

    /**
     * History of swapped vertexes
     */
    private Vertex[] swapHistory;

    private int swapHistoryCost[];

    /**
     * Maximum number of iterations
     */
    private int iterationLimit;
    /**
     * Props constant for setting iterationLimit
     */
    public static final String ITERATIONS = "fm-iterations";

    /**
     * Array containing lists of nodes of the same difference (difference is
     * gain achieved by their switching). Nodes with -maxDegree difference are
     * at 0. position while list with nodes of maxDegree is at 2 * maxDegree.
     * position.
     */
    private Vertex[] differenceBuckets;

    public FiducciaMattheyses() {
        this(20);
    }

    public FiducciaMattheyses(int iterationLimit) {
        this.iterationLimit = iterationLimit;
    }

    @Override
    public String getName() {
        return "Fiduccia-Mattheyses";
    }

    public void setIterationLimit(int iterationLimit) {
        this.iterationLimit = iterationLimit;
    }

    /**
     *
     * @return maximal node degree in the graph
     */
    private void findMaxDegree() {
        maxDegree = Integer.MIN_VALUE;
        for (int i = 0; i < nodeCount; i++) {
            int nodeDegree = g.getDegree(vertexes[i].node);
            if (nodeDegree > maxDegree) {
                maxDegree = nodeDegree;
            }
        }
    }

    private void createVertexes() {
        vertexes = new Vertex[g.getNodeCount()];
        for (Node node : g.getNodes()) {
            vertexes[g.getIndex(node)] = new Vertex(node);
        }
    }

    /**
     * Randomly assigns nodes to clusters at the beginning.
     */
    private void createIntitalPartition() {
        for (int i = 0; i < nodeCount / 2; i++) {
            vertexes[i].cluster = 0;
        }
        for (int i = nodeCount / 2; i < nodeCount; i++) {
            vertexes[i].cluster = 1;
        }
    }

    /**
     * Computes differences of all nodes.
     */
    private void computeDifferences() {
        prepareDifferenceBuckets();
        for (int i = 0; i < nodeCount; i++) {
            int difference = 0;
            Vertex neighbors[] = vertexes[i].getNeighbors();
            for (Vertex neighbor : neighbors) {
                if (vertexes[i].cluster != neighbor.cluster) {
                    difference++;
                } else {
                    difference--;
                }
            }
            addIntoBucket(difference, vertexes[i]);
            vertexes[i].difference = difference;
        }
    }

    private void prepareDifferenceBuckets() {
        if (differenceBuckets == null) {
            differenceBuckets = new Vertex[2 * maxDegree + 1];
        }
        for (int i = 0; i <= maxDegree; i++) {
            differenceBuckets[i] = null;
        }
    }

    /**
     * Adds vertex into bucket with the given difference
     */
    private void addIntoBucket(int difference, Vertex vertex) {
        int position = difference + maxDegree;
        if (differenceBuckets[position] == null) {
            differenceBuckets[position] = vertex;
            vertex.next = null;
        } else {
            differenceBuckets[position].previous = vertex;
            vertex.next = differenceBuckets[position];
            differenceBuckets[position] = vertex;
        }
        vertex.previous = null;
    }

    private void initialize(Graph g) {
        this.g = g;
        nodeCount = g.getNodeCount();
        differenceBuckets = null;
        swapHistory = new Vertex[nodeCount];
        swapHistoryCost = new int[nodeCount];
        createVertexes();
        findMaxDegree();
    }

    /**
     * Finds best vertex to swap from the given cluster. If there is no vertex
     * from the given cluster left, return best vertex from the other cluster.
     */
    private Vertex findBestVertex(int cluster) {
        int i = 2 * maxDegree;
        while (i >= 0) {
            Vertex item = differenceBuckets[i];
            while (item != null) {
                if (item.cluster == cluster) {
                    return item;
                }
                item = item.next;
            }
            i--;
        }
        return findBestVertex(cluster == 0 ? 1 : 0);
    }

    /**
     * Removes given vertex from the differenceBucket
     */
    private void removeFromBucket(Vertex vertex) {
        if (vertex.previous != null) {
            vertex.previous.next = vertex.next;
        } else {
            differenceBuckets[vertex.difference + maxDegree] = vertex.next;
        }
        if (vertex.next != null) {
            vertex.next.previous = vertex.previous;
        }
    }

    /**
     * Adds vertex and its cost to the swapping history
     */
    private void addIntoHistory(int i, Vertex vertex) {
        swapHistory[i] = vertex;
        swapHistoryCost[i] = vertex.difference;
        vertex.used = true;
    }

    /**
     * Change positions in differenceBuckets of vertexes neighboring the given
     * vertex
     */
    private void updateDifferences(Vertex vertex) {
        Vertex[] neighbors = vertex.getNeighbors();
        for (Vertex neighbor : neighbors) {
            if (neighbor.used) {
                continue;
            }
            if (neighbor.cluster == vertex.cluster) {
                removeFromBucket(neighbor);
                neighbor.difference += 2;
                addIntoBucket(neighbor.difference, neighbor);
            } else {
                removeFromBucket(neighbor);
                neighbor.difference -= 2;
                addIntoBucket(neighbor.difference, neighbor);
            }
        }
    }

    /**
     * Find how many swaps should be done to achieve best difference sum. If two
     * or more swap sequences have the same gain, choose the one which is better
     * balanced.
     */
    private int findBestSwaps() {
        int maxDifference = 0;
        int differenceSum = 0;
        int maxDifferenceIndex = -1;
        for (int i = 0; i < nodeCount; i++) {
            differenceSum += swapHistoryCost[i];
            //keep the bisection balanced, only swap pairs
            if (differenceSum > maxDifference && i % 2 == 1) {
                maxDifference = differenceSum;
                maxDifferenceIndex = i;
            }
        }
        // System.out.println("maxDifferenceSum - " + maxDifference);
        return maxDifferenceIndex;
    }

    /**
     * Make the best possible sequence of swaps and return index of the last
     * vertex swapped. If index is -1, no vertexes were swapped.
     *
     * @return Index of last swapped vertex
     */
    private int swapUpToBestIndex() {
        int index = findBestSwaps();
        for (int i = 0; i <= index; i++) {
            swapHistory[i].cluster = swapHistory[i].cluster == 0 ? 1 : 0;
        }
        return index;
    }

    /**
     * Create clusters of nodes form vertex array
     *
     * @return lists of nodes according to clusters
     */
    private ArrayList<ArrayList<Node>> createNodeClusters(int maxPartition) {
        ArrayList<ArrayList<Node>> clusters = new ArrayList<>();
        clusters.add(new ArrayList<Node>(maxPartition));
        clusters.add(new ArrayList<Node>(maxPartition));
        for (Vertex vertex : vertexes) {
            if (vertex.cluster == 0) {
                clusters.get(0).add(vertex.node);
            } else {
                clusters.get(1).add(vertex.node);
            }
        }
        return clusters;
    }

    //Prepares algorithm for the next iteration
    private void reset() {
        for (Vertex vertex : vertexes) {
            vertex.used = false;
        }
    }

    @Override
    public ArrayList<ArrayList<Node>> bisect(Graph g, Props params) {
        initialize(g);
        createIntitalPartition();
        int iterationCounter = 0;
        int maxPartition = params.getInt("max_partition_size", 15);
        //Repeat until no better swap can be done
        while (iterationCounter < iterationLimit) {
            iterationCounter++;
            int index = minimizeCosts();
            reset();
            if (index == -1) {
                break;
            }
        }
        return createNodeClusters(maxPartition);
    }

    private int minimizeCosts() {
        computeDifferences();
        for (int i = 0; i < nodeCount; i++) {
            Vertex bestVertex = findBestVertex(i % 2);
            addIntoHistory(i, bestVertex);
            updateDifferences(bestVertex);
            removeFromBucket(bestVertex);
        }
        return swapUpToBestIndex();
    }

    public void printBuckets() {
        for (int i = 0; i <= maxDegree * 2; i++) {
            Vertex item = differenceBuckets[i];
            System.out.print("Difference " + (i - maxDegree) + ": ");
            while (item != null) {
                System.out.print(g.getIndex(item.node) + " ");
                item = item.next;
            }
            System.out.println("");
        }
    }

    @Override
    public Graph removeUnusedEdges() {
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < nodeCount; j++) {
                if (vertexes[i].cluster != vertexes[j].cluster) {
                    Edge e = g.getEdge(vertexes[i].node, vertexes[j].node);
                    if (e != null) {
                        g.removeEdge(e);
                    }
                }
            }
        }
        return g; // deep copy or new graph needed
    }

    @Override
    public ArrayList<ArrayList<Node>> bisect(Props params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public class Vertex {

        Vertex previous;
        Vertex next;
        Node node;
        int cluster;
        boolean used;
        int difference;

        public Vertex(Node node) {
            this.node = node;
            used = false;
        }

        public Vertex[] getNeighbors() {
            NodeIterable neighbors = g.getNeighbors(node);
            LinkedList<Vertex> result = new LinkedList<>();
            for (Node current : neighbors) {
                result.add(vertexes[g.getIndex(current)]);
            }
            return result.toArray(new Vertex[0]);
        }
    }

}
