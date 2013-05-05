/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.panayotis.gnuplot.dataset.parser;

/**
 * Parser for Long data 
 * @author teras
 */
public class LongDataParser extends NumericDataParser {

    /**
     * Create a new numeric data parser for Float values
     */
    public LongDataParser() {
        super();
    }

    /**
     * Create a new Long data parser, with the information that the first column is in date format.
     * @param first_column_date Whether the first column is in date format
     */
    public LongDataParser(boolean first_column_date) {
        super(first_column_date);
    }

    /**
     * Check whether this String represents a Long number
     * @param format the String containing the Long number
     * @return True, if this is a representation of a Long number
     */
    protected boolean checkNumberFormat(String format) {
        try {
            Long.parseLong(format);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
