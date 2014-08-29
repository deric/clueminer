package org.clueminer.chart.api;

import java.awt.Graphics2D;
import java.io.Serializable;

/**
 *
 * @author Tomas Barton
 */
public abstract class Chart implements Serializable {

    private static final long serialVersionUID = 8338700913376159042L;

    public Chart() {
    }

    public abstract String getName();

    public abstract void paint(Graphics2D g, ChartConfig cf);
}
