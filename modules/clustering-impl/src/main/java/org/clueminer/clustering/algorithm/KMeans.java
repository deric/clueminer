package org.clueminer.clustering.algorithm;

import java.util.Random;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.PartitioningClustering;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.utils.DatasetTools;
import org.clueminer.utils.Props;

/**
 * Implements the K-means algorithms as described by Mac Queen in 1967.
 *
 * <bibtex> J. B. MacQueen (1967): "Some Methods for classification and Analysis
 * of Multivariate Observations, Proceedings of 5-th Berkeley Symposium on
 * Mathematical Statistics and Probability", Berkeley, University of California
 * Press, 1:281-297 </bibtex>
 *
 *
 * @author Thomas Abeel
 * @author Tomas Barton
 *
 */
public class KMeans extends AbstractClusteringAlgorithm implements PartitioningClustering {

    /**
     * The number of clusters.
     */
    private int numberOfClusters = -1;
    /**
     * The number of iterations the algorithm should make. If this value is
     * Integer.INFINITY, then the algorithm runs until the centroids no longer
     * change.
     *
     */
    private int numberOfIterations = -1;
    /**
     * Random generator for this clusterer.
     */
    private Random rg;

    @Param(name = "k", description = "expected number of clusters", required = true)
    private int k;

    /**
     * The centroids of the different clusters.
     */
    private Instance[] centroids;

    /**
     * Constuct a default K-means clusterer with 100 iterations, 4 clusters, a
     * default random generator and using the Euclidean distance.
     */
    public KMeans() {
        this(4);
    }

    /**
     * Constuct a default K-means clusterer with the specified number of
     * clusters, 100 iterations, a default random generator and using the
     * Euclidean distance.
     *
     * @param k the number of clusters to create
     */
    public KMeans(int k) {
        this(k, 100);
    }

    /**
     * Create a new Simple K-means clusterer with the given number of clusters
     * and iterations. The internal random generator is a new one based upon the
     * current system time. For the distance we use the Euclidean n-space
     * distance.
     *
     * @param clusters   - the number of clusters
     * @param iterations - the number of iterations
     */
    public KMeans(int clusters, int iterations) {
        this(clusters, iterations, new EuclideanDistance());
    }

    /**
     * Create a new K-means clusterer with the given number of clusters and
     * iterations. Also the Random Generator for the clusterer is given as
     * parameter.
     *
     * @param clusters   - the number of clusters
     * @param iterations - the number of iterations
     *
     * @param dm         - the distance measure to use
     */
    public KMeans(int clusters, int iterations, DistanceMeasure dm) {
        this.numberOfClusters = clusters;
        this.numberOfIterations = iterations;
        this.distanceFunction = dm;
        rg = new Random(System.currentTimeMillis());
    }

    @Override
    public String getName() {
        return "k-means (MacQueen)";
    }

    @Override
    public DistanceMeasure getDistanceFunction() {
        return distanceFunction;
    }

    @Override
    public void setDistanceFunction(DistanceMeasure dm) {
        this.distanceFunction = dm;
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset) {
        return partition(dataset);
    }

    @Override
    public Clustering partition(Dataset<? extends Instance> dataset, Props params) {
        //@TODO parse algorithm parameters
        return partition(dataset);
    }

