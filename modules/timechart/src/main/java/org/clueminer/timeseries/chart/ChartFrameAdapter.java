package org.clueminer.timeseries.chart;

import java.awt.Rectangle;
import org.clueminer.chart.api.Chart;
import org.clueminer.chart.api.ChartListener;
import org.clueminer.chart.api.Overlay;

/**
 *
 * @author Tomas Barton
 */
public abstract class ChartFrameAdapter implements ChartListener {

    @Override
    public void chartChanged(Chart newChart) {
    }

    @Override
    public void overlayAdded(Overlay overlay) {
    }

    @Override
    public void overlayRemoved(Overlay overlay) {
    }

    @Override
    public double zoomIn(Rectangle rect) {
        return rect.getWidth();
    }

    @Override
    public double zoomOut(Rectangle rect) {
        return rect.getWidth();
    }
}
