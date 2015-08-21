package org.clueminer.clustering.api.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class InternalEvaluatorFactory<E extends Instance, C extends Cluster<E>> extends ServiceFactory<InternalEvaluator<E, C>> {

    private static InternalEvaluatorFactory instance;

    public static InternalEvaluatorFactory getInstance() {
        if (instance == null) {
            instance = new InternalEvaluatorFactory();
        }
        return instance;
    }

    private InternalEvaluatorFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends InternalEvaluator<E, C>> list = (Collection<? extends InternalEvaluator<E, C>>) Lookup.getDefault().lookupAll(InternalEvaluator.class);
        for (InternalEvaluator<E, C> c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
