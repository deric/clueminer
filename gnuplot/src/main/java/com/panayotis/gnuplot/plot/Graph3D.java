/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.panayotis.gnuplot.plot;

/**
 *
 * @author teras
 */
public class Graph3D extends Graph {

      /**
     * Get the actual gnuplot command to initiate the plot.
     * @return This method always returns "plot"
     */
    protected String getPlotCommand() {
        return "splot";
    }
}
