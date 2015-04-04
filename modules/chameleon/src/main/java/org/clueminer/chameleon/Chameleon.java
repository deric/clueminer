package org.clueminer.chameleon;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.graph.adjacencyMatrix.AdjMatrixGraph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.impl.FiducciaMattheyses;
import org.clueminer.partitioning.impl.RecursiveBisection;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class Chameleon extends AbstractClusteringAlgorithm implements AgglomerativeClustering {

    public static class Builder {

        private int k = -1;
        private int maxPartitionSize = -1;
        private DistanceMeasure distanceMeasure = new EuclideanDistance();
        private Bisection bisection = new FiducciaMattheyses();
        private double closenessPriority = 1;
        private SimilarityMeasure similarityMeasure = SimilarityMeasure.IMPROVED;

        public Chameleon build() {
            Chameleon ch = new Chameleon();
            ch.k = k;
            ch.maxPartitionSize = maxPartitionSize;
            ch.distanceMeasure = distanceMeasure;
            ch.bisection = bisection;
            ch.closenessPriority = closenessPriority;
            ch.similarityMeasure = similarityMeasure;
            return ch;
        }

        /**
         *
         * @param k Number of neighbors to create in k-NN graph representing the
         * input data.
         * @return
         */
        public Builder k(int k) {
            if (k < 1) {
                throw new IllegalArgumentException("k has to be greater than 0 in order to assign at least one neighbor.");
            }
            this.k = k;
            return this;
        }

        /**
         *
         * @param maxPartitionSize Maximum number of nodes in each partition
         * after the execution of the partitioning algorithm.
         * @return
         */
        public Builder maxPartitionSize(int maxPartitionSize) {
            if (maxPartitionSize < 1) {
                throw new IllegalArgumentException("Partition size has to be greater than 0.");
            }
            this.maxPartitionSize = maxPartitionSize;
            return this;
        }

        /**
         *
         * @param bisection Bisection algorithm used in partitioning and
         * merging.
         * @return
         */
        public Builder bisection(Bisection bisection) {
            this.bisection = bisection;
            return this;
        }

        /**
         *
         * @param closenessPriority If bigger than 1, algorithm gives a higher
         * importance to the relative closeness of clusters during merging,
         * otherwise, if lesser than 1, to interconnectivity.
         * @return
         */
        public Builder closenessPriority(double closenessPriority) {
            this.closenessPriority = closenessPriority;
            return this;
        }

        /**
         *
         * @param similarityMeasure Similarity function used to compute
         * similarity between two clusters during merging.
         * @return
         */
        public Builder similarityMeasure(SimilarityMeasure similarityMeasure) {
            this.similarityMeasure = similarityMeasure;
            return this;
        }

        public Builder distanceMeasure(DistanceMeasure dm) {
            distanceMeasure = dm;
            return this;
        }
    }

    /**
     * Number of neighbors for each node in k-NN algorithm.
     */
    private int k;

    /**
     * Maximum number of nodes in each partition after the execution of the
     * partitioning algorithm.
     */
    private int maxPartitionSize;

    /**
     * Bisection algorithm used in partitioning and merging.
     */
    private Bisection bisection;

    /**
     * If bigger than 1, algorithm gives a higher importance to the relative
     * closeness of clusters during merging, otherwise, if lesser than 1, to
     * interconnectivity.
     */
    private double closenessPriority;

    /**
     * Similarity function used to compute similarity between two clusters
     * during merging.
     */
    private SimilarityMeasure similarityMeasure;

    private DistanceMeasure distanceMeasure;

    @Override
    public DistanceMeasure getDistanceFunction() {
        return distanceMeasure;
    }

    @Override
    public void setDistanceFunction(DistanceMeasure dm) {
        this.distanceMeasure = dm;
    }

    @Override
    public String getName() {
        return "Chameleon";
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset, Props props) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset) {
        KNN knn = new KNN(k);

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);

        RecursiveBisection rb = new RecursiveBisection(bisection);
        ArrayList<LinkedList<Node>> partitioningResult = rb.partition(maxPartitionSize, g);

        Merger m = new PairMerger(g, bisection, closenessPriority, similarityMeasure);

        m.merge(partitioningResult);

        return null;

    }

    @Override
    public HierarchicalResult hierarchy(Dataset<? extends Instance> dataset, Props pref) {
        if (pref != null) {
            AgglParams params = new AgglParams(pref);
            if (!params.clusterRows()) {
                return null;
            }
        }

        int datasetK = findK(dataset);
        int datasetMaxPSize = findMaxPartitionSize(dataset);

        KNN knn = new KNN(datasetK);

        AdjMatrixGraph g = new AdjMatrixGraph(dataset.size());
        g = (AdjMatrixGraph) knn.getNeighborGraph(dataset, g);

        RecursiveBisection rb = new RecursiveBisection(bisection);
        ArrayList<LinkedList<Node>> partitioningResult = rb.partition(datasetMaxPSize, g);

        PairMerger m = new PairMerger(g, bisection, closenessPriority, similarityMeasure);

        return m.getHierarchy(partitioningResult, dataset);
    }

    private int findK(Dataset<? extends Instance> dataset) {
        if (k == -1) {
            return (int) (Math.log(dataset.size()) / Math.log(2));
        } else {
            return k;
        }
    }

    private int findMaxPartitionSize(Dataset<? extends Instance> dataset) {
        if (maxPartitionSize == -1) {
            return 10;
        } else {
            return maxPartitionSize;
        }
    }

    @Override
    public boolean isLinkageSupported(String linkage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
