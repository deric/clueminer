package org.clueminer.chart.factory;

import java.util.Collection;
import java.util.LinkedHashMap;
import org.clueminer.chart.api.Annotation;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class AnnotationFactory extends ServiceFactory<Annotation> {

    private static AnnotationFactory instance;

    public static AnnotationFactory getInstance() {
        if (instance == null) {
            instance = new AnnotationFactory();
        }
        return instance;
    }

    private AnnotationFactory() {
        providers = new LinkedHashMap<String, Annotation>();
        Collection<? extends Annotation> list = Lookup.getDefault().lookupAll(Annotation.class);
        for (Annotation a : list) {
            providers.put(a.getName(), a);
        }
        sort();
    }

}
