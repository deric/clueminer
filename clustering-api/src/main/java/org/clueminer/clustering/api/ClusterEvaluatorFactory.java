package org.clueminer.clustering.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;


/**
 *
 * @author Tomas Barton
 */
public class ClusterEvaluatorFactory extends ServiceFactory<ClusterEvaluator> { 
    
    private static ClusterEvaluatorFactory instance;

    public static ClusterEvaluatorFactory getInstance() {
        if (instance == null) {
            instance = new ClusterEvaluatorFactory();
        }
        return instance;
    }

    private ClusterEvaluatorFactory() {
        providers = new LinkedHashMap<String, ClusterEvaluator>();
        Collection<? extends ClusterEvaluator> list = Lookup.getDefault().lookupAll(ClusterEvaluator.class);
        for (ClusterEvaluator c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
