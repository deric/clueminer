/*
 * DefaultTerminal.java
 *
 * Created on October 17, 2007, 3:34 AM
 *
 */
package com.panayotis.gnuplot.terminal;

import java.io.InputStream;
import java.io.Serializable;

/**
 * The default GNUPlot terminal. This terminal has no output file, or specific terminal type.
 * It is here, just in case no specific terminal type has been provided.
 * <p>Note that in many operating systems this terminal is not really useful, or even
 * visible. Please use a specific terminal yourself.
 * @author teras
 */
public class DefaultTerminal implements GNUPlotTerminal, Serializable {
    
    /**
     * This Terminal has no type.
     * @return Always returns "".
     */
    public String getType() {
        return "";
    }

    /**
     * No output is defined for this terminal
     * @return Always returns "".
     */
    public String getOutputFile() {
        return "";
    }

    /**
     * No processing is performed. The plot is displayed in the default output.
     * @param stdout The output of GNUPlot. Not processed.
     */
    public String processOutput(InputStream stdout) {
        return null;
    }

}
