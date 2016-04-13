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
package org.clueminer.dataset.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.api.TypeHandler;
import static org.clueminer.dataset.impl.AbstractRowFactory.dispatch;
import org.clueminer.dataset.row.IntegerDataRow;
import org.clueminer.exception.ParserError;

/**
 *
 * @author deric
 */
public class IntegerRowFactory<E extends Instance> extends AbstractRowFactory<E> implements InstanceBuilder<E> {

    static {
        dispatch.put(Double.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, DecimalFormat df) {
                row.set(attr.getIndex(), (int) value);
            }
        });
        dispatch.put(Float.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, DecimalFormat df) {
                row.set(attr.getIndex(), (int) value);
            }
        });
        dispatch.put(Integer.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, DecimalFormat df) {
                row.set(attr.getIndex(), (Integer) value);
            }
        });
        dispatch.put(Boolean.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, DecimalFormat df) {
                row.set(attr.getIndex(), (boolean) value ? 1.0 : 0.0);
            }
        });
        dispatch.put(String.class, new TypeHandler() {
            @Override
            public void handle(Object value, Attribute attr, Instance row, DecimalFormat df) throws ParseException, ParserError {
                BasicAttrType at = (BasicAttrType) attr.getType();
                switch (at) {
                    case NUMERICAL:
                    case NUMERIC:
                    case REAL:
                        row.set(attr.getIndex(), string2Int(value.toString(), df));
                        break;
                    default:
                        throw new ParseException("conversion to " + at + " is not supported for '" + value + "'", row.getIndex());
                }

            }
        });
    }

    private static int string2Int(String str, DecimalFormat df) throws ParseException, ParserError {
        if (str == null) {
            return 0;
        }
        try {
            if (df == null) {
                return Integer.parseInt(str);
            } else {
                Number num = df.parse(str);
                return num.intValue();
            }
        } catch (NumberFormatException e) {
            Logger.getLogger(IntegerRowFactory.class.getName()).log(Level.SEVERE, "string2Int(String): ''{0}'' is not a valid number!", str);
            throw new ParserError(e.getMessage(), e);
        }
    }

    public IntegerRowFactory(Dataset<E> dataset) {
        super(dataset);
    }

    public IntegerRowFactory(Dataset<E> dataset, char decimalChar) {
        super(dataset, decimalChar);
    }

    @Override
    public E createCopyOf(E orig) {
        IntegerDataRow row = new IntegerDataRow(orig.size());
        row.setId(orig.getId());
        row.setIndex(orig.getIndex());
        row.setClassValue(orig.classValue());
        return (E) row;
    }

    @Override
    public E build(int capacity) {
        return (E) new IntegerDataRow(capacity);
    }

    @Override
    public E build(double[] values) {
        IntegerDataRow row = new IntegerDataRow(values.length);
        for (int i = 0; i < values.length; i++) {
            row.set(i, values[i]);
        }
        return (E) row;
    }

}
