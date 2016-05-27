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

import java.util.Arrays;
import java.util.Iterator;
import org.clueminer.dataset.api.DataVector;
import org.clueminer.dataset.api.Statistics;
import org.clueminer.dataset.api.Stats;
import org.clueminer.dataset.api.StatsNum;

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
    private double q1 = Double.NaN;
    private double q3 = Double.NaN;
    private double median = Double.NaN;

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
        q1 = Double.NaN;
        median = Double.NaN;
        q3 = Double.NaN;
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
    public double statistics(Stats name) {
        switch ((StatsNum) name) {
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
            case RANGE:
                if (Double.isInfinite(minimum) || Double.isInfinite(maximum)) {
                    recalculate();
                }
                return maximum - minimum;
            case AVG:
            case MEAN:
                return avg();
            case VARIANCE:
                return variance();
            case SUM:
                return sum;
            case STD_SQ:
                return stdSq();
            case SQSUM:
                return squaredSum;
            case STD_DEV:
                return Math.sqrt(variance());
            case STD_COR:
                return Math.sqrt(stdSq());
            case ABS_DEV:
                if (Double.isNaN(absdev)) {
                    absdev = absDev();
                }
                return absdev;
            case Q1:
                if (Double.isNaN(q1)) {
                    computeQuartiles();
                }
                return q1;
            case Q2:
            case MEDIAN:
                if (Double.isNaN(median)) {
                    computeQuartiles();
                }
                return median;
            case Q3:
                if (Double.isNaN(q3)) {
                    computeQuartiles();
                }
                return q3;
            case QCD:
                if (Double.isNaN(q3) || Double.isNaN(q1)) {
                    computeQuartiles();
                }
                return (q3 - q1) / (q3 + q1);
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
     * An unbiased estimator for the variance is given by applying Bessel's correction
     *
     * @return unbiased sample variance, denoted s^2
     */
    private double stdSq() {
        return ((valueCounter > 1) ? sNew / (valueCounter - 1) : 0.0);
    }

    @Override
    public StatsNum[] provides() {
        return StatsNum.values();
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
        return ((valueCounter > 1) ? sNew / valueCounter : 0.0);
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
        double mean = statistics(StatsNum.AVG);
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

    private double[] toArray() {
        double[] res = new double[data.size()];
        Iterator<? extends Object> iter = data.values();
        for (int i = 0; i < res.length; i++) {
            res[i] = (Double) iter.next();
        }
        return res;
    }

    /**
     * For computing quartiles and median we need one scan and sorting all data.
     * There are at least 3 methods for computing quartiles:
     *
     * @see https://en.wikipedia.org/wiki/Quartile
     *
     */
    private void computeQuartiles() {
        double val[] = toArray();
        Arrays.sort(val);
        int m; //index of median value
        int q;
        //median it the middle value
        // even numbers in total
        if (val.length % 2 == 0) {
            //even numbers
            m = val.length >>> 1; //equal to division by 2
            median = (val[m - 1] + val[m]) / 2.0;

        } else {
            //odd numbers in total
            m = (val.length - 1) >>> 1; //equal to division by 2
            median = val[m];
        }

        // Q1 and Q3
        if (m % 2 == 0) {
            q = m >>> 1; //equal to division by 2
            q1 = (val[q - 1] + val[q]) / 2.0;
            q = (val.length - m) >>> 1;
            q3 = (val[q - 1] + val[q]) / 2.0;
        } else {
            //odd numbers in total
            q = (m - 1) >>> 1; //equal to division by 2
            q1 = val[q];
            q = val.length - q - 1;
            q3 = val[q];
        }
    }

}
