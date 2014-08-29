package org.clueminer.chart.api;

import java.awt.Rectangle;
import java.util.EventListener;

/**
 *
 * @author Tomas Barton
 */
public interface ChartListener extends EventListener
{
	public void chartChanged(Chart newChart);
	public void overlayAdded(Overlay overlay);
	public void overlayRemoved(Overlay overlay);
	public double zoomIn(Rectangle rect);
	public double zoomOut(Rectangle rect);

}

