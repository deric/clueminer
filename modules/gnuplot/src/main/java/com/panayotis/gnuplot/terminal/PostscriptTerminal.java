/*
 * PostscriptTerminal.java
 *
 * Created on October 16, 2007, 1:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.panayotis.gnuplot.terminal;

/**
 * This terminal uses postscript as it's backend
 * @author teras
 */
public class PostscriptTerminal extends TextFileTerminal {
    
    /**
     * Create a new instance of PostscriptTerminal.
     * It is recommended to use PostscriptTerminal(String filename) instead,
     * since this constructor does not produce any output file.
     */
    public PostscriptTerminal() {
        this("");
    }
    
    /**
     * Create a new Postscript terminal and save output to the specified file
     * @param filename The filename of the output postscript file
     */
    public PostscriptTerminal(String filename) {
        super("postscript", filename);
        setColor(true);
        setEPS(true);
    }
    
    /**
     * Select if the output will be in EPS format or not
     * @param eps If EPS mode will be used
     */
    public void setEPS(boolean eps) {
        if (eps)
            set("eps");
        else
            unset("eps");
    }
    
    /**
     * Select if the output will be color or not (monochrome)
     * @param color If the ouput will be in color
     */
    public void setColor(boolean color) {
        if (color)  {
            set("color");
            unset("monochrome");
        } else {
            set("monochrome");
            unset("color");
        }
    }
    
}
