package org.clueminer.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Generic factory for service providers. Classes extending ServiceFactory must
 * implement "public static <? extends ServiceFactory> getInstance()" method.
 * In Java we can't have abstract static methods, therefore this is just plain
 * text contract.
 *
 * @author Tomas Barton
 * @param <T> class to be looked up
 */
public class ServiceFactory<T> {

    protected LinkedHashMap<String, T> providers;
    protected T defaultProvider;

    protected void sort() {
        List<String> mapKeys = new ArrayList<>(providers.keySet());

        LinkedHashMap<String, T> someMap = new LinkedHashMap<>();
        for (String mapKey : mapKeys) {
            someMap.put(mapKey, providers.get(mapKey));
        }
        providers = someMap;
    }

    public T getProvider(String key) {
        if (!hasProvider(key)) {
            throw new RuntimeException("provider " + key + " was not found");
        }
        return providers.get(key);
    }

    public List<String> getProviders() {
        List<String> list = new ArrayList<>(providers.keySet());
        return list;
    }

    public String[] getProvidersArray() {
        List<String> list = getProviders();
        return list.toArray(new String[list.size()]);
    }

    public List<String> removeProvider(String current) {
        List<String> list = new ArrayList<>(providers.keySet());
        list.remove(current);
        return list;
    }

    /**
     * Return List of instances of all providers
     *
     * @return
     */
    public List<T> getAll() {
        List<T> list = new ArrayList<>(providers.values());
        return list;
    }

    /**
     * Default provider for given service, if no default specified, return first
     * provider in list (alphabetically sorted)
     *
     * @return
     */
    public T getDefault() {
        if (defaultProvider == null) {
            List<T> all = getAll();
            if (!all.isEmpty()) {
                return all.get(0);
            }
        }
        return defaultProvider;
    }

    public void setDefault(T provider) {
        this.defaultProvider = provider;
    }

    public boolean hasDefault() {
        return (defaultProvider != null);
    }

    public boolean hasProvider(String key) {
        return providers.containsKey(key);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(this.getClass().getName() + " [ \n");
        str.append(providers.toString());
        str.append("\n ]");
        return str.toString();
    }
}
