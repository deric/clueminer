package org.clueminer.utils;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * An advance string map. It's an extension of
 * {@code java.util.Map<String, String>}
 * <p>
 * This map should work similar to {@code java.util.Properties} but with many
 * more advance features. We limit the storage to only key=String and
 * value=String, thus calling this a String Map, or Props. This class also
 * provides many basic types conversion getter methods for convenience.
 * <p>
 *
 */
public class Props implements Map<String, String> {

    private static final long serialVersionUID = 2211405016738281988L;
    private Table<PropType, String, String> store;

    public Props() {
        init();
    }

    public Props(Properties props) {
        init();
        fromProperties(props);
    }

    public Props(Map<String, String> map) {
        init();
        putAll(PropType.MAIN, map);
    }

    private void init() {
        store = Tables.newCustomTable(
                Maps.<PropType, Map<String, String>>newHashMap(),
                new Supplier<Map<String, String>>() {
                    @Override
                    public Map<String, String> get() {
                        return Maps.newTreeMap();
                    }
                });
    }

    @Override
    public String put(String key, String value) {
        return store.put(PropType.MAIN, key, value);
    }

    /**
     * Store property into given {pt} category
     *
     * @param pt
     * @param key
     * @param value
     * @return
     */
    public String put(PropType pt, String key, String value) {
        return store.put(pt, key, value);
    }

    public String put(PropType pt, String key, boolean value) {
        return store.put(pt, key, String.valueOf(value));
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        putAll(PropType.MAIN, map);
    }

    public final void putAll(PropType pt, Map<? extends String, ? extends String> map) {
        for (Entry<? extends String, ? extends String> e : map.entrySet()) {
            store.put(pt, e.getKey(), e.getValue());
        }
    }

    public String get(PropType pt, String key) {
        // keys are in rows
        if (!store.containsRow(key)) {
            //TODO make it work for other rows
            return store.get(pt, key);
        }
        throw new IllegalArgumentException("Map key not found: " + key);
    }

    public String get(String key) {
        return get(PropType.MAIN, key);
    }

    /**
     * This might be a get operation which modifies HashMap, however in context
     * of algorithm parameters it makes sense.
     *
     * @param key
     * @param def
     * @return
     */
    public String get(String key, String def) {
        String result = this.get(key);
        if (result == null) {
            result = def;
            /**
             * store the value, so that we know which default value was used
             */
            put(key, result);
        }
        return result;
    }

    /**
     * Fetches requested parameter from given category, if it does not exist,
     * returns default value
     *
     * @param pt
     * @param key
     * @param def
     * @return
     */
    public String get(PropType pt, String key, String def) {
        String result = this.get(key);
        if (result == null) {
            result = def;
            /**
             * store the value, so that we know which default value was used
             */
            put(pt, key, result);
        }
        return result;
    }

    public void putInt(String key, int i) {
        put(key, String.valueOf(i));
    }

    public int getInt(String key) {
        String val = get(key);
        int ret = Integer.parseInt(val);
        return ret;
    }

    public int getInt(String key, int def) {
        String val = get(key, String.valueOf(def));
        int ret = Integer.parseInt(val);
        return ret;
    }

    public boolean getBoolean(String key) {
        return getBoolean(PropType.MAIN, key);
    }

    /**
     * Retrieve key from given {pt} category
     *
     * @param pt
     * @param key
     * @return
     */
    public boolean getBoolean(PropType pt, String key) {
        String val = get(pt, key);
        return Boolean.parseBoolean(val);
    }

    public boolean getBoolean(PropType pt, String key, boolean def) {
        String val = get(pt, key, String.valueOf(def));
        return Boolean.parseBoolean(val);
    }

    public void putBoolean(String key, boolean value) {
        put(key, String.valueOf(value));
    }

    public boolean getBoolean(String key, boolean def) {
        return getBoolean(PropType.MAIN, key, def);
    }

    public long getLong(String key) {
        String val = get(key);
        long ret = Long.parseLong(val);
        return ret;
    }

    public long getLong(String key, long def) {
        String val = get(key, String.valueOf(def));
        long ret = Long.parseLong(val);
        return ret;
    }

    public void putDouble(String key, double d) {
        put(key, String.valueOf(d));
    }

    public double getDouble(String key) {
        String val = get(key);
        double ret = Double.parseDouble(val);
        return ret;
    }

    public double getDouble(String key, double def) {
        String val = get(key, String.valueOf(def));
        double ret = Double.parseDouble(val);
        return ret;
    }

    private void fromProperties(Properties props) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        Map<String, String> map = (Map) props;
        putAll(map);
    }

    /**
     * Clone this map and return it as java.util.Properties
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public Properties toProperties() {
        Properties properties = new Properties();
        properties.putAll((Map<String, String>) this.clone());
        return properties;
    }

    public Props copy() {
        Props c = new Props();
        c.putAll((Map<String, String>) this.clone());
        return c;
    }

    @Override
    public Props clone() {
        Props c = new Props();
        for (Table.Cell<PropType, String, String> e : store.cellSet()) {
            c.put(e.getRowKey(), e.getColumnKey(), e.getValue());
        }
        return c;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Entry<String, String> e : store.row(PropType.MAIN).entrySet()) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(e.getKey());
            sb.append(": ");
            sb.append(e.getValue());
            i++;
        }
        return sb.toString();
    }

    /**
     * Number of keys
     *
     * @return
     */
    @Override
    public int size() {
        return store.columnKeySet().size();
    }

    @Override
    public boolean isEmpty() {
        return store.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return store.containsColumn(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return store.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return store.get(PropType.MAIN, key);
    }

    public String get(PropType tp, Object key) {
        return store.get(tp, key);
    }

    @Override
    public String remove(Object key) {
        return store.remove(PropType.MAIN, key);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public Set<String> keySet() {
        return store.columnKeySet();
    }

    @Override
    public Collection<String> values() {
        return store.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return store.row(PropType.MAIN).entrySet();
    }

    /**
     * Merge /other/ Props into this one. Same keys will be overwritten by
     * /other/ values
     *
     * @param other
     */
    public void merge(Props other) {
        for (Table.Cell<PropType, String, String> cell : other.store.cellSet()) {
            put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
        }
    }
}
