package org.clueminer.approximation.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class DataTransformFactory extends ServiceFactory<DataTransform> {

    private static DataTransformFactory instance;
    private static List<String> paramNames = null;

    public static DataTransformFactory getDefault() {
        if (instance == null) {
            instance = new DataTransformFactory();
        }
        return instance;
    }

    private DataTransformFactory() {
        providers = new LinkedHashMap<String, DataTransform>();
        Collection<? extends DataTransform> list = Lookup.getDefault().lookupAll(DataTransform.class);
        for (DataTransform c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }
}
