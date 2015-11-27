package org.clueminer.chameleon;

import edu.umn.metis.HMetisBisector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.graph.api.Edge;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.hclust.DClusterLeaf;
import org.clueminer.hclust.DTreeNode;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Merger;
import org.clueminer.utils.PairValue;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Bruna
 * @param <E>
 */
public abstract class AbstractMerger<E extends Instance> implements Merger<E> {

    /**
     * Original, not partitioned graph.
     */
    protected Graph graph;

    /**
     * Assigns each node to cluster.
     */
    protected int nodeToCluster[];

    /**
     * Set of merged clustering which are ignored. They could also be deleted
     * but deleting them from cluster array, external properties matrix and
     * priority queue would be too expensive.
     */
    protected HashSet<Integer> blacklist = new HashSet<>();

    protected DendroNode[] nodes;

    /**
     * Tree height
     */
    protected double height;

    protected Bisection bisection;

    /**
     * Current tree level
     */
    protected int level;

    protected Clustering<E, GraphCluster<E>> clusters;

    /**
     * Distance measure used by the k-NN algorithm
     */
    protected Distance dm;

    @Override
    public ArrayList<E> initialize(ArrayList<ArrayList<Node<E>>> clusterList, Graph<E> graph, Bisection bisection, Props params) {
        return initialize(clusterList, graph, bisection, params, null);
    }

    /**
     * Try to detect noise
     *
     * @param clusters
     * @param noise
     * @param params
     */
    public abstract void prefilter(Clustering<E, GraphCluster<E>> clusters, ArrayList<E> noise, Props params);

    @Override
    public ArrayList<E> initialize(ArrayList<ArrayList<Node<E>>> clusterList, Graph<E> graph, Bisection bisection, Props params, ArrayList<E> noise) {
        this.graph = graph;
        this.bisection = bisection;
        blacklist = new HashSet<>();
        clusters = createClusters(clusterList, bisection, params);
        prefilter(clusters, noise, params);
        assignNodesToCluters(clusters);
        computeExternalProperties(clusters);
        nodes = initiateTree(clusters, noise);
        return noise;
    }

    protected double computeMedianCl(Clustering<E, GraphCluster<E>> clusters) {
        double connectivities[] = new double[clusters.size()];
        int i = 0;
        for (GraphCluster cluster : clusters) {
            connectivities[i++] = cluster.getACL();
        }
        Arrays.sort(connectivities);
        if (connectivities.length % 2 == 0) {
            return (connectivities[connectivities.length / 2] + connectivities[connectivities.length / 2 - 1]) / 2;
        } else {
            return connectivities[connectivities.length / 2];
        }
    }

    /**
     * Creates clustering from lists of nodes
     *
     * @param clusterList
     * @param bisection
     * @param props
     * @return list of clustering
     */
    public Clustering<E, GraphCluster<E>> createClusters(ArrayList<ArrayList<Node<E>>> clusterList, Bisection bisection, Props props) {
        Clustering<E, GraphCluster<E>> clustering = (Clustering<E, GraphCluster<E>>) Clusterings.newList(clusterList.size());
        int i = 0;
        GraphCluster grc;
        for (ArrayList<Node<E>> cluster : clusterList) {
            grc = new GraphCluster(cluster, graph, i, bisection, props);
            clustering.add(grc);
            i++;
        }
        return clustering;
    }

    public void renumberClusters(Clustering<E, GraphCluster<E>> clusters, ArrayList<E> noise) {
        if (noise != null && noise.size() > 0) {
            int i = 0;
            for (GraphCluster cluster : clusters) {
                cluster.setClusterId(i);
                i++;
            }
        }
    }

    /**
     * Assigns clustering to nodes according to list of clustering in each node.
     * Having clustering assigned to nodes can be advantageous in some cases
     *
     * @param clusters
     */
    protected void assignNodesToCluters(Clustering<E, GraphCluster<E>> clusters) {
        nodeToCluster = new int[graph.getNodeCount()];
        //Fill with -1 so we can easily recognise nodes assigned to noise
        Arrays.fill(nodeToCluster, -1);
        int i = 0;
        for (GraphCluster<E> cluster : clusters) {
            for (Node<E> node : cluster.getNodes()) {
                nodeToCluster[graph.getIndex(node)] = i;
            }
            i++;
        }
    }

    /**
     * Computes external interconnectivity and closeness between every two
     * clustering. Computed values are stored in a triangular matrix.
     *
     * Goes through all edges and if the edge connects different clustering, the
     * external values are updated
     *
     * @param clusters
     */
    public void computeExternalProperties(Clustering<E, GraphCluster<E>> clusters) {
        GraphPropertyStore gps = new GraphPropertyStore(clusters.size());
        int firstClusterID, secondClusterID;
        for (Edge edge : graph.getEdges()) {
            firstClusterID = nodeToCluster[graph.getIndex(edge.getSource())];
            secondClusterID = nodeToCluster[graph.getIndex(edge.getTarget())];
            //noise
            if (firstClusterID == -1 || secondClusterID == -1) {
                continue;
            }
            if (firstClusterID != secondClusterID) {
                gps.updateWeight(firstClusterID, secondClusterID, edge.getWeight());
            }
        }
        graph.lookupAdd(gps);
    }

