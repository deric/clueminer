/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.panayotis.gnuplot.dataset.parser;

/**
 * Generic data parser for numeric data
 * @author teras
 */
public abstract class NumericDataParser implements DataParser {

    private boolean first_column_date;

    /**
     * Create a new numeric data parser
     */
    public NumericDataParser() {
        this(false);
    }
    
    /**
     * Create a new numeric data parser, with the information that the first column is in date format.
     * @param first_column_date Whether the first column is in date format
     */
    public NumericDataParser(boolean first_column_date) {
        this.first_column_date = first_column_date;
    }
    
    /**
     * Check whether a data value with a specific index number is valid or not
     * @param data The numerical data to check
     * @param index The index of the specified data
     * @return True, if the data is valid.
     */
    public boolean isValid(String data, int index) {
        if (first_column_date && index == 0) return true;
        return checkNumberFormat(data);
    }

    /**
     * Check whether this String represents a number
     * @param format the String containing the number
     * @return True, if this is a representation of a number
     */
    protected abstract boolean checkNumberFormat(String format);
}
