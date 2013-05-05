/*
 * Axis.java
 *
 * Created on October 14, 2007, 10:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.panayotis.gnuplot.plot;

import com.panayotis.gnuplot.*;

/**
 * This class represents the various axes of the plot. It is used to set various
 * axis-related parameters
 * @author teras
 */
public class Axis extends PropertiesHolder {
    
    private String name;
    
    /**
     * Creates a new instance of Axis.
     * @param name The name of the axis
     */
    Axis(String name) {
        this.name = name;
    }
    
    /**
     * Get the name of this axis as a String.
     * @return The name of the axis. Usually it is "x" or "y".
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set whether this axis is in logarithmic scale or not
     * @param log Set, if this axis is in logarithmic scale
     */
    public void setLogScale(boolean log) {
        if (log)
            set("logscale", getName());
        else
            unset("logscale");
    }
    
    
    /**
     * Set the label of this axis.
     * @param label THe label of this axis
     * @see #setLabel(String,String,int)
     */
    public void setLabel(String label) {
        setLabel(label, null, -1);
    }
    
    /**
     * Set the label and the font of the current axis
     * @param label The label of this axis
     * @param font Font name
     * @param size Font size
     */
    public void setLabel(String label, String font, int size) {
        String fontname="";
        if (font!=null) {
            fontname=" font '"+font+( (size>1)?","+size:"" )+"'";
        }
        set(getName()+"label", "'"+label+"'"+fontname);
    }
    
    /**
     * Define the area to plot.
     * <br>
     * Note that if we have choosed log scale, then the values should be guaranteed to be
     * larger than zero. If the axis is in log scale, do not set a value less than zero
     * or else a plot error will occure.
     * @param from The minimum value
     * @param to The maximum value
     */
    public void setBoundaries(double from, double to) {
        if (from==Double.POSITIVE_INFINITY || from==Double.NEGATIVE_INFINITY || from==Double.MAX_VALUE || from==Double.MIN_VALUE) return;
        if (to==Double.POSITIVE_INFINITY || to==Double.NEGATIVE_INFINITY || to==Double.MAX_VALUE || to==Double.MIN_VALUE) return;
        if (to<from) {
            double swap = to;
            to = from;
            from = swap;
        }
        set(getName()+"range", "["+from+":"+to+"]");
    }
    
    
}
