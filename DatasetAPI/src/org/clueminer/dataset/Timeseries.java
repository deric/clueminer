package org.clueminer.dataset;

import org.clueminer.instance.ContinuousInstance;
import org.clueminer.interpolation.Interpolator;
import org.clueminer.types.TimePoint;
import org.netbeans.api.progress.ProgressHandle;

/**
 * Dataset for representing time series
 *
 * @author Tomas Barton
 */
public interface Timeseries<E extends ContinuousInstance> extends Dataset<E> {

    public void crop(int begin, int end, ProgressHandle ph);

    public double interpolate(int index, double x, Interpolator interpolator);

    public TimePoint[] getTimePoints();
    
    public void setTimePoints(TimePoint[] tp);

    /**
     * Minimum value in the dataset
     *
     * @return
     */
    public double getMin();

    /**
     * Maximum value in the dataset
     *
     * @return
     */
    public double getMax();
}
