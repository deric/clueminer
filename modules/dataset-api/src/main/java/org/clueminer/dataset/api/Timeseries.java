package org.clueminer.dataset.api;

import java.util.Collection;
import java.util.Date;
import org.clueminer.math.Interpolator;
import org.clueminer.types.TimePoint;
import org.netbeans.api.progress.ProgressHandle;

/**
 * Dataset for representing time series
 *
 * @author Tomas Barton
 * @param <E>
 */
public interface Timeseries<E extends ContinuousInstance> extends Dataset<E> {

    void crop(int begin, int end, ProgressHandle ph);

    double interpolate(int index, double x, Interpolator interpolator);

    TimePoint[] getTimePoints();

    double[] getTimePointsArray();

    double[] getTimestampsArray();

    Collection<? extends Date> getTimePointsCollection();

    void setTimePoints(TimePoint[] tp);

    /**
     * Minimum value in the dataset
     *
     * @return
     */
    double getMin();

    /**
     * Maximum value in the dataset
     *
     * @return
     */
    double getMax();
}