    /**
     * Creates tree leaves and fills them with nodes.
     *
     * @param clusters
     * @param noise
     * @return
     */
    protected DendroNode[] initiateTree(Clustering<E, GraphCluster<E>> clusters, ArrayList<E> noise) {
        DendroNode[] treeNodes;
        //Create special node for noise only if noise is present
        treeNodes = new DendroNode[(2 * clusters.size())];
        if (noise != null && noise.size() > 0) {
            treeNodes[2 * clusters.size() - 1] = new DClusterLeaf(clusters.size(), noise);
            treeNodes[2 * clusters.size() - 1].setHeight(0);
            treeNodes[2 * clusters.size() - 1].setLevel(0);
        } else {
            treeNodes[2 * clusters.size() - 1] = null;
        }
        for (int i = 0; i < clusters.size(); i++) {
            treeNodes[i] = new DClusterLeaf(i, createInstanceList(clusters.get(i).getNodes()));
            treeNodes[i].setHeight(0);
            treeNodes[i].setLevel(0);
        }
        return treeNodes;
    }

    protected ArrayList<Instance> createInstanceList(ArrayList<Node<E>> nodes) {
        ArrayList<Instance> out = new ArrayList<>(nodes.size());
        for (Node node : nodes) {
            out.add(node.getInstance());
        }
        return out;
    }

    public void setGraph(Graph g) {
        this.graph = g;
    }

    public void setBisection(Bisection b) {
        this.bisection = b;
    }

    /**
     * Fetches graph from a GraphCluster instance
     *
     * @param clust
     * @return
     */
    public GraphPropertyStore getGraphPropertyStore(GraphCluster<E> clust) {
        Graph g = clust.getGraph();
        GraphPropertyStore gps = g.getLookup().lookup(GraphPropertyStore.class);
        if (gps == null) {
            throw new RuntimeException("graph property store was not found");
        }
        return gps;
    }

    /**
     * Computes external properties of the merged cluster and adds them to the
     * end of the external properties matrix.
     *
     * @param cluster new cluster
     * @param c1 clustering that are being merged
     * @param c2 clustering that are being merged
     */
    protected void updateExternalProperties(GraphCluster<E> cluster, GraphCluster<E> c1, GraphCluster<E> c2) {
        double eic1, eic2, cnt1, cnt2, eic, ecl, cnt;
        for (int i = 0; i < clusters.size() - 1; i++) {
            if (blacklist.contains(i)) {
                continue;
            }
            GraphPropertyStore gps = getGraphPropertyStore(c1);

            eic1 = gps.getEIC(c1.getClusterId(), i);
            cnt1 = gps.getCnt(c1.getClusterId(), i);

            eic2 = gps.getEIC(c2.getClusterId(), i);
            cnt2 = gps.getCnt(c2.getClusterId(), i);

            ecl = 0;
            eic = eic1 + eic2;

            cnt = cnt1 + cnt2;
            if (cnt > 0) {
                ecl = eic / cnt;
            }
            gps.set(i, cluster.getClusterId(), eic, ecl, cnt);
        }
    }

    /**
     * Adds node representing new cluster (the one created by merging) to
     * dendroTree
     *
     * @param pair
     * @param pref
     */
    protected void addIntoTree(PairValue<GraphCluster<E>> pair, Props pref) {
        DendroNode left = nodes[pair.A.getClusterId()];
        DendroNode right = nodes[pair.B.getClusterId()];
        double sim = pair.getValue();
        if (bisection instanceof HMetisBisector) {
            stdAdd(left, right, sim);
        } else {
            improvedAdd(left, right, sim);
        }
    }

    private void stdAdd(DendroNode left, DendroNode right, double sim) {
        DTreeNode newNode = new DTreeNode(clusters.size() - 1);
        newNode.setLeft(left);
        newNode.setRight(right);
        /* if (sim > 10) {
         sim = 10;
         }*/
        if (Double.isNaN(sim) || sim < 0.005) {
            sim = 0.005;
        }
        height += 1 / sim;
        newNode.setHeight(height);
        newNode.setLevel(level++);
        nodes[clusters.size() - 1] = newNode;
    }

    private void improvedAdd(DendroNode left, DendroNode right, double sim) {
        DTreeNode newNode = new DTreeNode(clusters.size() - 1);
        newNode.setLeft(left);
        newNode.setRight(right);

        if (sim > 10) {
            sim = 10;
        }
        if (sim < 0.005) {
            sim = 0.005;
        }
        height += 1 / sim;
        newNode.setHeight(height);
        newNode.setLevel(level++);
        nodes[clusters.size() - 1] = newNode;
    }

    @Override
    public Clustering<E, GraphCluster<E>> getClusters() {
        return clusters;
    }

    @Override
    public void setDistanceMeasure(Distance dm) {
        this.dm = dm;
    }

}
