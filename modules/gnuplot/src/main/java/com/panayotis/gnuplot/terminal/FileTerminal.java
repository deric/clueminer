/*
 * FileTerminal.java
 *
 * Created on October 17, 2007, 2:51 AM
 *
 */
package com.panayotis.gnuplot.terminal;

/**
 * This Terminal supports file operations.
 * The results of the gnuplot commands can be stored in a file, if desired.
 * @author teras
 */
public class FileTerminal extends ExpandableTerminal {
    
    private String filename;
    /**
     * Creates a new instance of FileTerminal and output to stadard out
     * @param type The terminal type
     */
    public FileTerminal(String type) {
        this(type, "");
    }
    /**
     * Creates a new instance of FileTerminal and output to a specific file
     * @param type The terminal type
     * @param filename e filaname to use as an output for this terminal
     * @see #getOutputFile()
     */
    public FileTerminal(String type, String filename) {
        super(type);
        if (filename==null) filename = "";
        this.filename = filename;
    }
    
    /**
     * Retrieve the filaname to use as an output for this terminal.
     * If the filename empty, then the output will be dumped to standard output,
     * and retrieved by JavaPlot
     * @return If this parameter is not empty, the output filename
     */
    public String getOutputFile() {
        return filename;
    }
    
}
