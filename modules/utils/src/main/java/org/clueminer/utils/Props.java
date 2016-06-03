/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.utils;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.openide.util.Exceptions;

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
public class Props implements Map<String, Object> {

    private static final long serialVersionUID = 2211405016738281988L;
    private Table<PropType, String, Object> store;

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
                Maps.<PropType, Map<String, Object>>newHashMap(),
                new Supplier<Map<String, Object>>() {
            @Override
            public Map<String, Object> get() {
                return Maps.newTreeMap();
            }
        });
    }

    @Override
    public Object put(String key, Object value) {
        return store.put(PropType.MAIN, key, value);
    }

    public Object put(String key, double value) {
        return store.put(PropType.MAIN, key, value);
    }

    public Object put(String key, long value) {
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
        return (String) store.put(pt, key, value);
    }

    public Object put(PropType pt, String key, Object value) {
        return store.put(pt, key, value);
    }

    public Object put(PropType pt, String key, double value) {
        return store.put(pt, key, value);
    }

    public Object put(PropType pt, String key, boolean value) {
        return store.put(pt, key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> map) {
        putAll(PropType.MAIN, map);
    }

    public final void putAll(PropType pt, Map<? extends String, ? extends Object> map) {
        for (Entry<? extends String, ? extends Object> e : map.entrySet()) {
            store.put(pt, e.getKey(), e.getValue());
        }
    }

    public Object get(PropType pt, String key) {
        // keys are in rows
        if (!store.containsRow(key)) {
            //TODO make it work for other rows
            return store.get(pt, key);
        }
        throw new IllegalArgumentException("Map key not found: " + key);
    }

    /**
     * By default return String, we can't override methods with different return
     * types,
     * thus for getting boolean use getBoolean() etc.
     *
     * @param key
     * @return String value for given key if present, otherwise null
     */
    public String get(String key) {
        Object ret = get(PropType.MAIN, key);
        if (ret == null) {
            return null;
        }
        return ret.toString();
    }

    /**
     * Method used internally to retrieve various types
     *
     * @param key
     * @return
     */
    public Object getObject(Object key) {
        return store.get(PropType.MAIN, key);
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
        Object result = this.getObject(key);
        if (result == null) {
            result = def;
            /**
             * store the value, so that we know which default value was used
             */
            put(key, result);
        }
        return result.toString();
    }

    public Object getObject(String key, Object def) {
        Object result = store.get(PropType.MAIN, key);
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
        Object result = this.get(pt, key);
        if (result == null) {
            result = def;
            /**
             * store the value, so that we know which default value was used
             */
            put(pt, key, result);
        }
        return (String) result;
    }

    public Object get(PropType pt, String key, Object def) {
        Object result = this.get(pt, key);
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
        Object val = get(key);
        if (val instanceof Integer) {
            return (int) val;
        }
        int ret = Integer.parseInt((String) val);
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
        Object val = get(pt, key);
        if (val instanceof Boolean) {
            return (boolean) val;
        }
        if (val == null) {
            return false;
        }
        return Boolean.valueOf(val.toString());
    }

    public boolean getBoolean(PropType pt, String key, boolean def) {
        Object val = get(pt, key, def);
        if (val instanceof Boolean) {
            return (boolean) val;
        }
        return Boolean.valueOf(val.toString());
    }

    public double getDouble(PropType pt, String key, double def) {
        Object val = get(pt, key, def);
        if (val instanceof Double) {
            return (double) val;
        }
        return Double.valueOf(val.toString());
    }

    public void putBoolean(String key, boolean value) {
        put(key, value);
    }

    public boolean getBoolean(String key, boolean def) {
        return getBoolean(PropType.MAIN, key, def);
    }

    public long getLong(String key) {
        Object val = get(key);
        if (val instanceof Long) {
            return (long) val;
        }
        return Long.parseLong(val.toString());
    }

    public long getLong(String key, long def) {
        String val = get(key, String.valueOf(def));
        long ret = Long.parseLong(val);
        return ret;
    }

    public void putDouble(String key, double d) {
        put(key, d);
    }

    public void putLong(String key, long d) {
        put(key, d);
    }

    public double getDouble(String key) {
        Object val = get(key);
        double ret = Double.parseDouble(val.toString());
        return ret;
    }

    public double getDouble(String key, double def) {
        Object val = getObject(key, def);
        if (val == null) {
            return def;
        }
        if (val instanceof Double) {
            return (double) val;
        }
        return Double.parseDouble(val.toString());
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
        properties.putAll((Map<String, Object>) this.clone());
        return properties;
    }

    /**
     * Deep copy of the props map
     *
     * @return copy all values from original Props object
     */
    public Props copy() {
        Props c = new Props();
        for (Table.Cell<PropType, String, Object> e : store.cellSet()) {
            c.put(e.getRowKey(), e.getColumnKey(), e.getValue());
        }
        return c;
    }

    @Override
    public Props clone() {
        Props c = new Props();
        for (Table.Cell<PropType, String, Object> e : store.cellSet()) {
            c.put(e.getRowKey(), e.getColumnKey(), e.getValue());
        }
        return c;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Entry<String, Object> e : store.row(PropType.MAIN).entrySet()) {
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
        return store.get(PropType.MAIN, key).toString();
    }

    public String get(PropType tp, Object key) {
        Object ret = store.get(tp, key);
        if (ret == null) {
            return "";
        }
        return ret.toString();
    }

    @Override
    public String remove(Object key) {
        return store.remove(PropType.MAIN, key).toString();
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
    public Collection<Object> values() {
        return store.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return store.row(PropType.MAIN).entrySet();
    }

    /**
     * Merge /other/ Props into this one. Same keys will be overwritten by
     * /other/ values
     *
     * @param other
     */
    public void merge(Props other) {
        for (Table.Cell<PropType, String, Object> cell : other.store.cellSet()) {
            put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
        }
    }

    public String toJson() {
        StringWriter w = new StringWriter();
        try {
            try (JsonWriter writer = new JsonWriter(w)) {
                writer.beginObject();
                for (Entry<String, Object> entry : entrySet()) {
                    Object val = entry.getValue();
                    writer.name(entry.getKey());
                    if (val instanceof Double) {
                        double d = (Double) val;
                        writer.value(d);
                    } else if (val instanceof Integer) {
                        int i = (Integer) val;
                        writer.value(i);
                    } else if (val instanceof Long) {
                        long lg = (Long) val;
                        writer.value(lg);
                    } else if (val instanceof String) {
                        String str = (String) val;
                        writer.value(str);
                    } else {
                        String str = val.toString();
                        writer.value(str);
                    }
                }
                writer.endObject();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return w.toString();
    }

    public static Props fromJson(String json) {
        Props p = new Props();

        JsonElement jelement = new JsonParser().parse(json);

        JsonObject jobject = jelement.getAsJsonObject();
        for (Entry<String, JsonElement> e : jobject.entrySet()) {
            if (e.getValue().isJsonPrimitive()) {
                p.put(e.getKey(), e.getValue().getAsString());
            } else {
                throw new RuntimeException("nested structures not supported yet");
            }
        }

        return p;
    }
}
