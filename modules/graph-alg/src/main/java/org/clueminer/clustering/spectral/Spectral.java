/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.clustering.spectral;

import org.clueminer.clustering.ClusterHelper;
import org.clueminer.clustering.algorithm.KMeans;
import static org.clueminer.clustering.api.AlgParams.CLUSTERING_TYPE;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.Configurator;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.EVD;
import smile.math.matrix.Matrix;

/**
 * Spectral Clustering with native ARPACK support
 *
 * @author deric
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class Spectral<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> implements ClusteringAlgorithm<E, C> {

    public static final String NAME = "Spectral";
    public static final String K = "k";
    public static final String SIGMA = "sigma";
    public static final String EPS = "eps";
    public static final String MATRIX_CONV = "matrix_conv";
    public static final String SP_ALG = "spectral_clustering_alg";
    public static final String KMEANS_ITERATIONS = "K-means iterations";

    private static final Logger LOG = LoggerFactory.getLogger(SpectralClustering.class);

    @Param(name = SpectralClustering.MATRIX_CONV, description = "neighborhood matrix alg", required = true)
    private String matrixConv;

    @Param(name = SpectralClustering.SP_ALG, description = "spectral clustering alg", required = true)
    private String spAlg;

    //min and max values are used as limit for evolutionary algorithms
    @Param(name = SpectralClustering.K, description = "expected number of clusters", required = true, min = 2, max = 25)
    private int k;

    // Sigma is an user specified scalling factor
    @Param(name = SpectralClustering.SIGMA, description = "an user specified scaling factor for the similarity matrix", required = true, min = 0.01)
    private double sigma;

    @Param(name = KMEANS_ITERATIONS, description = "K-means iterations", required = false, min = 1)
    private int kmeansIterations;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> data, Props params) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("The dataset should not be empty");
        }

        //number of clusters is required
        if (!params.containsKey(SpectralClustering.K)) {
            throw new IllegalArgumentException("Number of clusters (\"" + SpectralClustering.K + "\") must be specified");
        }

        String matrixConv = params.get(SpectralClustering.MATRIX_CONV, "epsilon-neighborhood matrix");

        String spAlg = params.get(SpectralClustering.SP_ALG, "Unnormalized SP");

        int k = params.getInt(SpectralClustering.K, 2);

        if (k < 2) {
            throw new IllegalArgumentException("Number of clusters should be at least 2, got " + k);
        }

        if (k > data.size()) {
            throw new IllegalArgumentException("k(" + k + ") can't be larger than dataset size (" + data.size() + ")");
        }

        distanceFunction = ClusterHelper.initDistance(params);

        double sigma = params.getDouble(SpectralClustering.SIGMA, 2);

        if (sigma <= 0.0) {
            throw new IllegalArgumentException("Invalid standard deviation of Gaussian kernel '" + sigma + "' can not be less than 0.0");
        }

        int kmeansIterations = params.getInt(SpectralClustering.KMEANS_ITERATIONS, 100);

        if (kmeansIterations <= 0) {
            throw new IllegalArgumentException("K-means iteration value should be at least 1");
        }
        LOG.info("1) Pre-processing - construct a matrix representation of the graph");
        LOG.debug("computing similarity matrix");
        int n = data.size();
        LOG.debug("k = {}, n = {}", k, n);

        double gamma = -0.5 / (sigma * sigma);
        double dist;
        DenseMatrix W = Matrix.zeros(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                dist = Math.exp(gamma * distanceFunction.measure(data.get(i), data.get(j)));
                W.set(i, j, dist);
                W.set(j, i, dist);
            }
        }

        double[] D = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                D[i] += W.get(i, j);
            }

            if (D[i] < 1E-5) {
                LOG.error(String.format("Small D[%d] = %f. The data may contain outliers.", i, D[i]));
            }

            D[i] = 1.0 / Math.sqrt(D[i]);
        }

        DenseMatrix L = W;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                double l = D[i] * W.get(i, j) * D[j];
                L.set(i, j, l);
                L.set(j, i, l);
            }
        }

        L.setSymmetric(true);
        EVD eigen = L.eigen(k);
        double[][] Y = eigen.getEigenVectors().array();
        for (int i = 0; i < n; i++) {
            smile.math.Math.unitize2(Y[i]);
        }
        Dataset<E> dataY = new ArrayDataset(Y);
        LOG.debug("Y dimensions: {}x{}", data.size(), data.attributeCount());
        LOG.info("3) Grouping - assign points to two or more clusters, based on the new representation");
        LOG.debug("computing K-means");
        Clustering kmeansResult = computeKmeans(dataY, k, kmeansIterations);

        LOG.info("Spectral cluster summary:");
        for (int i = 0; i < kmeansResult.size(); i++) {
            LOG.info(" - Cluster #{}: {} nodes", i + 1, kmeansResult.get(i).size());
        }

        LOG.info("Creating final output: assign the results to the original data");
        LOG.info("Sigma: {}", sigma);
        return getFinalOutputClusters(kmeansResult, data);
    }

    private Clustering computeKmeans(Dataset<? extends Instance> eigDataset, int k, int iter) {
        KMeans kMeans = new KMeans();
        Props kMeansParams = new Props();
        kMeansParams.putInt(KMeans.K, k);
        kMeansParams.putInt(KMeans.MAX_ITERATIONS, iter);
        return kMeans.cluster(eigDataset, kMeansParams);
    }

    private Clustering getFinalOutputClusters(Clustering kmeansResult, Dataset<E> data) {
        Clustering output = Clusterings.newList(kmeansResult.size(), data);
        Cluster cluster;

        if (colorGenerator != null) {
            colorGenerator.reset();
        }

        for (int i = 0; i < kmeansResult.size(); i++) {
            cluster = output.createCluster(i, data.size());
            if (colorGenerator != null) {
                cluster.setColor(colorGenerator.next());
            }
            //we have to copy attributes settings
            cluster.setAttributes(data.getAttributes());
        }

        for (int i = 0; i < kmeansResult.size(); i++) {
            Cluster c = kmeansResult.get(i);
            for (int j = 0; j < c.size(); j++) {
                Instance inst = c.get(j);
                int index = inst.getIndex();
                output.get(i).add(data.instance(index));
            }
        }

        return output;
    }

    @Override
    public Configurator<E> getConfigurator() {
        return SpectralClusteringConfig.getInstance();
    }

    @Override
    public boolean isDeterministic() {
        return true;
    }

    public boolean clusterRows(Props params) {
        if (params.containsKey(CLUSTERING_TYPE)) {
            return ClusteringType.parse(params.getObject(CLUSTERING_TYPE)) != ClusteringType.COLUMNS_CLUSTERING;
        }
        return true; //by default cluster rows
    }

}
