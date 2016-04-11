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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.DataRow;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.row.FloatArrayDataRow;
import org.clueminer.dataset.row.Tools;
import org.clueminer.exception.EscapeException;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public class FloatArrayFactory<E extends Instance> extends AbstractRowFactory<E> implements InstanceBuilder<E> {

    public FloatArrayFactory(Dataset<E> dataset) {
        super(dataset);
    }

    /**
     * @param dataset
     * @param decimalPointCharacter the letter for decimal points, usually '.'
     */
    public FloatArrayFactory(Dataset<E> dataset, char decimalPointCharacter) {
        super(dataset, decimalPointCharacter);
    }

    @Override
    public E build(double[] values) {
        FloatArrayDataRow row = new FloatArrayDataRow(values.length);
        for (int i = 0; i < values.length; i++) {
            row.set(i, (float) values[i]);
        }
        return (E) row;
    }

    @Override
    public E build(int capacity) {
        return (E) new FloatArrayDataRow(capacity);
    }

    @Override
    public E createCopyOf(E orig) {
        FloatArrayDataRow row = new FloatArrayDataRow(orig.size());
        row.setClassValue(orig.classValue());
        return (E) row;
    }

    /**
     * Creates a data row from an array of Strings. If the corresponding
     * attribute is nominal, the string is mapped to its index, otherwise it is
     * parsed using <code>Double.parseDouble(String)</code> .
     *
     * @param strings
     * @param attributes
     * @return
     * @see FileDataRowReader
     */
    @Override
    public E create(String[] strings, Attribute[] attributes) {
        FloatArrayDataRow dataRow = (FloatArrayDataRow) create(strings.length);
        for (int i = 0; i < strings.length; i++) {
            if (strings[i] != null) {
                strings[i] = strings[i].trim();
            }
            if ((strings[i] != null) && (strings[i].length() > 0) && (!strings[i].equals("?"))) {
                if (attributes[i].isNominal()) {
                    try {
                        String unescaped = Tools.unescape(strings[i]);
                        dataRow.setValue(attributes[i], attributes[i].getMapping().mapString(unescaped));
                    } catch (EscapeException ex) {
                        Logger.getLogger(DoubleArrayFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    dataRow.setValue(attributes[i], string2Float(strings[i], this.decimalPointCharacter));
                }
            } else {
                dataRow.setValue(attributes[i], Double.NaN);
            }
        }
        dataRow.trim();
        return (E) dataRow;
    }

    /**
     * Creates a data row from an Object array. The classes of the object must
     * match the value type of the corresponding {@link Attribute}. If the
     * corresponding attribute is nominal, <code>data[i]</code> will be cast to
     * String. If it is numerical, it will be cast to Number.
     *
     * @param data
     * @param attributes
     * @return
     * @throws ClassCastException if data class does not match attribute type
     * @see DatabaseDataRowReader
     */
    public DataRow create(Object[] data, Attribute[] attributes) {
        DataRow dataRow = (DataRow) create(data.length);
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null) {
                if (attributes[i].isNominal()) {
                    dataRow.setValue(attributes[i], attributes[i].getMapping().mapString(((String) data[i]).trim()));
                } else {
                    dataRow.setValue(attributes[i], ((Number) data[i]).doubleValue());
                }
            } else {
                dataRow.setValue(attributes[i], Double.NaN);
            }
        }
        dataRow.trim();
        return dataRow;
    }

    /**
     * Creates a data row from an Object array. The classes of the object must
     * match the value type of the corresponding {@link Attribute}. If the
     * corresponding attribute is nominal, <code>data[i]</code> will be cast to
     * String. If it is numerical, it will be cast to Number.
     *
     * @param data
     * @param attributes
     * @return
     * @throws ClassCastException if data class does not match attribute type
     */
    public DataRow create(Double[] data, Attribute[] attributes) {
        DataRow dataRow = (DataRow) create(data.length);
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null) {
                if (attributes[i].isNominal()) {
                    dataRow.setValue(attributes[i], attributes[i].getMapping().mapString((String.valueOf(data[i])).trim()));
                } else {
                    dataRow.setValue(attributes[i], ((Number) data[i]).doubleValue());
                }
            } else {
                dataRow.setValue(attributes[i], Double.NaN);
            }
        }
        dataRow.trim();
        return dataRow;
    }

    private static float string2Float(String str, char decimalPointCharacter) {

        if (str == null) {
            return Float.NaN;
        }
        try {
            str = str.replace(decimalPointCharacter, '.');
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            Logger.getLogger(FloatArrayFactory.class.getName()).log(Level.SEVERE, "DataRowFactory.string2Float(String): ''{0}'' is not a valid number!", str);
            return Float.NaN;
        }
    }

}
