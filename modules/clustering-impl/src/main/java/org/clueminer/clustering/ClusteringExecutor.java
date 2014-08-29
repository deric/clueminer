package org.clueminer.clustering;

import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.aggl.HAC;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.factory.CutoffStrategyFactory;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.struct.DendrogramData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;
import org.clueminer.utils.Props;

/**
 * Executor should be responsible of converting dataset into appropriate input
 * (e.g. a dense matrix) and then joining the original inputs with appropriate
 * clustering result
 *
 * @author Tomas Barton
 */
public class ClusteringExecutor {

    private AgglomerativeClustering algorithm;

    public ClusteringExecutor() {
        algorithm = new HAC();
    }

    public HierarchicalResult hclustRows(Dataset<? extends Instance> dataset, DistanceMeasure dm, Props params) {
        if (dataset == null || dataset.isEmpty()) {
            throw new NullPointerException("no data to process");
        }
        Matrix input = Scaler.standartize(dataset.arrayCopy(), params.get(AgglParams.STD, Scaler.NONE), params.getBoolean(AgglParams.LOG, false));
        params.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HierarchicalResult rowsResult = algorithm.hierarchy(input, dataset, params);
        CutoffStrategy strategy = getCutoffStrategy(params);
        rowsResult.findCutoff(strategy);
        params.putDouble(AgglParams.CUTOFF, rowsResult.getCutoff());
        return rowsResult;
    }

    public HierarchicalResult hclustColumns(Dataset<? extends Instance> dataset, DistanceMeasure dm, Props params) {
        if (dataset == null || dataset.isEmpty()) {
            throw new NullPointerException("no data to process");
        }
        Matrix input = Scaler.standartize(dataset.arrayCopy(), params.get(AgglParams.STD, Scaler.NONE), params.getBoolean(AgglParams.LOG, false));
        params.putBoolean(AgglParams.CLUSTER_ROWS, false);
        HierarchicalResult columnsResult = algorithm.hierarchy(input, dataset, params);
        //CutoffStrategy strategy = getCutoffStrategy(params);
        //columnsResult.findCutoff(strategy);
        return columnsResult;
    }

    private CutoffStrategy getCutoffStrategy(Props params) {
        CutoffStrategy strategy;
        String cutoffAlg = params.get(AgglParams.CUTOFF_STRATEGY, "-- naive --");

        if (!cutoffAlg.equals("-- naive --")) {
            String evalAlg = params.get(AgglParams.CUTOFF_SCORE, "NMI");
            ClusterEvaluator eval = InternalEvaluatorFactory.getInstance().getProvider(evalAlg);
            strategy = CutoffStrategyFactory.getInstance().getDefault();
            strategy.setEvaluator(eval);
        } else {
            strategy = CutoffStrategyFactory.getInstance().getProvider("naive cutoff");
        }
        return strategy;
    }

    public Clustering<Cluster> clusterRows(Dataset<? extends Instance> dataset, DistanceMeasure dm, Props params) {
        HierarchicalResult rowsResult = hclustRows(dataset, dm, params);
        DendrogramMapping mapping = new DendrogramData(dataset, rowsResult.getInputData(), rowsResult);

        Clustering clustering = rowsResult.getClustering();
        clustering.mergeParams(params);
        clustering.lookupAdd(mapping);
        return clustering;
    }

    /**
     * Cluster both - rows and columns
     *
     * @param dataset data to be clustered
     * @param dm      distance metric
     * @param params
     * @return
     */
    public DendrogramMapping clusterAll(Dataset<? extends Instance> dataset, DistanceMeasure dm, Props params) {
        HierarchicalResult rowsResult = hclustRows(dataset, dm, params);
        HierarchicalResult columnsResult = hclustColumns(dataset, dm, params);

        DendrogramMapping mapping = new DendrogramData(dataset, rowsResult.getInputData(), rowsResult, columnsResult);
        return mapping;
    }

    public AgglomerativeClustering getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(AgglomerativeClustering algorithm) {
        this.algorithm = algorithm;
    }

}
