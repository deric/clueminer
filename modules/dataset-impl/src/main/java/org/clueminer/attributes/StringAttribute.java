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

/**
 *
 * @author deric
 */
public class StringAttribute extends AbstractAttribute {

    private boolean nominal = false;

    public StringAttribute(String name) {
        super(name, BasicAttrType.STRING, BasicAttrRole.META);
        //TODO: text statistics
        //registerStatistics(new TextStats(this));
    }

    public StringAttribute(String name, AttributeRole role) {
        super(name, BasicAttrType.STRING, role);
    }

    private StringAttribute(StringAttribute other) {
        super(other);
        this.nominal = other.nominal;
    }

    @Override
    public Object clone() {
        return new StringAttribute(this);
    }

    @Override
    public void updateStatistics(Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        return nominal;
    }

    public void setNominal(boolean b) {
        this.nominal = b;
    }

    @Override
    public boolean isNumerical() {
        return false;
    }

    @Override
    public boolean isMeta() {
        return role == BasicAttrRole.META;
    }

    @Override
    public Iterator<? extends Object> values() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

}
