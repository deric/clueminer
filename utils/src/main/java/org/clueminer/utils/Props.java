package org.clueminer.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
public class Props extends HashMap<String, String> {

    private static final long serialVersionUID = 2211405016738281987L;

    public Props() {
    }

    public Props(Properties props) {
        fromProperties(props);
    }

    public Props(Map<String, String> map) {
        putAll(map);
    }

    public List<String> getGroupKeys(String groupKey) {
        List<String> result = new ArrayList<String>();
        for (String key : keySet()) {
            if (key.startsWith(groupKey)) {
                result.add(key);
            }
        }
        return result;
    }

    public String getString(String key) {
        if (!containsKey(key)) {
            throw new IllegalArgumentException("Map key not found: " + key);
        }
        String result = this.get(key);
        return result;
    }

    public String get(String key, String def) {
        String result = this.get(key);
        if (result == null) {
            result = def;
        }
        return result;
    }

    public void putInt(String key, int i) {
        put(key, String.valueOf(i));
    }

    public int getInt(String key) {
        String val = getString(key);
        int ret = Integer.parseInt(val);
        return ret;
    }

    public int getInt(String key, int def) {
        String val = get(key, "" + def);
        int ret = Integer.parseInt(val);
        return ret;
    }

    public boolean getBoolean(String key) {
        String val = getString(key);
        return Boolean.parseBoolean(val);
    }

    public void putBoolean(String key, boolean value) {
        put(key, String.valueOf(value));
    }

    public boolean getBoolean(String key, boolean def) {
        String val = get(key, "" + def);
        return Boolean.parseBoolean(val);
    }

    public long getLong(String key) {
        String val = getString(key);
        long ret = Long.parseLong(val);
        return ret;
    }

    public long getLong(String key, long def) {
        String val = get(key, "" + def);
        long ret = Long.parseLong(val);
        return ret;
    }

    public void putDouble(String key, double d) {
        put(key, String.valueOf(d));
    }

    public double getDouble(String key) {
        String val = getString(key);
        double ret = Double.parseDouble(val);
        return ret;
    }

    public double getDouble(String key, double def) {
        String val = get(key, "" + def);
        double ret = Double.parseDouble(val);
        return ret;
    }

    private void fromProperties(Properties props) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        Map<String, String> map = (Map) props;
        super.putAll(map);
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
}
