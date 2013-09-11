package org.clueminer.asr;

import java.util.LinkedList;
import java.util.List;
import org.clueminer.approximation.api.DataReduction;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.types.TimePoint;

/**
 *
 * @author Tomas Barton
 */
public class AdaptableSamplingReduction implements DataReduction {

    private int window;

    public AdaptableSamplingReduction() {
        //default values of parameters
        this.window = 10;
    }

    public AdaptableSamplingReduction(int window) {
        this.window = window;
    }

    @Override
    public Timeseries<ContinuousInstance> apply(Timeseries<ContinuousInstance> input) {
        double threshold = window / Math.abs(input.getMax() - input.getMin());
        double diff;
        System.out.println("threshold = " + threshold);
        List<Long> timepoints = new LinkedList<Long>();
        TimePoint[] tp = input.getTimePoints();

        for (Instance inst : input) {
            int i = 0;
            while (i < inst.size() - 1) {
                for (int j = i; j < i + window; j++) {
                    diff = Math.abs(inst.value(i + 1) - inst.value(i));
                    System.out.println("diff(" + i + ") = " + diff);
                    if (diff > threshold) {
                        timepoints.add(tp[i].getTimestamp());
                    }
                }
                i += window;
            }
            System.out.println("reduced timepoints: " + timepoints.size());
        }

        return null;
    }
}
