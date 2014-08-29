package org.clueminer.timeseries.chart;

import java.util.EventListener;

/**
 *
 * @author Tomas Barton
 */
public interface NormalizationListener extends EventListener {

    public void markerMoved(NormalizationEvent evt);
    
    public void normalizationCompleted();
}
