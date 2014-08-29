package com.panayotis.gnuplot.plot;

/**
 * This interface is used by JavaPlot to handle various plot arguments. It can be
 * implemented to provide other entries for the plot command
 * @author teras
 */
public interface Plot {
    
    /**
     * Retrieve the definition part of the plot command. This is the part that is
     * given to the plot command, separated by commas. Commas and newlines are 
     * automatically added
     * @param buffer The buffer to store the argument of the plot command
     */
    public abstract void retrieveDefinition(StringBuffer buffer);
    /**
     * Retrieve the data set of this plot command. It is used only in data-set plots
     * and it is usually a set of numbers separated by space and newline and terminated
     * by the 'e' character. These data are appended at the end of the actual plot
     * command. If a plot argument does not require additional data sets, then this
     * method should do nothing.
     * @param buffer The buffer to store the data set
     */
    public abstract void retrieveData(StringBuffer buffer);
}
