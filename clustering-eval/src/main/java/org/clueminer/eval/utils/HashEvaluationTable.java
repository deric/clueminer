package org.clueminer.eval.utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.TreeSet;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.clustering.api.factory.ExternalEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class HashEvaluationTable implements EvaluationTable {

    private Clustering<Cluster> clustering;
    private Dataset<? extends Instance> dataset;
    protected static Object2ObjectMap<String, ClusterEvaluation> internalMap;
    protected static Object2ObjectMap<String, ClusterEvaluation> externalMap;

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
        if (internalMap == null) {
            return new String[0];
        }
        Collection<String> evaluators = new TreeSet<String>();
        evaluators.addAll(internalMap.keySet());
        evaluators.addAll(externalMap.keySet());
        return evaluators.toArray(new String[internalMap.size()]);
    }

    private static void initEvaluators() {
        if (internalMap == null) {
            InternalEvaluatorFactory inf = InternalEvaluatorFactory.getInstance();
            internalMap = new Object2ObjectOpenHashMap<String, ClusterEvaluation>();

            for (ClusterEvaluator eval : inf.getAll()) {
                internalMap.put(eval.getName(), eval);
            }
        }
        if (externalMap == null) {
            externalMap = new Object2ObjectOpenHashMap<String, ClusterEvaluation>();
            ExternalEvaluatorFactory extf = ExternalEvaluatorFactory.getInstance();
            for (ExternalEvaluator eval : extf.getAll()) {
                internalMap.put(eval.getName(), eval);
            }
        }
    }

}
