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

import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.stats.AttrNumStats;

/**
 * Aggregate histogram data
 *
 * @author deric
 * @param <E>
 */
public class DistributionCollector<E extends Instance> {

    private int numBins = 100;
    private int[] bins;
    private int numSamples;
    private double min;
    private double max;
    private double step;

    public DistributionCollector(int numBins) {
        this.numBins = numBins;
        clear();
    }

    public void datasetChanged(Dataset<E> dataset) {
        Attribute attr;
        double value;
        clear();
        for (int i = 0; i < dataset.attributeCount(); i++) {
            attr = dataset.getAttribute(i);
            value = attr.statistics(AttrNumStats.MIN);
            if (value < min) {
                min = value;
            }
            value = attr.statistics(AttrNumStats.MAX);
            if (value > max) {
                max = value;
            }
        }
        step = (max - min) / (double) numBins;
    }

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
    public void sample(double value) {
        numSamples++;
        if (value < min || value > max) {
            throw new RuntimeException("Value " + value + " is outside of current range [" + min + ", " + max + "]");
        }
        int pos = (int) Math.floor(value / step);
        //due to rounding error we might misplace value by one bin
        if (pos >= bins.length) {
            pos = bins.length - 1;
        }
        bins[pos]++;
    }

    public int[] getBins() {
        return bins;
    }

    public int getNumSamples() {
        return numSamples;
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
