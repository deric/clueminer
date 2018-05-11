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

import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.PriorityQueue;
import org.clueminer.clustering.ClusterHelper;
import org.clueminer.clustering.aggl.AgglClustering;
import org.clueminer.clustering.aggl.Element;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.AlgParams;
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
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceProvider(service = ClusteringAlgorithm.class)
public class SpectralClustering<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> implements ClusteringAlgorithm<E, C> {

    public static final String NAME = "Spectral Clustering";
    public static final String K = "k";
    public static final String SIGMA = "Sigma";
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
    @Param(name = SpectralClustering.SIGMA, description = "an user specified scaling factor for the similarity matrix", required = true, min = 0.0)
    private double sigma;

    @Param(name = KMEANS_ITERATIONS, description = "K-means iterations", required = false, min = 1)
    private int kmeansIterations;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> data, Props params) {

        basicChecksAndInits(data, params);

        LOG.info("1) Pre-processing - construct a matrix representation of the graph");
        LOG.debug("computing similarity matrix");
        Matrix similarityMatrix = computeSimilarityMatrix(data, params);
        LOG.info("sim matrix rank: {}", similarityMatrix.rank());

        LOG.debug("compute neighborhood graph matrix");
        MatrixConvertor nmf = GraphMatrixConvertorFactory.getInstance().getProvider(matrixConv);
        Matrix neighborhoodMatrix = nmf.buildMatrix(similarityMatrix, params);
        LOG.info("neighborhood graph matrix rank: {}", neighborhoodMatrix.rank());

        EigenVectorConvertor evf = SpectralEigenVectorsFactory.getInstance().getProvider(spAlg);
        Dataset<? extends Instance> eigDataset = evf.buildEigVecsMatrix(neighborhoodMatrix, k);
        LOG.info("eigen dataset size: {}", eigDataset.size());

        LOG.info("3) Grouping - assign points to two or more clusters, based on the new representation");
        LOG.debug("computing K-means");
        Clustering kmeansResult = computeKmeans(eigDataset);

        LOG.info("Spectral cluster summary:");
        for (int i = 0; i < kmeansResult.size(); i++) {
            LOG.info(" - Cluster #{}: {} nodes", i + 1, kmeansResult.get(i).size());
        }

        LOG.info("Creating final output: assign the results to the original data");
        LOG.info("Sigma: {}", sigma);
        return getFinalOutputClusters(kmeansResult, data);
    }

    private void basicChecksAndInits(Dataset<E> data, Props params) {
        if (data == null || data.isEmpty()) {
            throw new RuntimeException("The dataset should not be empty");
        }

        //number of clusters is required
        if (!params.containsKey(SpectralClustering.K)) {
            throw new RuntimeException("Number of clusters (\"" + SpectralClustering.K + "\") must be specified");
        }

        matrixConv = params.get(SpectralClustering.MATRIX_CONV, "epsilon-neighborhood matrix");

        spAlg = params.get(SpectralClustering.SP_ALG, "Unnormalized SP");
        
        k = params.getInt(SpectralClustering.K, 2);

        if (k <= 1) {
            throw new RuntimeException("Number of clusters should be at least 2");
        }

        if (k > data.size()) {
            throw new RuntimeException("k(" + k + ") can't be larger than dataset size (" + data.size() + ")");
        }

        distanceFunction = ClusterHelper.initDistance(params);

        sigma = params.getDouble(SpectralClustering.SIGMA, 2);

        if (sigma < 0.0) {
            throw new RuntimeException("Sigma value '" + sigma + "' can not be less than 0.0");
        }

        kmeansIterations = params.getInt(SpectralClustering.KMEANS_ITERATIONS, 100);

        if (kmeansIterations <= 0) {
            throw new RuntimeException("K-means iteration value should be at least 1");
        }
    }

    private Matrix computeSimilarityMatrix(Dataset<E> data, Props params) {
        LOG.debug("{} clustering: {}", getName(), params.toString());
        AbstractQueue<Element> pq = initQueue(triangleSize(data.size()), params);

        if (clusterRows(params)) {
            return AgglClustering.rowSimilarityMatrix(data.asMatrix(), distanceFunction, pq, sigma);
        } else {
            LOG.info("matrix columns: {}", data.asMatrix().columnsCount());
            return AgglClustering.columnSimilarityMatrix(data.asMatrix(), distanceFunction, pq, sigma);
        }
    }
    
//    private SymmetricMatrixDiag buildSymmetricsNormalizedRwLaplacianMatrixWorks(Matrix similarityMatrix) {
//        SymmetricMatrixDiag laplacianMatrix = new SymmetricMatrixDiag(similarityMatrix.rowsCount(), similarityMatrix.columnsCount(), 0);
//        for (int i = 0; i < similarityMatrix.rowsCount(); i++) {
//            for (int j = 0; j < similarityMatrix.columnsCount(); j++) {
//
//                int deg = 0;
//                double sumWeight = 0.0;
//                for (int columnIndex = 0; columnIndex < similarityMatrix.columnsCount(); columnIndex++) {
//                    if (columnIndex == i) {
//                        continue;
//                    }
//                    
//                    double weight = similarityMatrix.get(i, columnIndex);
//                    
//                    if(weight != 0.0){
//                        deg += 1;
//                        sumWeight += weight;
//                    }                   
//                }
//                
//                if (i == j && deg != 0) {
//                    laplacianMatrix.set(i, j, sumWeight);
//                    continue;
//                }
//
//                // mozna plus
//                double normalization = 1.0 / 1.0 + sumWeight;
//                laplacianMatrix.set(i, j, 0.0 - normalization);
//            }
//        }
//        return laplacianMatrix;
//    }
    
    private Clustering computeKmeans(Dataset<? extends Instance> eigDataset) {
        KMeans kMeans = new KMeans();
        Props kMeansParams = new Props();
        kMeansParams.putInt(KMeans.K, k);
        kMeansParams.putInt(KMeans.MAX_ITERATIONS, kmeansIterations);
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

    /**
     * Compute size of triangular matrix (n x n) minus diagonal
     *
     * @param n
     * @return
     */
    public int triangleSize(int n) {
        return ((n - 1) * n) >>> 1;
    }

    /**
     * Initialize processing queue
     *
     * @param items expected number of items in the queue
     * @param pref
     * @return
     */
    protected AbstractQueue<Element> initQueue(int items, Props pref) {
        AbstractQueue<Element> pq;
        //by default most similar items have smallest distance
        boolean smallestFirst = pref.getBoolean(AlgParams.SMALLEST_FIRST, true);
        if (smallestFirst) {
            pq = new PriorityQueue<>(items);
        } else {
            //inverse sorting - biggest values first
            Comparator<Element> comp = (Element o1, Element o2) -> o2.compareTo(o1);
            pq = new PriorityQueue<>(items, comp);
        }
        return pq;
    }

    public boolean clusterRows(Props params) {
        if (params.containsKey(CLUSTERING_TYPE)) {
            return ClusteringType.parse(params.getObject(CLUSTERING_TYPE)) != ClusteringType.COLUMNS_CLUSTERING;
        }
        return true; //by default cluster rows
    }
}
