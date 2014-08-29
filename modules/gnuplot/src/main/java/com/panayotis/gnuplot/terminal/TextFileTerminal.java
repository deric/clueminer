/*
 * TextFileTerminal.java
 *
 * Created on October 23, 2007, 10:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.panayotis.gnuplot.terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Base class of all terminals with output in text format.
 * @author teras
 */
public class TextFileTerminal extends FileTerminal {
    /**
     * 
     */
    protected String output = "";
    
    /** Creates a new instance of TextFileTerminal.
     * The output will be parsed by JavaPlot and stored in a String, since it is
     * expected to be a text and not binary data.
     * @param type the terminal type
     */
    public TextFileTerminal(String type) {
        this(type, "");
    }
    /**
     * Creates a new instance of TextFileTerminal and output to a specific file
     * @param type the terminal type
     * @param filename the file to save output to
     */
    public TextFileTerminal(String type, String filename) {
        super(type, filename);
    }
    
    /**
     * Process output of this terminal. Since this is a text terminal, the output
     * will be stored in a String
     * @param stdout The gnuplot output stream
     * @return Return the error as a String, if an error occured.
     */
    public String processOutput(InputStream stdout) {
        StringBuffer out = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(stdout));
        String line;
        try {
            while ((line=in.readLine())!=null)
                out.append(line);
            in.close();
        } catch (IOException ex) {
            return "I/O error while processing gnuplot output: "+ex.getMessage();
        }
        output = out.toString();
        return null;
    }
    
    /**
     * Retrieve the String with the output of the last gnuplot command
     * @return The String with gnuplot output
     */
    public String getTextOutput() {
        return output;
    }
}
