/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.dendrogram;

import org.clueminer.clustering.api.Distribution;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 * Aggregate histogram data
 *
 * @author deric
 * @param <E>
 */
public class DistributionCollector<E extends Instance> implements Distribution<E> {

    private int numBins;
    private int[] bins;
    private int numSamples;
    private double min;
    private double max;
    private double step;

    public DistributionCollector(int numBins) {
        this.numBins = numBins;
        clear();
    }

    @Override
    public void datasetChanged(Dataset<E> dataset) {
        clear();
        min = dataset.min();
        max = dataset.max();
        step = (max - min) / (double) numBins;
    }

    @Override
    public final void clear() {
        this.bins = new int[numBins];
        this.numSamples = 0;
        this.min = Double.MAX_VALUE;
        this.max = Double.MIN_VALUE;
    }

    /**
     * Number for inclusion in dendrogram
     *
     * @param value
     */
    @Override
    public void sample(double value) {
        numSamples++;
        checkRange(value);
        int pos = countPos(value);
        //due to rounding error we might misplace value by one bin
        if (pos >= bins.length) {
            pos = bins.length - 1;
        }
        bins[pos]++;
    }

    protected int countPos(double value) {
        return (int) Math.floor((value - min) / step);
    }

    @Override
    public int hist(double value) {
        checkRange(value);
        int pos = countPos(value);
        if (pos >= bins.length) {
            pos = bins.length - 1;
        }
        return bins[pos];
    }

    private void checkRange(double value) {
        if (value < min || value > max) {
            throw new RuntimeException("Value " + value + " is outside of current range [" + min + ", " + max + "]");
        }
    }

    @Override
    public int[] getBins() {
        return bins;
    }

    @Override
    public double getStep() {
        return step;
    }

    /**
     * Min and max value in histogram
     *
     * @return min at index 0, max at index 1
     */
    @Override
    public int[] binsRange() {
        int[] range = new int[2];
        for (int i = 0; i < bins.length; i++) {
            if (bins[i] < range[0]) {
                range[0] = bins[i];
            }
            if (bins[i] > range[1]) {
                range[1] = bins[i];
            }
        }
        return range;
    }

    @Override
    public int getNumSamples() {
        return numSamples;
    }

    public void setNumBins(int bins) {
        this.numBins = bins;
        clear();
    }

    @Override
    public int getNumBins() {
        return numBins;
    }

    public void dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("Distribution of ").append(numSamples).append(" samples\n");
        sb.append("min = ").append(min).append(", max = ").append(max).append("\n");
        sb.append("step = ").append(step).append("\n");
        double value = min;
        for (int i = 0; i < bins.length; i++) {
            sb.append("[").append(value).append(" - ");
            value += step;
            sb.append(value).append("] = ").append(bins[i]).append("\n");
        }
        System.out.println(sb.toString());
    }

}
