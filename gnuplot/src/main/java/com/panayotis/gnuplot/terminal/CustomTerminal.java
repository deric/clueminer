/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.gnuplot.terminal;

/**
 * This is a user specific terminal. The user in run-time defines what kind of terminal wants.
 * The output is not processed. If you want to process the output, you might need to subclass 
 * this object and override processOutput(InputStream stdout) method.
 * @see GNUPlotTerminal#processOutput(java.io.InputStream) 
 * @author teras
 */
public class CustomTerminal extends ExpandableTerminal {

    private String file;

    /**
     * Create a new custom terminal
     * @param type The type of this terminal
     * @param file The filename to redirect output (if desired)
     */
    public CustomTerminal(String type, String file) {
        super(type);
        if (file == null)
            file = "";
        this.file = file;
    }

    /**
     * Retrieve the output filename
     * @return The filename which this terminal will direct gnuplot output
     */
    public String getOutputFile() {
        return file;
    }
}
