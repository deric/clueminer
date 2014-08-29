/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.gnuplot.dataset.parser;

/**
 * Parser for Double data 
 * @author teras
 */
public class DoubleDataParser extends NumericDataParser {

    /**
     * Create a new numeric data parser for Double values
     */
    public DoubleDataParser() {
        super();
    }

    /**
     * Create a new Double data parser, with the information that the first column is in date format.
     * @param first_column_date Whether the first column is in date format
     */
    public DoubleDataParser(boolean first_column_date) {
        super(first_column_date);
    }

    /**
     * Check whether this String represents a Double number
     * @param format the String containing the Double number
     * @return True, if this is a representation of a Double number
     */
    protected boolean checkNumberFormat(String format) {
        try {
            Double.parseDouble(format);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
