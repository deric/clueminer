/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.panayotis.gnuplot.dataset.parser;

/**
 * Parser for Integer data 
 * @author teras
 */
public class IntegerDataParser extends NumericDataParser {

    /**
     * Create a new numeric data parser for Float values
     */
    public IntegerDataParser() {
        super();
    }

    /**
     * Create a new Integer data parser, with the information that the first column is in date format.
     * @param first_column_date Whether the first column is in date format
     */
    public IntegerDataParser(boolean first_column_date) {
        super(first_column_date);
    }

    /**
     * Check whether this String represents a Integer number
     * @param format the String containing the Integer number
     * @return True, if this is a representation of a Integer number
     */
    protected boolean checkNumberFormat(String format) {
        try {
            Integer.parseInt(format);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
