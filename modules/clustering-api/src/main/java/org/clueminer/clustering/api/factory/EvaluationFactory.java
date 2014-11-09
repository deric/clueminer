package org.clueminer.clustering.api.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

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
        Collection<? extends ClusterEvaluation> list = Lookup.getDefault().lookupAll(ClusterEvaluation.class);
        for (ClusterEvaluation c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
