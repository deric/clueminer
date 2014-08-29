/*
 * PlotStyle.java
 *
 * Created on 26 Οκτώβριος 2007, 2:58 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.panayotis.gnuplot.style;

import com.panayotis.gnuplot.PropertiesHolder;

/**
 * This object represents the styles which can be used in gnuplot to personalize
 * each prot
 * @author teras
 */
public class PlotStyle extends PropertiesHolder {
    
    private Style type;
    private FillStyle fill;
    
    /**
     * Creates a new instance of PlotStyle with default parameters
     */
    public PlotStyle() {
        this(null);
    }
    /**
     * Creates a new instance of PlotStyle with a specified style
     * @param style The style to use
     */
    public PlotStyle(Style style) {
        super(" ", "");
        fill = null;
        setStyle(style);
    }
    
    /**
     * Set the current style to the given one
     * @param style the style to use
     */
    public void setStyle(Style style) {
        this.type = style;
    }
    
    /**
     * Gather the properties of this style.
     * This method is used internally by GNUPlot
     * @param buf The Srting buffer to store this object's properties.
     */
    public void appendProperties(StringBuffer buf) {
        if (type!=null) {
            buf.append(" with ").append(type.name().toLowerCase());
            super.appendProperties(buf);
            
            if (fill!=null && type.filled) fill.appendProperties(buf);
        }
    }
    
    /**
     * Set the line width of this graph
     * @param width The line width. If this number is less than zero, then the 
     * default parameter will be used
     */
    public void setLineWidth(int width) {
        if (width<0)
            unset("linewidth");
        else
            set("linewidth", String.valueOf(width));
    }
    
    /**
     * Set the point size of this graph
     * @param width The point size. If this number is less than zero, then the 
     * default parameter will be used
     */
    public void setPointSize(int width) {
        if (width<0)
            unset("pointsize");
        else
            set("pointsize", String.valueOf(width));
    }
    
    /**
     * Set the line type of this graph. This option is terminal dependent.
     * @param type The line type. If this number is less than zero, then the 
     * default parameter will be used
     */
    public void setLineType(int type) {
        if (type<-1)
            unset("linetype");
        else
        set("linetype", String.valueOf(type));
    }
    
    /**
     * Set the line type of this graph to be a specific color. This option is 
     * terminal dependent.
     * @param col The color to use. If this parameter is null, then the default
     * parameter will be used.
     */
    public void setLineType(PlotColor col) {
        if (col==null) 
            unset("linetype");
        else
            set("linetype", col.getColor());
    }

    /**
     * Set the point type of this graph. This option is terminal dependent.
     * @param type The point type. If this number is less than zero, then the 
     * default parameter will be used
     */
    public void setPointType(int type) {
        if (type<-1)
            unset("pointtype");
        else
        set("pointtype", String.valueOf(type));
    }
    
    /**
     * Set the fill style of this graph. If the desired style does not support 
     * fill then this parameter will be ignored.
     * @param fillstyle The fill style to use. If this parameter is null, then the default
     * parameter will be used.
     */
    public void setFill(FillStyle fillstyle) {
        this.fill = fillstyle;
    }

}
