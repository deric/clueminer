package org.clueminer.clustering.api.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class InternalEvaluatorFactory extends ServiceFactory<InternalEvaluator> {

    private static InternalEvaluatorFactory instance;

    public static InternalEvaluatorFactory getInstance() {
        if (instance == null) {
            instance = new InternalEvaluatorFactory();
        }
        return instance;
    }

    private InternalEvaluatorFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends InternalEvaluator> list = Lookup.getDefault().lookupAll(InternalEvaluator.class);
        for (InternalEvaluator c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
