package org.clueminer.timeseries.chart;

import org.clueminer.timeseries.chart.SelectionEvent;
import java.util.EventListener;

/**
 *
 * @author Tomas Barton
 */
public interface SelectionListener extends EventListener {
    
    public void areaSelected(SelectionEvent evt);
}
