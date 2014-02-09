package org.clueminer.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import org.clueminer.chart.Annotation;
import org.clueminer.chart.api.ChartConfig;
import org.openide.util.Lookup;

public class AnnotationFactory {

    private static AnnotationFactory instance;
    private LinkedHashMap<String, Annotation> annotations;
    private Annotation current;

    public static AnnotationFactory getDefault() {
        if (instance == null) {
            instance = new AnnotationFactory();
        }
        return instance;
    }

    private AnnotationFactory() {
        annotations = new LinkedHashMap<String, Annotation>();
        Collection<? extends Annotation> list = Lookup.getDefault().lookupAll(Annotation.class);
        for (Annotation a : list) {
            annotations.put(a.getName(), a);
        }

        annotations = sort(annotations);
    }

    private LinkedHashMap<String, Annotation> sort(LinkedHashMap<String, Annotation> oldMap) {
        List<String> mapKeys = new ArrayList<String>(oldMap.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, Annotation> newMap = new LinkedHashMap<String, Annotation>();
        for (int i = 0; i < mapKeys.size(); i++) {
            newMap.put(mapKeys.get(i), oldMap.get(mapKeys.get(i)));
        }

        return newMap;
    }

    public Annotation getAnnotation(String key) {
        return annotations.get(key);
    }

    public List<String> getAnnotations() {
        List<String> list = new ArrayList<String>(annotations.keySet());
        Collections.sort(list);
        return list;
    }

    public void setNewAnnotation(Annotation a) {
        current = a;
    }

    public boolean hasNew() {
        return current != null;
    }

    public Annotation getNewAnnotation(ChartConfig frame) {
        return current.newInstance(frame);
    }

    public void clearNewAnnotation() {
        current = null;
    }
}
