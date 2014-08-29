/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.gnuplot.layout;

import com.panayotis.gnuplot.plot.Page;

/**
 * This Object is used to define how graphs will be positioned on the whole page
 * @author teras
 */
public interface GraphLayout {

    /**
     * Sets the required definitions in the "set multiplot" part of gnuplot commands.
     * It can be also used to set various parameters, such as X/Y position or dimension.
     * @param page The Page we are referring to
     * @param buffer Where to send commands, just after the "set multiplot" part. It might not be used.
     */
    public abstract void setDefinition (Page page, StringBuffer buffer);
}
