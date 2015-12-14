package org.clueminer.clustering.algorithm;

import java.util.Random;
import org.clueminer.clustering.ClusterHelper;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.DatasetTools;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Implements the K-means algorithms as described by Mac Queen in 1967.
 *
 * @param <E>
 * @param <C>
 * @cite J. B. MacQueen (1967): "Some Methods for classification and Analysis
 * of Multivariate Observations, Proceedings of 5-th Berkeley Symposium on
 * Mathematical Statistics and Probability", Berkeley, University of California
 * Press, 1:281-297
 *
 *
 * @author Thomas Abeel
 * @author Tomas Barton
 *
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class KMeans<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> implements ClusteringAlgorithm<E, C> {

    public static final String K = "k";

    public static final String ITERATIONS = "iterations";

    public static final String SEED = "seed";
    /**
     * The number of iterations the algorithm should make. If this value is
     * Integer.INFINITY, then the algorithm runs until the centroids no longer
     * change.
     *
     */
    @Param(name = KMeans.ITERATIONS, description = "number of k-means iterations", required = false, min = 100, max = 105)
    private int iterations = -1;
    /**
     * Random generator for this clusterer.
     */
    private Random random;

    //min and max values are used as limit for evolutionary algorithms
    @Param(name = KMeans.K, description = "expected number of clusters", required = true, min = 2, max = 25)
    private int k;

    //@Param(name = KMeans.SEED, description = "random seeed", required = false, min = 1, max = Integer.MAX_VALUE)
    int seed;

    /**
     * The centroids of the different clusters.
     */
    private Instance[] centroids;

    public KMeans() {

    }

    @Override
    public String getName() {
        return "k-means";
    }

    @Override
    public Distance getDistanceFunction() {
        return distanceFunction;
    }

    @Override
    public void setDistanceFunction(Distance dm) {
        this.distanceFunction = dm;
    }

    /**
     * Execute the KMeans clustering algorithm on the data set that is provided.
     *
     * @param data data set to cluster
     * @param params set of algorithm parameters
     */
    @Override
    public Clustering<E, C> cluster(Dataset<E> data, Props params) {
        if (data == null || data.isEmpty()) {
            throw new RuntimeException("The dataset should not be empty");
        }
        //number of clusters is required
        if (!params.containsKey(KMeans.K)) {
            throw new RuntimeException("Number of clusters (\"" + KMeans.K + "\") must be specified");
        }
        k = params.getInt(KMeans.K);
        if (k <= 1) {
            throw new RuntimeException("Number of clusters should be at least 2");
        }
        if (k > data.size()) {
            throw new RuntimeException("k(" + k + ") can't be larger than dataset size (" + data.size() + ")");
        }

        random = ClusterHelper.initSeed(params);
        distanceFunction = ClusterHelper.initDistance(params);

        iterations = params.getInt(ITERATIONS, 100);

        // Place K points into the space represented by the objects that are
        // being clustered. These points represent the initial group of
        // centroids.
        Instance min = DatasetTools.minAttributes(data);
        Instance max = DatasetTools.maxAttributes(data);
        this.centroids = new Instance[k];
        int instanceLength = data.attributeCount();
        for (int j = 0; j < k; j++) {
            double[] randomInstance = DatasetTools.getRandomInstance(data, random);
            this.centroids[j] = new DoubleArrayDataRow(randomInstance);
        }

        int iterationCount = 0;
        boolean centroidsChanged = true;
        boolean randomCentroids = true;
        while (randomCentroids || (iterationCount < this.iterations && centroidsChanged)) {
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
            double[][] sumPosition = new double[this.k][instanceLength];
            int[] countPosition = new int[this.k];
            for (int i = 0; i < data.size(); i++) {
                E in = data.instance(i);
                for (int j = 0; j < instanceLength; j++) {

                    sumPosition[assignment[i]][j] += in.value(j);

                }
                countPosition[assignment[i]]++;
            }
            centroidsChanged = false;
            randomCentroids = false;
            for (int i = 0; i < this.k; i++) {
                if (countPosition[i] > 0) {
                    double[] tmp = new double[instanceLength];
                    for (int j = 0; j < instanceLength; j++) {
                        tmp[j] = sumPosition[i][j] / (double) countPosition[i];
                    }
                    Instance newCentroid = new DoubleArrayDataRow(tmp);
                    if (distanceFunction.measure(newCentroid, centroids[i]) > 0.0001) {
                        centroidsChanged = true;
                        centroids[i] = newCentroid;
                    }
                } else {
                    for (int j = 0; j < instanceLength; j++) {
                        double dist = Math.abs(max.value(j) - min.value(j));
                        centroids[i].set(j, (min.value(j) + random.nextDouble() * dist));
                    }
                    randomCentroids = true;
                }

            }

        }
        Clustering output = new ClusterList(centroids.length);
        output.setParams(params);
        params.put("algorithm", getName());
        params.putInt(ITERATIONS, iterations);
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

    public void setRandom(Random rand) {
        this.random = rand;
    }

}
