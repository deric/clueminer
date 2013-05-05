/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.panayotis.gnuplot.dataset.parser;

/**
 * Parser for Float data 
 * @author teras
 */
public class FloatDataParser extends NumericDataParser {

    /**
     * Create a new numeric data parser for Float values
     */
    public FloatDataParser() {
        super();
    }

    /**
     * Create a new Float data parser, with the information that the first column is in date format.
     * @param first_column_date Whether the first column is in date format
     */
    public FloatDataParser(boolean first_column_date) {
        super(first_column_date);
    }

    /**
     * Check whether this String represents a Float number
     * @param format the String containing the Float number
     * @return True, if this is a representation of a Float number
     */
    protected boolean checkNumberFormat(String format) {
        try {
            Float.parseFloat(format);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
