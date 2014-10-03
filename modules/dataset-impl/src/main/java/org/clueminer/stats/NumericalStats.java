package org.clueminer.stats;

import java.util.Iterator;
import org.clueminer.dataset.api.DataVector;
import org.clueminer.dataset.api.IStats;
import org.clueminer.dataset.api.Statistics;

/**
 *
 * @author Tomas Barton
 */
public class NumericalStats implements Statistics {

    private static final long serialVersionUID = -8824202504092017667L;
    private double minimum = Double.POSITIVE_INFINITY;
    private double maximum = Double.NEGATIVE_INFINITY;
    private double sum = 0.0d;
    private double squaredSum = 0.0d;
    private int valueCounter = 0;
    private double variance = Double.NaN;
    private double avg = Double.NaN;
    private double stddev = Double.NaN;
    private double absdev = Double.NaN;
    private final DataVector data;

    public NumericalStats(DataVector attribute) {
        this.data = attribute;
    }

    @Override
    public Object clone() {
        return new NumericalStats(this.data);
    }

    @Override
    public void reset() {
        minimum = Double.POSITIVE_INFINITY;
        maximum = Double.NEGATIVE_INFINITY;
        sum = 0;
        squaredSum = 0;
        valueCounter = 0;
    }

    private void annulateCache() {
        variance = Double.NaN;
        avg = Double.NaN;
        stddev = Double.NaN;
        absdev = Double.NaN;
    }

    /**
     * Recompute statistics for the vector
     */
    public void recalculate() {
        reset();
        Iterator<? extends Object> it = data.values();
        while (it.hasNext()) {
            valueAdded((Double) it.next());
        }
    }

    @Override
    public void valueAdded(double value) {
        if (!Double.isNaN(value)) {
            if (value < minimum) {
                minimum = value;
            }
            if (value > maximum) {
                maximum = value;
            }
            sum += value;
            squaredSum += value * value;
            valueCounter++;
            annulateCache();
        }
    }

    @Override
    public void valueRemoved(double value) {
        if (!Double.isNaN(value)) {
            if (minimum == value) {
                resetMin();
            }
            if (maximum == value) {
                resetMax();
            }
            sum += value;
            squaredSum += value * value;
            valueCounter++;
            annulateCache();
        }
    }

    @Override
    public double statistics(IStats name) {
        switch ((AttrNumStats) name) {
            case MAX:
                if (maximum == Double.NEGATIVE_INFINITY) {
                    recalculate();
                }
                return maximum;
            case MIN:
                if (minimum == Double.POSITIVE_INFINITY) {
                    recalculate();
                }
                return minimum;
            case AVG:
                return avg();
            case VARIANCE:
                return variance();
            case SUM:
                return sum;
            case SQSUM:
                return squaredSum;
            case STD_DEV:
                if (Double.isNaN(stddev)) {
                    //std dev is just square root of variance
                    stddev = Math.sqrt(statistics(AttrNumStats.VARIANCE));
                }
                return stddev;
            case ABS_DEV:
                if (Double.isNaN(absdev)) {
                    absdev = absDev();
                }
                return absdev;
        }
        throw new RuntimeException("unknown statistics " + name);
    }

    private double avg() {
        if (Double.isNaN(avg)) {
            avg = sum / valueCounter;
        }
        return avg;
    }

    @Override
    public AttrNumStats[] provides() {
        return AttrNumStats.values();
    }

    private void resetMin() {
        minimum = Double.POSITIVE_INFINITY;
        Iterator<? extends Object> it = data.values();
        double value;
        while (it.hasNext()) {
            value = (Double) it.next();
            if (value < minimum) {
                minimum = value;
            }
        }
    }

    private void resetMax() {
        maximum = Double.NEGATIVE_INFINITY;
        Iterator<? extends Object> it = data.values();
        double value;
        while (it.hasNext()) {
            value = (Double) it.next();
            if (value > maximum) {
                maximum = value;
            }
        }
    }

    /**
     * Pretty fast way how to compute variance, we don't have to recount sums
     * from begining each time we add or remove one element
     *
     * @return variance of an attribute
     */
    private double variance() {
        if (Double.isNaN(variance)) {
            //if the average is cached, we reduce number of divisions
            variance = (squaredSum - avg() * avg() * valueCounter) / (valueCounter - 1);
            if (variance < 0) // this is due to rounding errors above
            {
                variance = 0;
            }
        }
        return variance;
    }

    /**
     * classical approach, but we have computed partial sums, so we don't need
     * this private double stdDev() { double mean =
     * statistics(AttrNumStats.AVG); Iterator<? extends Object> it =
     * attribute.values(); double value; double sqsum = 0.0; while
     * (it.hasNext()) { value = (Double) it.next(); sqsum += Math.pow(value -
     * mean, 2); } return Math.sqrt((1.0 / (attribute.size() - 1) * sqsum)); }
     */
    /**
     * Since absolute value is a bit tricky for counting we can't avoid
     * recounting all elements each time we change the set. Computionally less
     * expensive would be using some estimate of the mean. However this work
     * with reasonable precision only in case of a big set
     *
     * @return mean absolute deviation
     */
    private double absDev() {
        double mean = statistics(AttrNumStats.AVG);
        Iterator<? extends Object> it = data.values();
        double value;
        double asum = 0.0;
        while (it.hasNext()) {
            value = (Double) it.next();
            asum += Math.abs(value - mean);
        }
        return Math.sqrt(asum / data.size());
    }

    @Override
    public double get(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
