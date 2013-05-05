/*
 * FileDataSet.java
 *
 */

package com.panayotis.gnuplot.dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This object uses data sets already stored in files. 
 * @author teras
 */
public class FileDataSet extends GenericDataSet {
    
    /**
     * Creates a new instance of a data set, stored in a file.
     * When this object is initialized, the file is read into memory.
     * @param datafile The file containing the data set
     * @throws java.io.IOException when a I/O error is found
     * @throws java.lang.ArrayIndexOutOfBoundsException when the file has not consistent number of columns
     * @throws java.lang.NumberFormatException when the numbers inside the file are not parsable
     */
    public FileDataSet(File datafile) throws IOException, NumberFormatException, ArrayIndexOutOfBoundsException {
        super(true);

        BufferedReader in = new BufferedReader(new FileReader(datafile));
        String line;
        ArrayList<String> data;
        while ( (line=in.readLine())!=null && (!line.equals("")) ) {
            line = line.trim();
            if (!line.startsWith("#")) {
                data = new ArrayList<String>();
                StringTokenizer tk = new StringTokenizer(line);
                while (tk.hasMoreTokens()) {
                    data.add(tk.nextToken());
                }
                add(data);
            }
        }
    }
    
}
