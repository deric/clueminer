/*
 * SVGTerminal.java
 *
 * Created on October 24, 2007, 7:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.panayotis.gnuplot.terminal;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import javax.swing.JPanel;


/**
 * This Terminal uses SVG graphics to display data on screen. It relies on the
 * open source project SVGSalamander (http://svgsalamander.dev.java.net/)
 * @author teras
 */
public class SVGTerminal extends TextFileTerminal {
    
    /**
     * Creates a new instance of SVGTerminal 
     */
    public SVGTerminal() {
        this("");
    }
    
    /**
     * Creates a new instance of SVGTerminal and store output to a specific file
     * @param filename
     */
    public SVGTerminal(String filename) {
        super("svg", filename);
    }
    
    /**
     * Process output of this terminal. Typically this is used to overcome a bug
     * in SVGSalamander
     * @param stdout The gnuplot output stream
     * @return Return the error as a String, if an error occured.
     */ 
    public String processOutput(InputStream stdout) {
        String out_status = super.processOutput(stdout);
        if (output!=null && getOutputFile().equals("") ) {
            output = output.replace("currentColor", "black");
        }
        return out_status;
    }
    
    /**
     * Retrieve a JPanel whith the provided SVG drawn on it.
     * @return The JPanel with the SVG drawing
     * @throws java.lang.ClassNotFoundException If the SVGSalamander library could
     * not be found, or if any other error occured.
     */
    public JPanel getPanel() throws ClassNotFoundException {
        /* Use reflection API to create the representation in SVG format */
        Object svgDisplayPanel = null;
        if (output==null || output.equals(""))
            throw new NullPointerException("SVG output is empty; probably SVG terminal is not used or plot() not executed yet.");
        try {
            svgDisplayPanel = Class.forName("com.kitfox.svg.SVGDisplayPanel").newInstance();
            Object universe = Class.forName("com.kitfox.svg.SVGUniverse").newInstance();
            Object diagram = null;
            
            universe.getClass().getMethod("loadSVG", Reader.class, String.class).invoke(universe, new StringReader(output), "plot");
            URI uri = (URI) universe.getClass().getMethod("getStreamBuiltURI", String.class).invoke(universe, "plot");
            diagram = universe.getClass().getMethod("getDiagram", URI.class).invoke(universe, uri);
            svgDisplayPanel.getClass().getMethod("setDiagram", Class.forName("com.kitfox.svg.SVGDiagram")).invoke(svgDisplayPanel, diagram);
        } catch (NoSuchMethodException e) {
            throw new ClassNotFoundException(e.getMessage());
        } catch (InstantiationException e) {
            throw new ClassNotFoundException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new ClassNotFoundException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new ClassNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new ClassNotFoundException(e.getMessage());
        }
        
        return (JPanel)svgDisplayPanel;
    }
    
}
