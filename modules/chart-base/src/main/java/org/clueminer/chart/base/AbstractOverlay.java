package org.clueminer.chart.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.ChartData;
import org.clueminer.chart.api.Overlay;
import org.clueminer.chart.api.PropertyListener;
import org.clueminer.chart.api.Range;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.events.DatasetEvent;
import org.clueminer.events.DatasetListener;
import org.clueminer.events.LogEvent;
import org.clueminer.events.LogListener;
import org.clueminer.xml.XMLUtil;
import org.clueminer.xml.XMLUtil.XMLTemplate;
import org.openide.nodes.AbstractNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractOverlay implements Serializable, DatasetListener, Overlay, LogListener, XMLTemplate {

    private static final long serialVersionUID = 2709569808433244828L;
    protected Dataset<? extends Instance> dataset;
    protected LinkedHashMap<String, ContinuousInstance> instances;
    private boolean logarithmic = false;

    public AbstractOverlay() {
        instances = new LinkedHashMap<String, ContinuousInstance>();
    }

    @Override
    public boolean isLogarithmic() {
        return logarithmic;
    }

    @Override
    public void setLogarithmic(boolean b) {
        logarithmic = b;
    }

    public String getFontHTML(Color color, String text) {
        String html = "<font color=\"" + Integer.toHexString(color.getRGB() & 0x00ffffff) + "\">" + text + "</font>";
        return html;
    }

    @Override
    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    @Override
    public void setDataset(Dataset<? extends Instance> dataset) {
        this.dataset = dataset;
    }

    public void addDataset(String key, ContinuousInstance value) {
        instances.put(key, value);
    }

    public Instance getDataset(String key) {
        return instances.get(key);
    }

    public void removeDatasets() {
        instances.clear();
    }

    @Override
    public abstract String getName();

    @Override
    public abstract String getLabel();

    @Override
    public abstract Overlay newInstance();

    @Override
    public abstract LinkedHashMap getHTML(ChartConfig cf, int i);

    public Range getRange(ChartConfig cf, String price) {
        if (instances.isEmpty()) {
            return new Range();
        }

        Range range = null;
        Iterator<ContinuousInstance> it = instances.values().iterator();

        while (it.hasNext()) {
            ContinuousInstance d = it.next();
            double min = d.getMin();
            double max = d.getMax();

            if (range == null) {
                range = new Range(min - (max - min) * 0.01, max + (max - min) * 0.01);
            } else {
                range = Range.combine(range, new Range(min - (max - min) * 0.01, max + (max - min) * 0.01));
            }
        }

        return range;
    }

    @Override
    public abstract void paint(Graphics2D g, ChartConfig cf, Rectangle bounds);

    @Override
    public abstract void calculate();

    public abstract Color[] getColors();

    public abstract double[] getValues(ChartConfig cf);

    public abstract double[] getValues(ChartConfig cf, int i);

    public abstract boolean getMarkerVisibility();

    @Override
    public abstract AbstractNode getNode();

    @Override
    public void datasetChanged(DatasetEvent evt) {
        synchronized (this) {
            ChartData cd = (ChartData) evt.getSource();
            setDataset(cd.getDataset());
            calculate();
        }
    }

    @Override
    public void fire(LogEvent evt) {
        ChartPropertiesImpl cp = (ChartPropertiesImpl) evt.getSource();
        logarithmic = cp.getAxisLogarithmicFlag();
        calculate();
    }

    /**
     * If an override in the overlay class sets this to false that overlay is
     * not included in the range calculation of the chart.
     *
     * @return whether to include this overlay in chart range
     */
    public boolean isIncludedInRange() {
        return true;
    }

    @Override
    public void saveToTemplate(Document document, Element element) {
        AbstractPropertiesNode node = (AbstractPropertiesNode) getNode();
        PropertyListener listener = node.getPropertyListener();
        Field[] fields = listener.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.getModifiers() == Modifier.PRIVATE) {
                    XMLUtil.addProperty(document, element, field.getName(), field.get(listener));
                }
            } catch (Exception ex) {
                Logger.getLogger(getName()).log(Level.SEVERE, "", ex);
            }
        }
    }

    @Override
    public void loadFromTemplate(Element element) {
        AbstractPropertiesNode node = (AbstractPropertiesNode) getNode();
        PropertyListener listener = node.getPropertyListener();
        Field[] fields = listener.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.getModifiers() == Modifier.PRIVATE) {
                    if (field.getType().equals(String.class)) {
                        field.set(listener, XMLUtil.getStringProperty(element, field.getName()));
                    } else if (field.getType().equals(int.class)) {
                        field.set(listener, XMLUtil.getIntegerProperty(element, field.getName()));
                    } else if (field.getType().equals(double.class)) {
                        field.set(listener, XMLUtil.getDoubleProperty(element, field.getName()));
                    } else if (field.getType().equals(double.class)) {
                        field.set(listener, XMLUtil.getFloatProperty(element, field.getName()));
                    } else if (field.getType().equals(boolean.class)) {
                        field.set(listener, XMLUtil.getBooleanProperty(element, field.getName()));
                    } else if (field.getType().equals(Color.class)) {
                        field.set(listener, XMLUtil.getColorProperty(element, field.getName()));
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(getName()).log(Level.SEVERE, "", ex);
            }
        }
    }
}
