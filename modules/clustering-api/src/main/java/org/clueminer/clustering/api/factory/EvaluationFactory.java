package org.clueminer.clustering.api.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.utils.ServiceFactory;

/**
 * Mixes internal and external evaluator
 *
 * @author Tomas Barton
 */
public class EvaluationFactory extends ServiceFactory<ClusterEvaluation> {

    private static EvaluationFactory instance;

    public static EvaluationFactory getInstance() {
        if (instance == null) {
            instance = new EvaluationFactory();
        }
        return instance;
    }

    private EvaluationFactory() {
        providers = new LinkedHashMap<>();
        List<ExternalEvaluator> extern = ExternalEvaluatorFactory.getInstance().getAll();
        List<InternalEvaluator> internal = InternalEvaluatorFactory.getInstance().getAll();

        Collection<ClusterEvaluation> list = new LinkedList<>();
        list.addAll(extern);
        list.addAll(internal);

        for (ClusterEvaluation c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
