package org.clueminer.utils;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * Same functionality as @link{Props} here we store data in Properties, there in
 * HashMap<String,String>
 *
 * @author deric
 * @deprecated
 */
@Deprecated
public class AlgorithmParameters implements Serializable {

    private static final long serialVersionUID = 2921695136119710825L;
    //EH changed from private to protected so this class could be extended
    protected Properties properties;

    public AlgorithmParameters() {
        this.properties = new Properties();
    }

    public void setProperty(String name, String value) {
        properties.setProperty(name, value);
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public String getString(String key, String defValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defValue;
        }
        return value;
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public boolean getBoolean(String key, boolean defValue) {
        String bool = properties.getProperty(key);
        if (bool == null) {
            return defValue;
        }
        return Boolean.parseBoolean(bool);
    }

    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public int getInt(String key, int defValue) {
        int value;
        try {
            value = Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException nfe) {
            return defValue;
        }
        return value;
    }

    public long getLong(String key) {
        return Long.parseLong(properties.getProperty(key));
    }

    public long getLong(String key, long defValue) {
        long value;
        try {
            value = Long.parseLong(properties.getProperty(key));
        } catch (NumberFormatException nfe) {
            return defValue;
        }
        return value;
    }

    public float getFloat(String key) {
        return Float.parseFloat(properties.getProperty(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(properties.getProperty(key));
    }

    public float getFloat(String key, float defValue) {
        float value;
        try {
            value = Float.parseFloat(properties.getProperty(key));
        } catch (NumberFormatException nfe) {
            return defValue;
        }
        return value;
    }

    public URL getURL(String key) throws MalformedURLException {
        return new URL(properties.getProperty(key));
    }

    // util methods
    public Map getMap() {
        return properties;
    }

    public Set entrySet() {
        return properties.entrySet();
    }

    public Properties getProperty() {
        return properties;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        Set<Entry<Object, Object>> col = properties.entrySet();
        for(Entry<Object, Object> e: col){
            sb.append(e.getKey().toString());
            sb.append(": ");
            sb.append(e.getValue().toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
