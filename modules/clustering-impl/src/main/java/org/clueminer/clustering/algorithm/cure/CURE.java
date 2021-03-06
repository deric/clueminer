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
package org.clueminer.clustering.algorithm.cure;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Iterator;
import org.clueminer.clustering.ClusterHelper;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.Configurator;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.hclust.DynamicClusterTreeData;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CURE - CLustering Using REpresentatives
 *
 * @cite Guha, Sudipto, Rajeev Rastogi, and Kyuseok Shim. "CURE: an efficient
 * clustering algorithm for large databases." ACM SIGMOD Record. Vol. 27. No. 2.
 * ACM, 1998.
 *
 * @author deric
 * @param <E>
 * @param <C> CURE requires cluster structure with set of representatives
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class CURE<E extends Instance, C extends CureCluster<E>> extends Algorithm<E, C> implements ClusteringAlgorithm<E, C> {

    /**
     * total number of points (instances) in the data set
     */
    private int n;
    /**
     * number of expected clusters
     */
    public static final String K = "k";
    @Param(name = K, description = "expected number of clusters", required = true, min = 2, max = 25)
    private int k;

    public static final String NUM_REPRESENTATIVES = "num_representatives";
    @Param(name = NUM_REPRESENTATIVES, description = "number of representatives", required = false, min = 1, max = 1000)
    int minRepresentativeCount;

    public static final String SHRINK_FACTOR = "shrink_factor";
    @Param(name = SHRINK_FACTOR, description = "shrink factor", required = false, min = 0.0, max = 1.0)
    double shrinkFactor;

    public static final String REPRESENTATION_PROBABILITY = "representation_probablity";
    @Param(name = REPRESENTATION_PROBABILITY,
            description = "required representation probablity",
            min = 0.0, max = 1.0)
    protected double representationProbablity;

    /**
     * For performance only. When clustering large datasets it is recommended to
     * split in so many parts, that each partition will fit into memory
     */
    public static final String NUM_PARTITIONS = "num_partitions";
    @Param(name = NUM_PARTITIONS, description = "number of partitions", min = 1, max = 500)
    protected int numPartitions;

    /**
     * Minimal cluster size for not being considered as an outlier
     */
    public static final String REDUCE_FACTOR = "reduce_factor";
    @Param(name = REDUCE_FACTOR, description = "reduce factor for each partition", min = 1, max = 1000)
    protected int reduceFactor;

    /**
     * Whether allow sub-sampling or not. If true clustering is performed on
     * part of the data. Quite necessary for larger datasets.
     */
    public static final String SAMPLING = "sampling";

    public static final String NAME = "CURE";

    private static final Logger LOG = LoggerFactory.getLogger(CURE.class);

    public CURE() {

    }

    @Override
    public String getName() {
        return NAME;
    }

    private CUREConfig configure(Dataset<E> dataset, Props props) {
        CUREConfig conf = new CUREConfig();
        conf.distanceFunction = ClusterHelper.initDistance(props);
        conf.n = dataset.size();
        if (conf.n < 2) {
            throw new RuntimeException("no data to cluster, dataset of size: " + conf.n);
        }
        conf.k = props.getInt(K);
        conf.representationProbablity = props.getDouble(REPRESENTATION_PROBABILITY, 0.1);
        conf.numPartitions = props.getInt(NUM_PARTITIONS, 1);
        int sqrtN = (int) (2 * Math.sqrt(conf.n));
        if (conf.numPartitions > sqrtN) {
            LOG.warn("overriding num_partitions to {} (was too large: {})", sqrtN, conf.numPartitions);
            conf.numPartitions = sqrtN;
            props.putInt(NUM_PARTITIONS, conf.numPartitions);
        }
        reduceFactor = props.getInt(REDUCE_FACTOR, 3);

        conf.currentRepAdditionCount = conf.n;
        conf.blacklist = new IntOpenHashSet(dataset.size());
        conf.outliers = new CureCluster<>(dataset);
        conf.clusterCnt = 0;
        conf.colorGenerator = colorGenerator;
        if (conf.colorGenerator != null) {
            conf.colorGenerator.reset();
        }

        return conf;
    }

    public Clustering<E, C> cluster(Dataset<E> dataset, Props props, CUREConfig conf) {
        //final clustering to be returned
        Clustering<E, C> clustering = new ClusterList<>(k);
        clustering.lookupAdd(dataset);
        //use part of dataset to create initial clustering
        if (props.getBoolean(SAMPLING, true)) {
            sampleData(dataset, clustering, conf, props);
            labelRemainingDataPoints(dataset, clustering, conf);
        } else {
            clusterPartition(dataset, clustering, conf, props);
        }

        LOG.info("left {} outliers", conf.outliers.size());

        if (!conf.outliers.isEmpty()) {
            conf.outliers.setName(Algorithm.OUTLIER_LABEL);
            conf.outliers.setClusterId(clustering.size());
            if (conf.colorGenerator != null) {
                conf.outliers.setColor(colorGenerator.next());
            }
            clustering.add((C) conf.outliers);
        }
        clustering.setParams(props);
        return clustering;
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> dataset, Props props) {
        CUREConfig conf = configure(dataset, props);
        return cluster(dataset, props, conf);
    }

    private void sampleData(Dataset<E> dataset, Clustering<E, C> clustering, CUREConfig conf, Props props) {
        int sampleSize = calculateSampleSize(conf);
        LOG.info("using sample size {}", sampleSize);
        conf.random = ClusterHelper.initSeed(props);
        Dataset<E> randomPointSet = selectRandomPoints(dataset, sampleSize, conf);
        Dataset<E> partition;

        Iterator<E> iter = randomPointSet.iterator();
        for (int i = 0; i < conf.numPartitions; i++) {
            partition = new ArrayDataset<>(randomPointSet.size() / conf.numPartitions, dataset.attributeCount());
            partition.setAttributes(dataset.getAttributes());
            int pointIndex = 0;
            while (pointIndex < randomPointSet.size() / conf.numPartitions) {
                partition.add(iter.next());
                pointIndex++;
            }
            LOG.info("partition {} size = {}", i, partition.size());
            clusterPartition(partition, clustering, conf, props);
        }

        if (iter.hasNext()) {
            partition = new ArrayDataset<>(randomPointSet.size() / conf.numPartitions, dataset.attributeCount());
            partition.setAttributes(dataset.getAttributes());
            while (iter.hasNext()) {
                partition.add(iter.next());
            }
            if (!partition.isEmpty()) {
                clusterPartition(partition, clustering, conf, props);
            }
        }
    }

    public HierarchicalResult hierarchy(Dataset<E> dataset, Props pref) {
        HierarchicalResult result = new HClustResult(dataset, pref);
        CUREConfig conf = configure(dataset, pref);
        Clustering<E, C> clustering = cluster(dataset, pref, conf);

        DendroTreeData treeData = new DynamicClusterTreeData(conf.nodes[2 * k - 2]);
        treeData.createMapping(dataset.size(), treeData.getRoot(), conf.nodes[2 * k - 1]);
        result.setTreeData(treeData);
        result.setClustering(clustering);
        return result;
    }

    public boolean isLinkageSupported(String linkage) {
        return false;
    }

    private void clusterPartition(Dataset<E> partition, Clustering<E, C> clustering, CUREConfig conf, Props props) {
        //int numPartition = n / (numberOfPartitions * reducingFactor * k);
        //logger.log(Level.INFO, "clustering partititon, exp: {0}", numPartition);
        ClusterSet<E, C> clusterSet = new ClusterSet(partition, props, conf);

        eliminateOutliers(clusterSet, clustering, conf);
    }

    /**
     * Calculates the Sample Size based on Chernoff Bounds Mentioned in the CURE
     * Algorithm
     *
     * @return int The Sample Data Size to be worked on
     */
    private int calculateSampleSize(CUREConfig conf) {
        return (int) ((0.5 * conf.n)
                + (conf.k * Math.log10(1 / conf.representationProbablity))
                + (conf.k * Math.sqrt(Math.pow(Math.log10(1 / conf.representationProbablity), 2)
                        + (conf.n / conf.k) * Math.log10(1 / conf.representationProbablity))));
    }

    /**
     * Select random points from the data set
     *
     * @param sampleSize The sample size selected
     * @return ArrayList The Selected Random Points
     */
    private Dataset<E> selectRandomPoints(Dataset<E> dataset, int sampleSize, CUREConfig conf) {
        if (dataset.size() == sampleSize) {
            return dataset;
        }
        Dataset<E> randomPointSet = new ArrayDataset<>(sampleSize, dataset.attributeCount());
        randomPointSet.setAttributes(dataset.getAttributes());
        for (int i = 0; i < sampleSize; i++) {
            int index = conf.random.nextInt(conf.n);
            if (conf.blacklist.contains(index)) {
                i--;
            } else {
                randomPointSet.add(dataset.get(index));
                conf.blacklist.add(index);
            }
        }
        return randomPointSet;
    }

    /**
     * Eliminates outliers after pre-clustering
     *
     * @param clusters Clusters present cluster
     */
    private void eliminateOutliers(ClusterSet<E, C> clusterSet, Clustering<E, C> clustering, CUREConfig conf) {
        LOG.info("cluster set with {} clusters", clusterSet.size());
        C cluster;
        while (clusterSet.hasClusters()) {
            cluster = clusterSet.remove();
            if (cluster.size() >= conf.reduceFactor) {
                cluster.setClusterId(conf.clusterCnt++);
                cluster.setName("cluster " + conf.clusterCnt);
                if (conf.colorGenerator != null) {
                    cluster.setColor(conf.colorGenerator.next());
                }
                clustering.add(cluster);
            } else {
                conf.outliers.addAll(cluster);
            }
        }
    }

    /**
     * Assign all remaining data points which were not part of the sampled data
     * set to set of clusters formed
     *
     * @param clusters Set of clusters
     * @return ArrayList Modified clusters
     */
    private Clustering<E, C> labelRemainingDataPoints(Dataset<E> dataset, Clustering<E, C> clusters, CUREConfig conf) {
        for (E inst : dataset) {
            if (conf.blacklist.contains(inst.getIndex())) {
                continue;
            }
            double smallestDistance = Double.POSITIVE_INFINITY;
            int nearestClusterIndex = -1;
            double distance;
            for (int i = 0; i < clusters.size(); i++) {
                for (E other : clusters.get(i).rep) {
                    distance = conf.distanceFunction.measure(inst, other);
                    if (distance < smallestDistance) {
                        smallestDistance = distance;
                        nearestClusterIndex = i;
                    }
                }
            }
            if (nearestClusterIndex != -1) {
                clusters.get(nearestClusterIndex).add(inst);
            }
        }
        return clusters;
    }

    @Override
    public Configurator<E> getConfigurator() {
        return CUREConfig.getInstance();
    }

    /**
     * @TODO: depends on sub-sampling
     * @return
     */
    @Override
    public boolean isDeterministic() {
        return true;
    }

}
