/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panayotis.gnuplot.layout;

import com.panayotis.gnuplot.plot.Page;
import java.io.Serializable;

/**
 * Position graphs in absolute coordinates. This is actually a dummy layout - no layout information is used.
 * @author teras
 */
public class ManualGraphLayout implements GraphLayout, Serializable {

    /**
     * This is a dummy layout manager, which actually does nothing
     * @param page The Page which layout we want to calculate
     * @param buffer The definition part of the multiplot layout
     */
    public void setDefinition(Page page, StringBuffer buffer) {
    }

}
