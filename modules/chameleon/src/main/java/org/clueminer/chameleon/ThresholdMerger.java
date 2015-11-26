package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import org.clueminer.chameleon.similarity.Closeness;
import org.clueminer.chameleon.similarity.Interconnectivity;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.utils.Props;

/**
 * This class merges pairs of clusters exceeding given thresholds for relative
 * interconnectivity and closeness. Merging stops when there is no pair of
 * clusters exceeding the thresholds.
 *
 *
 * @author Tomas Bruna
 * @param <E>
 */
public class ThresholdMerger<E extends Instance> extends AbstractMerger<E> {

    private final double RICThreshold;
    private final double RCLThreshold;
    private boolean merged;
    private final Interconnectivity<E> interconnectivity;
    private final Closeness<E> closeness;
    private static final String name = "threshold merger";

    public ThresholdMerger(Graph g, Bisection bisection, double RICThreshold, double RCLThreshold) {
        this.RICThreshold = RICThreshold;
        this.RCLThreshold = RCLThreshold;
        interconnectivity = new Interconnectivity<>();
        closeness = new Closeness<>();
        this.graph = g;
        this.bisection = bisection;
    }

    @Override
    public String getName() {
        return name;
    }

    public ArrayList<ArrayList<Node<E>>> merge(ArrayList<ArrayList<Node<E>>> clusterList, Props props) {
        ArrayList<ArrayList<Node<E>>> result = clusterList;
        merged = true;
        while (merged) {
            merged = false;
            result = singleMerge(result, props);
        }
        return result;
    }

    private ArrayList<ArrayList<Node<E>>> singleMerge(ArrayList<ArrayList<Node<E>>> clusterList, Props props) {
        createClusters(clusterList, bisection, props);
        computeExternalProperties(clusters);
        initiateClustersForMerging();

        for (int i = 0; i < clusters.size(); i++) {
            double maxRIC = Double.NEGATIVE_INFINITY;
            int index = -1;
            for (int j = 0; j < clusters.size(); j++) {
                if (i == j) {
                    continue;
                }

                double RIC = interconnectivity.getRIC(clusters.get(i), clusters.get(j));
                double RCL = closeness.getRCL(clusters.get(i), clusters.get(j));
                if (RIC > RICThreshold && RCL > RCLThreshold && RIC > maxRIC) {
                    maxRIC = RIC;
                    index = j;
                }
            }
            if (index != -1) {
                merged = true;
                mergeTwoClusters(clusters.get(i), clusters.get(index));
            }
        }
        return getNewClusters();
    }

    /**
     * Prepares clusters for merging
     */
    public void initiateClustersForMerging() {
        for (GraphCluster<E> cluster : clusters) {
            cluster.offsprings = new LinkedList<>();
            cluster.offsprings.add(cluster);
            cluster.setParent(cluster);
        }
    }

    protected void mergeTwoClusters(GraphCluster<E> cluster1, GraphCluster<E> cluster2) {
        if (cluster1.getParent().getClusterId() == cluster2.getParent().getClusterId()) {
            return;
        }
        if (cluster1.getParent().offsprings.size() < cluster2.getParent().offsprings.size()) {
            GraphCluster temp = cluster1;
            cluster1 = cluster2;
            cluster2 = temp;
        }
        cluster1.getParent().offsprings.addAll(cluster2.getParent().offsprings);
        GraphCluster<E> parent = cluster2.getParent();
        for (GraphCluster<E> cluster : parent.offsprings) {
            cluster.setParent(cluster1.getParent());
        }
        parent.offsprings = null;
    }

    /**
     * Creates lists of nodes according to new clusters
     *
     * @return lists of nodes in clusters
     */
    public ArrayList<ArrayList<Node<E>>> getNewClusters() {
        ArrayList<ArrayList<Node<E>>> result = new ArrayList<>(clusters.size());
        for (GraphCluster<E> clust : clusters) {
            if (clust.offsprings != null) {
                ArrayList<Node<E>> list = new ArrayList<>(clust.offsprings.size());
                for (GraphCluster<E> cluster : clust.offsprings) {
                    for (Node<E> node : cluster.getNodes()) {
                        list.add(node);
                    }
                }
                result.add(list);
            }
        }
        return result;
    }

    @Override
    public HierarchicalResult getHierarchy(Dataset<E> dataset, Props pref) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PriorityQueue getQueue(Props pref) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void prefilter(Clustering<E, GraphCluster<E>> clusters, ArrayList<E> noise, Props params) {
        //
    }

}
