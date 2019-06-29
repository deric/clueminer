/*
 * Copyright (C) 2011-2019 clueminer.org
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
package org.clueminer.clustering.algorithm;

import java.util.Random;
import org.clueminer.clustering.ClusterHelper;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.Configurator;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.DatasetTools;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
 * @author Thomas Abeel
 * @author Tomas Barton
 *
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class KMeans<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> implements ClusteringAlgorithm<E, C> {

    public static final String NAME = "k-means";
    public static final String K = "k";

    public static final String ITERATIONS = "iterations";

    public static final String MAX_ITERATIONS = "max iterations";

    public static final String SEED = "seed";

    private static final Logger LOG = LoggerFactory.getLogger(KMeans.class);

    /**
     * The number of iterations the algorithm should make. If this value is
     * Integer.INFINITY, then the algorithm runs until the centroids no longer
     * change.
     *
     */
    @Param(name = KMeans.ITERATIONS, description = "number of k-means iterations", required = false, min = 100, max = 150)
    protected int iterations = -1;

    @Param(name = KMeans.ITERATIONS, description = "number of k-means iterations", required = false, min = 1, max = 500)
    protected int maxIterations;


    //min and max values are used as limit for evolutionary algorithms
    @Param(name = KMeans.K, description = "expected number of clusters", required = true, min = 2, max = 25)
    protected int k;

    //@Param(name = KMeans.SEED, description = "random seeed", required = false, min = 1, max = Integer.MAX_VALUE)
    int seed;

    public KMeans() {

    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Execute the KMeans clustering algorithm on the data set that is provided.
     *
     * @param data   data set to cluster
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
        //local variables in order to avoid concurrency issues (with multiple runs)
        int k = params.getInt(KMeans.K);

        if (k <= 1) {
            throw new RuntimeException("Number of clusters should be at least 2");
        }

        if (k > data.size()) {
            throw new RuntimeException("k(" + k + ") can't be larger than dataset size (" + data.size() + ")");
        }

        Random random = ClusterHelper.initSeed(params);
        Distance distanceFunction = ClusterHelper.initDistance(params);

        int iterations = params.getInt(ITERATIONS, 100);

        int maxIterations = params.getInt(MAX_ITERATIONS, 500);

        // Place K points into the space represented by the objects that are
        // being clustered. These points represent the initial group of
        // centroids.
        Instance min = DatasetTools.minAttributes(data);
        Instance max = DatasetTools.maxAttributes(data);
        E[] centroids = (E[]) new Instance[k];
        int instanceLength = data.attributeCount();
        for (int j = 0; j < k; j++) {
            double[] randomInstance = DatasetTools.getRandomInstance(data, random);
            centroids[j] = (E) new DoubleArrayDataRow(randomInstance);
        }

        int iterationCount = 0;
        boolean centroidsChanged = true;
        boolean randomCentroids = true;
        double dist, minDist;
        Instance tmp = new DoubleArrayDataRow(instanceLength);
        while ((randomCentroids || (iterationCount < iterations && centroidsChanged)) && iterationCount <= maxIterations) {
            iterationCount++;
            LOG.trace("Iteration: {} / {} / {}", iterationCount, iterations, centroidsChanged);
            // Assign each object to the group that has the closest centroid.
            int[] assignment = new int[data.size()];
            for (int i = 0; i < data.size(); i++) {
                int tmpCluster = 0;
                double minDistance = distanceFunction.measure(centroids[0], data.instance(i));
                for (int j = 1; j < centroids.length; j++) {
                    dist = distanceFunction.measure(centroids[j], data.instance(i));
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
            double[][] sumPosition = new double[k][instanceLength];
            int[] countPosition = new int[k];
            for (int i = 0; i < data.size(); i++) {
                E in = data.instance(i);
                for (int j = 0; j < instanceLength; j++) {
                    sumPosition[assignment[i]][j] += in.value(j);
                }
                countPosition[assignment[i]]++;
            }
            centroidsChanged = false;
            randomCentroids = false;
            for (int i = 0; i < k; i++) {
                if (countPosition[i] > 0) {
                    for (int j = 0; j < instanceLength; j++) {
                        tmp.set(j, sumPosition[i][j] / (double) countPosition[i]);
                    }
                    if (distanceFunction.measure(tmp, centroids[i]) > 0.0001) {
                        centroidsChanged = true;
                        //in order to avoid unnecessary memory allocation tmp variable is shared
                        centroids[i] = (E) tmp.copy();
                    }
                } else {
                    for (int j = 0; j < instanceLength; j++) {
                        dist = Math.abs(max.value(j) - min.value(j));
                        centroids[i].set(j, (min.value(j) + random.nextDouble() * dist));
                    }
                    randomCentroids = true;
                }

            }

        }
        Clustering output = Clusterings.newList(centroids.length, data);
        output.setParams(params);
        params.put(AlgParams.ALG, getName());
        params.putInt(ITERATIONS, iterations);
        Cluster cluster;
        if (colorGenerator != null) {
            colorGenerator.reset();
        }
        for (int i = 0; i < centroids.length; i++) {
            cluster = output.createCluster(i, data.size());
            if (colorGenerator != null) {
                cluster.setColor(colorGenerator.next());
            }
            //we have to copy attributes settings
            cluster.setAttributes(data.getAttributes());
        }
        for (int i = 0; i < data.size(); i++) {
            int tmpCluster = 0;
            minDist = distanceFunction.measure(centroids[0], data.instance(i));
            for (int j = 0; j < centroids.length; j++) {
                dist = distanceFunction.measure(centroids[j], data.instance(i));
                if (distanceFunction.compare(dist, minDist)) {
                    minDist = dist;
                    tmpCluster = j;
                }
            }
            output.get(tmpCluster).add(data.instance(i));

        }
        return output;
    }

    @Override
    public Configurator<E> getConfigurator() {
        return KMeansConfig.getInstance();
    }

    /**
     * By default initial centers are randomized.
     *
     * @return
     */
    @Override
    public boolean isDeterministic() {
        return false;
    }

}
