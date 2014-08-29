/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.panayotis.gnuplot.dataset.parser;

/**
 * Use a specific numeric parser to check if the data provided are valid or not.
 * @author teras
 */
public interface DataParser {

    /**
     * Check whether a data value with a specific index number is valid or not
     * @param data The data to check
     * @param index The index of the specified data
     * @return True, if the data is valid.
     */
    public boolean isValid(String data, int index);
}