    /**
     * Execute the KMeans clustering algorithm on the data set that is provided.
     *
     * @param data data set to cluster
     */
    @Override
    public Clustering<Cluster> partition(Dataset<? extends Instance> data) {
        if (data.isEmpty()) {
            throw new RuntimeException("The dataset should not be empty");
        }
        if (numberOfClusters == 0) {
            throw new RuntimeException("There should be at least one cluster");
        }
        // Place K points into the space represented by the objects that are
        // being clustered. These points represent the initial group of
        // centroids.
        // DatasetTools.
        Instance min = DatasetTools.minAttributes(data);
        Instance max = DatasetTools.maxAttributes(data);
        this.centroids = new Instance[numberOfClusters];
        int instanceLength = data.attributeCount();
        for (int j = 0; j < numberOfClusters; j++) {
//            double[] randomInstance = new double[instanceLength];
//            for (int i = 0; i < instanceLength; i++) {
//                double dist = Math.abs(max.value(i) - min.value(i));
//                randomInstance[i] = (float) (min.value(i) + rg.nextDouble() * dist);
//
//            }
            double[] randomInstance = DatasetTools.getRandomInstance(data, rg);
            this.centroids[j] = new DoubleArrayDataRow(randomInstance);
        }

        int iterationCount = 0;
        boolean centroidsChanged = true;
        boolean randomCentroids = true;
        while (randomCentroids || (iterationCount < this.numberOfIterations && centroidsChanged)) {
            iterationCount++;
            // Assign each object to the group that has the closest centroid.
            int[] assignment = new int[data.size()];
            for (int i = 0; i < data.size(); i++) {
                int tmpCluster = 0;
                double minDistance = distanceFunction.measure(centroids[0], data.instance(i));
                for (int j = 1; j < centroids.length; j++) {
                    double dist = distanceFunction.measure(centroids[j], data.instance(i));
                    if (distanceFunction.compare(dist, minDistance)) {
                        minDistance = dist;
                        tmpCluster = j;
                    }
                }
                assignment[i] = tmpCluster;

            }
            // When all objects have been assigned, recalculate the positions of
            // the K centroids and start over.
            // The new position of the centroid is the weighted center of the
            // current cluster.
            double[][] sumPosition = new double[this.numberOfClusters][instanceLength];
            int[] countPosition = new int[this.numberOfClusters];
            for (int i = 0; i < data.size(); i++) {
                Instance in = data.instance(i);
                for (int j = 0; j < instanceLength; j++) {

                    sumPosition[assignment[i]][j] += in.value(j);

                }
                countPosition[assignment[i]]++;
            }
            centroidsChanged = false;
            randomCentroids = false;
            for (int i = 0; i < this.numberOfClusters; i++) {
                if (countPosition[i] > 0) {
                    double[] tmp = new double[instanceLength];
                    for (int j = 0; j < instanceLength; j++) {
                        tmp[j] = (float) sumPosition[i][j] / countPosition[i];
                    }
                    Instance newCentroid = new DoubleArrayDataRow(tmp);
                    if (distanceFunction.measure(newCentroid, centroids[i]) > 0.0001) {
                        centroidsChanged = true;
                        centroids[i] = newCentroid;
                    }
                } else {
                    double[] randomInstance = new double[instanceLength];
                    for (int j = 0; j < instanceLength; j++) {
                        double dist = Math.abs(max.value(j) - min.value(j));
                        randomInstance[j] = (float) (min.value(j) + rg.nextDouble() * dist);

                    }
                    randomCentroids = true;
                    this.centroids[i] = new DoubleArrayDataRow(randomInstance);
                }

            }

        }
        Clustering output = new ClusterList(centroids.length);
        Props p = output.getParams();
        p.put("algorithm", getName());
        p.putInt("k", numberOfClusters);
        p.putInt("iterations", numberOfIterations);
        BaseCluster cluster;
        if (colorGenerator != null) {
            colorGenerator.reset();
        }
        for (int i = 0; i < centroids.length; i++) {
            cluster = new BaseCluster(data.size());
            if (colorGenerator != null) {
                cluster.setColor(colorGenerator.next());
            }
            cluster.setName("cluster " + (i + 1));
            cluster.setClusterId(i);
            //we have to copy attributes settings
            cluster.setAttributes(data.getAttributes());
            output.put(cluster);
        }
        for (int i = 0; i < data.size(); i++) {
            int tmpCluster = 0;
            double minDistance = distanceFunction.measure(centroids[0], data.instance(i));
            for (int j = 0; j < centroids.length; j++) {
                double dist = distanceFunction.measure(centroids[j], data.instance(i));
                if (distanceFunction.compare(dist, minDistance)) {
                    minDistance = dist;
                    tmpCluster = j;
                }
            }
            output.get(tmpCluster).add(data.instance(i));

        }
        /**
         * associate dataset which was used to create clustering with the
         * clustering
         */
        output.lookupAdd(data);
        return output;
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset, Props props) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
