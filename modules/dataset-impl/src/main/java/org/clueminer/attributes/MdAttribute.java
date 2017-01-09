/*
 * Copyright (C) 2011-2017 clueminer.org
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
import org.clueminer.dataset.api.AttributeType;
import org.clueminer.dataset.api.NominalMapping;
import org.clueminer.math.Numeric;

/**
 * Multi-dimensional allows storing several dependent variables (e.g. timeseries,
 * 2D, 3D data)
 *
 * @author deric
 */
public class MdAttribute extends AbstractAttribute implements Numeric, Comparable<Numeric> {

    public MdAttribute(String name, AttributeType type, AttributeRole role) {
        super(name, type, role);
    }

    public MdAttribute(MdAttribute other) {
        super(other.getName(), other.getType(), other.getRole());
    }

    @Override
    public MdAttribute duplicate() {
        return new MdAttribute(this);
    }

    @Override
    public NominalMapping getMapping() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMapping(NominalMapping nominalMapping) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isNominal() {
        return false;
    }

    @Override
    public boolean isNumerical() {
        //TODO: currently only numerical data are supported
        return true;
    }

    @Override
    public boolean isMeta() {
        return role == BasicAttrRole.META;
    }

    @Override
    public Iterator<? extends Object> values() {
        return new MdAttributeIterator();
    }

    class MdAttributeIterator implements Iterator<Object> {

        private int i = 0;
        private int index = getIndex();

        @Override
        public boolean hasNext() {
            return i < dataset.size();
        }

        @Override
        public Object next() {
            i++;
            return dataset.instance(i - 1).value(index);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from instance using the iterator.");

        }
    }

    @Override
    public double[] asDoubleArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int size() {
        return dataset.size();
    }

    @Override
    public String asString(double value, int digits, boolean quoteNominal) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getMissingValue() {
        return MISSING_NOMINAL_VALUE;
    }

    @Override
    public double getValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compareTo(double d) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compareTo(Numeric o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("MdAttribute [");
        str.append("index: ").append(getIndex()).append(", values: ");
        Iterator<? extends Object> iter = this.values();
        int i = 0;
        while (iter.hasNext()) {
            if (i > 0) {
                str.append(", ");
            }
            str.append(iter.next());
            i++;
        }
        str.append("]");
        return str.toString();
    }

}
