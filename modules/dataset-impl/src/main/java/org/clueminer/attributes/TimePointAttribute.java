package org.clueminer.attributes;

import java.util.Date;
import java.util.Iterator;
import org.clueminer.dataset.api.AbstractAttribute;
import org.clueminer.dataset.api.NominalMapping;
import org.clueminer.dataset.api.Statistics;
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
    public Object clone() {
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

    @Override
    public void updateStatistics(Object value) {
        double val = (Double) value;
        for (Statistics s : statistics) {
            s.valueAdded(val);
        }
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
