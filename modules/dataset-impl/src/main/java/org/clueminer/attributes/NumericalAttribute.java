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

import java.util.Iterator;
import org.clueminer.dataset.api.AbstractAttribute;
import org.clueminer.dataset.api.AttributeRole;
import org.clueminer.dataset.api.NominalMapping;
import org.clueminer.dataset.api.Statistics;
import org.clueminer.dataset.row.Tools;
import org.clueminer.stats.NumericalStats;

/**
 *
 * @author Tomas Barton
 */
public class NumericalAttribute extends AbstractAttribute {

    private static final long serialVersionUID = 5512480424675307535L;

    protected NumericalAttribute(String name, AttributeRole role) {
        super(name, BasicAttrType.NUMERICAL, role);
        registerStatistics(new NumericalStats(this));
    }

    public NumericalAttribute(String name) {
        super(name, BasicAttrType.NUMERICAL, BasicAttrRole.INPUT);
        registerStatistics(new NumericalStats(this));
    }

    /**
     * Clone constructor.
     */
    private NumericalAttribute(NumericalAttribute a) {
        super(a);
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
    public NominalMapping getMapping() {
        throw new UnsupportedOperationException("The method getNominalMapping() is not supported by numerical attributes! You probably tried to execute an operator on a numerical data which is only able to handle nominal values. You could use one of the discretization operators before this application.");
    }

    /**
     * Does nothing.
     */
    @Override
    public void setMapping(NominalMapping mapping) {
    }

    /**
     * Returns a string representation of statistics. If the numberOfDigits is
     * greater than 0 this number is used to format the string
     */
    @Override
    public String asString(double value, int numberOfDigits, boolean quoteNominal) {
        return Tools.formatIntegerIfPossible(value, numberOfDigits, false);
    }

    /**
     * Clones this attribute.
     */
    @Override
    public NumericalAttribute duplicate() {
        return new NumericalAttribute(this);
    }

    @Override
    public void updateStatistics(Object value) {
        for (Statistics s : statistics) {
            s.valueAdded(value);
        }
    }

    @Override
    public Iterator<Double> values() {
        return new NumericalAttributeIterator();
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

    @Override
    public double[] asDoubleArray() {
        double[] res = new double[size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = dataset.instance(i).value(getIndex());
        }
        return res;
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

    class NumericalAttributeIterator implements Iterator<Double> {

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
