package org.clueminer.clustering;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.aggl.HCLW;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.OptimalTreeOrder;
import org.clueminer.clustering.order.MOLO;
import org.clueminer.clustering.struct.DendrogramData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.std.Scaler;
import org.clueminer.utils.Props;

/**
 * Executor should be responsible of converting dataset into appropriate input
 * (e.g. a dense matrix) and then joining the original inputs with appropriate
 * clustering result
 *
 * Should reduce circa 50% of memory during evolution (trying all combinations
 * of standardizations)
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class ClusteringExecutorCached<E extends Instance, C extends Cluster<E>> extends AbstractExecutor<E, C> implements Executor<E, C> {

    private static final Logger logger = Logger.getLogger(ClusteringExecutorCached.class.getName());
    private Map<Dataset<E>, StdStorage<E>> storage;
    private OptimalTreeOrder treeOrder = new MOLO();

    public ClusteringExecutorCached() {
        algorithm = new HCLW<>();
    }

    public ClusteringExecutorCached(ClusteringAlgorithm alg) {
        algorithm = alg;
    }

    @Override
    public HierarchicalResult hclustRows(Dataset<E> dataset, Props params) {
        StdStorage store = getStorage(dataset);
        logger.log(Level.FINER, "normalizing data {0}, logscale: {1}", new Object[]{params.get(AgglParams.STD, Scaler.NONE), params.getBoolean(AgglParams.LOG, false)});
        Dataset<E> norm = store.get(params.get(AgglParams.STD, Scaler.NONE), params.getBoolean(AgglParams.LOG, false));
        params.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        logger.log(Level.FINER, "clustering {0}", params.toString());
        AgglomerativeClustering aggl = (AgglomerativeClustering) algorithm;
        HierarchicalResult rowsResult = aggl.hierarchy(norm, params);
        //TODO: tree ordering might break assigning items to clusters
        //treeOrder.optimize(rowsResult, true);
        return rowsResult;
    }

    @Override
    public HierarchicalResult hclustColumns(Dataset<E> dataset, Props params) {
        StdStorage store = getStorage(dataset);
        Dataset<? extends Instance> norm = store.get(params.get(AgglParams.STD, Scaler.NONE), params.getBoolean(AgglParams.LOG, false));
        params.put(AgglParams.CLUSTERING_TYPE, ClusteringType.COLUMNS_CLUSTERING);
        AgglomerativeClustering aggl = (AgglomerativeClustering) algorithm;
        HierarchicalResult columnsResult = aggl.hierarchy(norm, params);
        //treeOrder.optimize(columnsResult, true);
        //CutoffStrategy strategy = getCutoffStrategy(params);
        //columnsResult.findCutoff(strategy);
        return columnsResult;
    }

    private void checkInput(Dataset<? extends Instance> dataset) {
        if (dataset == null || dataset.isEmpty()) {
            throw new NullPointerException("no data to process");
        }
    }

    private StdStorage getStorage(Dataset<E> dataset) {
        checkInput(dataset);
        StdStorage stdStore;

        if (storage == null) {
            storage = new HashMap<>(2);
        }
        if (storage.containsKey(dataset)) {
            stdStore = storage.get(dataset);
        } else {
            stdStore = new StdStorage(dataset);
            storage.put(dataset, stdStore);
        }
        return stdStore;
    }

    @Override
    public Clustering<E, C> clusterRows(Dataset<E> dataset, Props params) {
        Clustering clustering;
        if (algorithm instanceof AgglomerativeClustering) {
            HierarchicalResult rowsResult = hclustRows(dataset, params);

            findCutoff(rowsResult, params);
            DendrogramMapping mapping = new DendrogramData(dataset, rowsResult);

            clustering = rowsResult.getClustering();
            clustering.mergeParams(params);
            clustering.lookupAdd(mapping);
        } else {
            //non-hierarchical method
            clustering = algorithm.cluster(dataset, params);
        }
        return clustering;
    }

    public void findCutoff(HierarchicalResult result, Props params) {
        CutoffStrategy strategy = getCutoffStrategy(params);
        logger.log(Level.FINER, "cutting dendrogram with {0}", strategy.getName());
        double cut = result.findCutoff(strategy);
        logger.log(Level.FINER, "found cutoff {0}, resulting clusters {1}", new Object[]{cut, result.getClustering().size()});
    }

    /**
     * Cluster both - rows and columns
     *
     * @param dataset data to be clustered
     * @param params
     * @return
     */
    @Override
    public DendrogramMapping clusterAll(Dataset<E> dataset, Props params) {
        HierarchicalResult rowsResult = hclustRows(dataset, params);
        findCutoff(rowsResult, params);
        HierarchicalResult columnsResult = hclustColumns(dataset, params);
        DendrogramMapping mapping = new DendrogramData(dataset, rowsResult, columnsResult);
        Clustering clustering = rowsResult.getClustering();
        clustering.lookupAdd(mapping);
        clustering.lookupAdd(rowsResult);
        clustering.lookupAdd(columnsResult);
        clustering.mergeParams(params);
        return mapping;
    }

}
