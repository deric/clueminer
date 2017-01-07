package org.clueminer.chart.api;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.events.DatasetListener;
import org.clueminer.events.LogListener;
import org.openide.nodes.AbstractNode;
import org.w3c.dom.Element;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface Overlay<E extends Instance> extends LogListener, DatasetListener {

    String getName();

    String getLabel();

    Dataset<E> getDataset();

    void paint(Graphics2D g, ChartConfig cf, Rectangle bounds);

    HashMap getHTML(ChartConfig cf, int i);

    void calculate();

    boolean isLogarithmic();

    void setLogarithmic(boolean b);

    AbstractNode getNode();

    Overlay newInstance();

    void setDataset(Dataset<E> dataset);

    void loadFromTemplate(Element element);
}
