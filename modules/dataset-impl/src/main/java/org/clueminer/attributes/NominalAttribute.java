/*
 * Copyright (C) 2011-2018 clueminer.org
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

import org.clueminer.dataset.api.AbstractAttribute;
import org.clueminer.dataset.api.AttributeType;
import org.clueminer.dataset.row.Tools;

/**
 *
 * @author Tomas Barton
 */
public abstract class NominalAttribute extends AbstractAttribute {

    private static final long serialVersionUID = -3830980883541763869L;

    protected NominalAttribute(String name, AttributeType type) {
        super(name, type, BasicAttrRole.INPUT);
        //	registerStatistics(new NominalStatistics());
        //	registerStatistics(new UnknownStatistics());
    }

    protected NominalAttribute(NominalAttribute other) {
        super(other);
    }

    @Override
    public boolean isNominal() {
        return true;
    }

    @Override
    public boolean isNumerical() {
        return false;
    }

    /**
     * Returns a string representation and maps the value to a string if type is
     * nominal. The number of digits is ignored.
     *
     * @param value
     * @param digits
     * @param quoteNominal
     * @return
     */
    @Override
    public String asString(double value, int digits, boolean quoteNominal) {
        if (Double.isNaN(value)) {
            return "?";
        } else {
            try {
                String result = getMapping().mapIndex((int) value);
                if (quoteNominal) {
                    result = Tools.escape(result);
                    result = "\"" + result + "\"";
                }
                return result;
            } catch (Throwable e) {
                return "?";
            }
        }
    }
}
