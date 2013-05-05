/*
 * Debug.java
 *
 * Created on October 17, 2007, 3:01 PM
 *
 */
package com.panayotis.iodebug;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * This object is responsible to display debug and/or general information on any
 * given Writer (by default to System.err).<p>
 * It is possible to set another output writer, in order to redirect the errors
 * whereever the programmer wants.
 * @author teras
 */
public class Debug {
    /**
     * Absolutely no debug information is presented
     */
    public final static int QUIET = 0;
    /**
     * Only critical information is presented
     */
    public final static int CRITICAL = 10;
    /**
     * Only errors and critical information is presented
     */
    public final static int ERROR = 20;
    /**
     * Warnings, as well as errors and critical information is presented
     */
    public final static int WARNING = 30;
    /**
     * All messages except verbose messages are presented
     */
    public final static int INFO = 40;
    /**
     * All messages are presented
     */
    public final static int VERBOSE = 50;
    
    private static final String NL = System.getProperty("line.separator");
    
    private int level;
    private Writer out;
    
    
    /**
     * Creates a new instance of Debug
     */
    public Debug() {
        setLevel(WARNING);
        out = new BufferedWriter(new OutputStreamWriter(System.err));
    }
    
    
    /**
     * Set the level of verbosity.
     * @param level Only messages at least as critical as this level are presented.
     */
    public void setLevel( int level ) {
        this.level = level;
    }
    
    /**
     * Present a message on the Debug stream
     * @param message Message to display. Automatically adds a newline at the end of the string.
     * @param level Level of verbosity. If this level is les critical than the desired level,
     * the message is not displayed.
     */
    public void msg(String message, int level) {
        if (message==null||message.equals(""))
            return;
        if (level>QUIET && level<=this.level) {
            try {
                out.write(message);
                if (!message.endsWith(NL))
                    out.write(NL);
                out.flush();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    /**
     * Set the output writer. By default this is the System.err stream.
     * @param out The Debug stream writer.
     */
    public void setWriter(Writer out) {
        if (out==null) throw new NullPointerException("Debug: set of null output device.");
        this.out = out;
    }
}
