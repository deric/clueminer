package org.clueminer.eval.utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.ClusterEvaluatorFactory;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class HashEvaluationTable implements EvaluationTable {

    private Clustering<Cluster> clustering;
    private Dataset<? extends Instance> dataset;
    protected static Object2ObjectMap<String, ClusterEvaluator> evaluatorMap;

    public HashEvaluationTable(Clustering<Cluster> clustering, Dataset<? extends Instance> dataset) {
        setData(clustering, dataset);
        initEvaluators();
    }

    @Override
    public final void setData(Clustering<Cluster> clustering, Dataset<? extends Instance> dataset) {
        this.clustering = clustering;
        this.dataset = dataset;
    }

    /**
     * @TODO implement this
     * @param evaluator
     * @return
     */
    @Override
    public double getScore(ClusterEvaluation evaluator) {
        return Double.NaN;
    }

    @Override
    public String[] getEvaluators() {
        if (evaluatorMap == null) {
            return new String[0];
        }
        return evaluatorMap.keySet().toArray(new String[evaluatorMap.size()]);
    }

    private static void initEvaluators() {
        if (evaluatorMap == null) {
            ClusterEvaluatorFactory cef = ClusterEvaluatorFactory.getInstance();
            evaluatorMap = new Object2ObjectOpenHashMap<String, ClusterEvaluator>();
            for (ClusterEvaluator eval : cef.getAll()) {
                evaluatorMap.put(eval.getName(), eval);
            }
        }
    }

}
