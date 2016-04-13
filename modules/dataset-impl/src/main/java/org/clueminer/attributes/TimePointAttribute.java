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
package org.clueminer.attributes;

import java.util.Date;
import java.util.Iterator;
import org.clueminer.dataset.api.AbstractAttribute;
import org.clueminer.dataset.api.NominalMapping;
import org.clueminer.math.Numeric;
import org.clueminer.stats.NumericalStats;
import org.clueminer.types.TimePoint;

/**
 * TimePoint represents timepoints of one measurement, distances between
 * ChartPoints are not guaranteed to be the same, in fact they are never the
 * same. DataItem taken from other measurement, must be interpolated to fit
 * Chart's specific points.
 *
 * @author Tomas Barton
 */
public class TimePointAttribute extends AbstractAttribute implements TimePoint, Numeric, Comparable<Numeric> {

    private static final long serialVersionUID = -7207089775143688737L;
    /**
     * chart's indexing system starts from 0
     */
    private final int index;
    /**
     * position in graph starts from 0.0
     */
    private double position = 0;
    /**
     * time corresponding with this point
     */
    private long timestamp;

    public TimePointAttribute(int index, long tp) {
        super(String.valueOf(index), BasicAttrType.NUMERICAL, BasicAttrRole.INPUT);
        this.index = index;
        this.timestamp = tp;
        registerStatistics(new NumericalStats(this));
    }

    public TimePointAttribute(int index, long tp, double position) {
        super("timeserie-" + String.valueOf(index), BasicAttrType.NUMERICAL, BasicAttrRole.INPUT);
        this.index = index;
        this.timestamp = tp;
        this.position = position;
        registerStatistics(new NumericalStats(this));
    }

    /**
     * Clone constructor.
     */
    private TimePointAttribute(TimePointAttribute a, int index) {
        super(a);
        this.index = index;
    }

    @Override
    public TimePointAttribute duplicate() {
        return new TimePointAttribute(this, this.index);
    }

    @Override
    public NominalMapping getMapping() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMapping(NominalMapping nominalMapping) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isNominal() {
        return false;
    }

    @Override
    public boolean isNumerical() {
        return true;
    }

    @Override
    public String asString(double value, int digits, boolean quoteNominal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long time) {
        this.timestamp = time;
    }

    public Date getDate() {
        return new Date(timestamp);
    }

    public void setPosition(double pos) {
        this.position = pos;
    }

    @Override
    public double getPosition() {
        return position;
    }

    @Override
    public double getValue() {
        return position;
    }

    /**
     * This is basically number of rows in a dataset
     *
     * @return Number of attribute's values
     */
    @Override
    public int size() {
        return dataset.size();
    }

    public int compareTo(TimePointAttribute that) {
        if (this.getValue() == that.getValue()) {
            return 0;
        } else if (this.getValue() > that.getValue()) {
            return 1;
        }
        return -1;
    }

    @Override
    public int compareTo(Numeric that) {
        if (this.getValue() == that.getValue()) {
            return 0;
        } else if (this.getValue() > that.getValue()) {
            return 1;
        }
        return -1;
    }

    @Override
    public int compareTo(double d) {
        if (this.getValue() == d) {
            return 0;
        } else if (this.getValue() > d) {
            return 1;
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("TimePoint [");
        str.append("index: ").append(index).append(", timestamp: ").append(timestamp);
        str.append(", pos: ").append(position);
        str.append("]");
        return str.toString();
    }

    @Override
    public Iterator<Double> values() {
        return new TimePointAttributeIterator();
    }

    @Override
    public double[] asDoubleArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isMeta() {
        return role == BasicAttrRole.META;
    }

    @Override
    public Object getMissingValue() {
        if (allowMissing()) {
            return Double.NaN;
        }
        throw new RuntimeException("missing values are not allowed for attribute " + getName());
    }

    class TimePointAttributeIterator implements Iterator<Double> {

        private int i = 0;
        private int index = getIndex();

        @Override
        public boolean hasNext() {
            return i < dataset.size();
        }

        @Override
        public Double next() {
            i++;
            return dataset.instance(i - 1).value(index);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from instance using the iterator.");

        }
    }
}
