package org.clueminer.utils;

import java.util.List;

/**
 *
 * @author Tomas Barton
 */
public interface DatasetWriter {

    /**
     * Writes the entire list to a CSV file. The list is assumed to be a
     * String[]
     *
     * @param allLines a List of String[], with each String[] representing a
     * line of the file.
     */
    public void writeAll(List<String[]> allLines);

    /**
     * Writes the next line to the file.
     *
     * @param nextLine a string array with each comma-separated element as a
     * separate entry.
     * @param applyQuotesToAll true if all values are to be quoted. false
     * applies quotes only to values which contain the separator, escape, quote
     * or new line characters.
     */
    public void writeNext(String[] nextLine, boolean applyQuotesToAll);

    /**
     * Writes the next line to the file.
     *
     * @param nextLine a string array with each comma-separated element as a
     * separate entry.
     */
    public void writeNext(String[] nextLine);
}
