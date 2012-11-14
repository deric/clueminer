package org.clueminer.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Tomas Barton
 */
public class ServiceFactory<T> {

    protected LinkedHashMap<String, T> providers;

    protected void sort() {
        List<String> mapKeys = new ArrayList<String>(providers.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, T> someMap = new LinkedHashMap<String, T>();
        for (int i = 0; i < mapKeys.size(); i++) {
            someMap.put(mapKeys.get(i), providers.get(mapKeys.get(i)));
        }
        providers = someMap;
    }

    public T getProvider(String key) {
        if(!providers.containsKey(key)){
            throw new RuntimeException("provider "+key+" was not found");
        }
        return providers.get(key);
    }

    public List<String> getProviders() {
        List<String> list = new ArrayList<String>(providers.keySet());
        Collections.sort(list);
        return list;
    }

    public List<String> removeProvider(String current) {
        List<String> list = new ArrayList<String>(providers.keySet());
        list.remove(current);
        Collections.sort(list);
        return list;
    }
    
    /**
     * Return List of instances of all providers
     * @return 
     */
    public List<T> getAll() {
        List<T> list = new ArrayList<T>(providers.values());
        return list;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(this.getClass().getName() + " [ \n");
        str.append(providers.toString());
        str.append("\n ]");
        return str.toString();
    }
}
