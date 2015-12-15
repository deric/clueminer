package org.clueminer.clustering.aggl;

import java.util.AbstractQueue;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class HacLwMsPar2<E extends Instance, C extends Cluster<E>> extends HCLWMS<E, C> {

    private final static String name = "HAC-LW-MS-PAR-lock";
    private int threads = 4;

    public HacLwMsPar2() {

    }

    public HacLwMsPar2(int numThreads) {
        this.threads = numThreads;
    }

    @Override
    public String getName() {
        return name + "-" + threads;
    }

    /**
     * Computes hierarchical clustering with specified linkage and stores
     * dendrogram tree structure. However final clustering is not computed yet,
     * it will be formed later based on cut-off function.
     *
     * @param dataset
     * @param pref
     * @return
     */
    @Override
    public HierarchicalResult hierarchy(Dataset<E> dataset, Props pref) {
        int n;
        HierarchicalResult result = new HClustResult(dataset, pref);
        AgglParams params = new AgglParams(pref);
        Matrix similarityMatrix;
        distanceFunction = params.getDistanceMeasure();
        if (params.clusterRows()) {
            n = dataset.size();
        } else {
            //columns clustering
            n = dataset.attributeCount();
        }

        int items = triangleSize(n);
        //TODO: we might track clustering by estimated time (instead of counters)
        AbstractQueue<Element> pq = initQueue(items, pref);

        Matrix input = dataset.asMatrix();
        if (params.clusterRows()) {
            if (distanceFunction.isSymmetric()) {
                similarityMatrix = AgglClustering.rowSimilarityMatrixParSymLock(input, distanceFunction, pq, threads);
            } else {
                similarityMatrix = AgglClustering.rowSimilarityMatrix(input, distanceFunction, pq);
            }
        } else {
            similarityMatrix = AgglClustering.columnSimilarityMatrix(input, distanceFunction, pq);
        }
        //whether to keep reference to proximity matrix (could be memory exhausting)
        if (pref.getBoolean(PropType.PERFORMANCE, AgglParams.KEEP_PROXIMITY, true)) {
            result.setProximityMatrix(similarityMatrix);
        }

        DendroTreeData treeData = computeLinkage(pq, similarityMatrix, dataset, params, n);
        treeData.createMapping(n, treeData.getRoot());
        result.setTreeData(treeData);
        return result;
    }

}
