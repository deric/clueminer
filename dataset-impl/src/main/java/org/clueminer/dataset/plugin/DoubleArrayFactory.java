package org.clueminer.dataset.plugin;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.DataRow;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.dataset.row.Tools;
import org.clueminer.exception.EscapeException;

/**
 *
 * @author Tomas Barton
 */
public class DoubleArrayFactory implements InstanceBuilder<DoubleArrayDataRow> {

    private static int DEFAULT_CAPACITY = 50;
    /**
     * The decimal point character.
     */
    private char decimalPointCharacter = '.';

    /**
     * @param type
     * @param decimalPointCharacter the letter for decimal points, usually '.'
     */
    public DoubleArrayFactory(char decimalPointCharacter) {
        this.decimalPointCharacter = decimalPointCharacter;
    }

    @Override
    public DoubleArrayDataRow create(double[] values) {
        DoubleArrayDataRow row = new DoubleArrayDataRow(values.length);
        for (int i = 0; i < values.length; i++) {
            row.set(i, values[i]);
        }
        return row;
    }

    @Override
    public DoubleArrayDataRow create(double[] values, Object classValue) {
        DoubleArrayDataRow row = create(values);
        row.setClassValue(classValue);
        return row;
    }

    @Override
    public DoubleArrayDataRow create() {
        return new DoubleArrayDataRow(DEFAULT_CAPACITY);
    }

    @Override
    public DoubleArrayDataRow createCopyOf(DoubleArrayDataRow orig) {
        DoubleArrayDataRow row = new DoubleArrayDataRow(orig.size());
        row.setClassValue(orig.classValue());
        return row;
    }

    @Override
    public DoubleArrayDataRow createCopyOf(DoubleArrayDataRow orig, Dataset<DoubleArrayDataRow> parent) {
        return createCopyOf(orig);
    }

    /**
     * Creates a new DataRow with the given initial capacity.
     */
    @Override
    public DoubleArrayDataRow create(int size) {
        return new DoubleArrayDataRow(size);
    }

    /**
     * Creates a data row from an array of Strings. If the corresponding
     * attribute is nominal, the string is mapped to its index, otherwise it is
     * parsed using
     * <code>Double.parseDouble(String)</code> .
     *
     * @see FileDataRowReader
     */
    @Override
    public DoubleArrayDataRow create(String[] strings, Attribute[] attributes) {
        DoubleArrayDataRow dataRow = create(strings.length);
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
                    dataRow.setValue(attributes[i], string2Double(strings[i], this.decimalPointCharacter));
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
     * corresponding attribute is nominal,
     * <code>data[i]</code> will be cast to String. If it is numerical, it will
     * be cast to Number.
     *
     * @throws ClassCastException if data class does not match attribute type
     * @see DatabaseDataRowReader
     */
    public DataRow create(Object[] data, Attribute[] attributes) {
        DataRow dataRow = create(data.length);
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
     * corresponding attribute is nominal,
     * <code>data[i]</code> will be cast to String. If it is numerical, it will
     * be cast to Number.
     *
     * @throws ClassCastException if data class does not match attribute type
     * @see DatabaseDataRowReader
     */
    public DataRow create(Double[] data, Attribute[] attributes) {
        DataRow dataRow = create(data.length);
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

    private static double string2Double(String str, char decimalPointCharacter) {

        if (str == null) {
            return Double.NaN;
        }
        try {
            str = str.replace(decimalPointCharacter, '.');
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            Logger.getLogger(DoubleArrayFactory.class.getName()).log(Level.SEVERE, "DoubleArrayFactory.string2Double(String): ''{0}'' is not a valid number!", str);
            return Double.NaN;
        }
    }
}
