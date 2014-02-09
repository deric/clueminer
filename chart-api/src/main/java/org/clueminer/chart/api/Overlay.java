package org.clueminer.chart.api;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.events.DatasetListener;
import org.clueminer.events.LogListener;
import org.openide.nodes.AbstractNode;
import org.w3c.dom.Element;

/**
 *
 * @author Tomas Barton
 */
public interface Overlay extends LogListener, DatasetListener {

    public String getName();

    public String getLabel();

    public Timeseries<? extends ContinuousInstance> getDataset();

    public abstract void paint(Graphics2D g, ChartConfig cf, Rectangle bounds);

    public abstract LinkedHashMap getHTML(ChartConfig cf, int i);

    public abstract void calculate();

    public boolean isLogarithmic();

    public void setLogarithmic(boolean b);

    public abstract AbstractNode getNode();

    public abstract Overlay newInstance();

    public void setDataset(Timeseries<? extends ContinuousInstance> d);

    public void loadFromTemplate(Element element);
}
