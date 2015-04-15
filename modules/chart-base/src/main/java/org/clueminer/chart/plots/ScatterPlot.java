package org.clueminer.chart.plots;

import org.clueminer.chart.base.AbstractPlot;

/**
 *
 * @author Tomas Barton
 */
public class ScatterPlot extends AbstractPlot {

    private static final long serialVersionUID = 1450179727270901601L;

    public ScatterPlot(int width, int height) {
        super(width, height);
    }

    public ScatterPlot(ChartBuilder builder) {
        super(builder.width, builder.height);
    }
}
