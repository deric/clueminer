/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.stats;

import java.util.Iterator;
import org.clueminer.dataset.api.DataVector;
import org.clueminer.dataset.api.IStats;
import org.clueminer.dataset.api.Statistics;

/**
 * Running statistics
 *
 * Chan, Tony F.; Golub, Gene H.; LeVeque, Randall J. (1983). Algorithms for
 * Computing the Sample Variance: Analysis and Recommendations. The American
 * Statistician 37, 242-247.
 *
 * Ling, Robert F. (1974). Comparison of Several Algorithms for Computing Sample
 * Means and Variances. Journal of the American Statistical Association, Vol.
 * 69, No. 348, 859-866.
 *
 * @see http://www.johndcook.com/standard_deviation.html
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
    private double absdev = Double.NaN;
    private double mOld, mNew, sOld, sNew;
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
    public void valueAdded(Object val) {
        double value = (Double) val;
        if (!Double.isNaN(value)) {
            if (value < minimum) {
                minimum = value;
            }
            if (value > maximum) {
                maximum = value;
            }

            valueCounter++;

            if (valueCounter == 1) {
                mOld = value;
                mNew = value;
                sOld = 0.0;
            } else {
                mNew = mOld + (value - mOld) / valueCounter;
                sNew = sOld + (value - mOld) * (value - mNew);

                // set up for next iteration
                mOld = mNew;
                sOld = sNew;
                annulateCache();
            }

            sum += value;
            squaredSum += value * value;
        }
    }

    @Override
    public void valueRemoved(Object val) {
        double value = (Double) val;
        if (!Double.isNaN(value)) {
            if (minimum == value) {
                resetMin();
            }
            if (maximum == value) {
                resetMax();
            }
            sum -= value;
            squaredSum -= value * value;
            valueCounter--;
            annulateCache();
        }
    }

    @Override
    public double statistics(IStats name) {
        switch ((AttrNumStats) name) {
            case MAX:

                if (Double.isInfinite(maximum)) {
                    recalculate();
                }
                return maximum;
            case MIN:
                if (Double.isInfinite(minimum)) {
                    recalculate();
                }
                return minimum;
            case AVG:
                return avg();
            case VARIANCE:
                return variance();
            case SUM:
                return sum;
            case STD_X:
                return stdx();
            case SQSUM:
                return squaredSum;
            case STD_DEV:
                return Math.sqrt(variance());
            case ABS_DEV:
                if (Double.isNaN(absdev)) {
                    absdev = absDev();
                }
                return absdev;
        }
        throw new RuntimeException("unknown statistics " + name);
    }

    /**
     * From Donald Knuth's Art of Computer Programming, Vol 2, page 232, 3rd
     * edition
     *
     * @return average (mean)
     */
    private double avg() {
        return (valueCounter > 0) ? mNew : 0.0;
    }

    /**
     * Without -1 -- SQRT(1/N * variance)
     *
     * @return
     */
    private double stdx() {
        return Math.sqrt(((valueCounter > 1) ? sNew / valueCounter : 0.0));
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
     * from beginning each time we add or remove one element
     *
     * @return variance of an attribute
     */
    private double variance() {
        return ((valueCounter > 1) ? sNew / (valueCounter - 1) : 0.0);
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
     * recounting all elements each time we change the set. Computationally less
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
        return Math.sqrt(asum / (data.size() - 1));
    }

    @Override
    public double get(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
