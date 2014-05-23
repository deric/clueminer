package org.clueminer.clustering.api.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class ExternalEvaluatorFactory extends ServiceFactory<ExternalEvaluator> {

    private static ExternalEvaluatorFactory instance;

    public static ExternalEvaluatorFactory getInstance() {
        if (instance == null) {
            instance = new ExternalEvaluatorFactory();
        }
        return instance;
    }

    private ExternalEvaluatorFactory() {
        providers = new LinkedHashMap<String, ExternalEvaluator>();
        Collection<? extends ExternalEvaluator> list = Lookup.getDefault().lookupAll(ExternalEvaluator.class);
        for (ExternalEvaluator c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
