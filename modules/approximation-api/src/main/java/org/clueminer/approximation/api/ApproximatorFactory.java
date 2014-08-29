package org.clueminer.approximation.api;

import java.util.*;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class ApproximatorFactory extends ServiceFactory<Approximator> {

    private static ApproximatorFactory instance;
    private static List<String> paramNames = null;

    public static ApproximatorFactory getInstance() {
        if (instance == null) {
            instance = new ApproximatorFactory();
        }
        return instance;
    }

    private ApproximatorFactory() {
        providers = new LinkedHashMap<String, Approximator>();
        Collection<? extends Approximator> list = Lookup.getDefault().lookupAll(Approximator.class);
        for (Approximator c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

    public List<String> getParamNames() {
        if (paramNames == null) {
            paramNames = new ArrayList<String>();
            List<Approximator> appr = getAll();
            for (Approximator a : appr) {
                paramNames.addAll(Arrays.asList(a.getParamNames()));
            }
        }
        return paramNames;
    }
}
