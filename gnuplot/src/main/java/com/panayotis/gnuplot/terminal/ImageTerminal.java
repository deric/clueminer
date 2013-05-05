/*
 * PostscriptTerminal.java
 *
 * Created on October 16, 2007, 1:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.panayotis.gnuplot.terminal;

import com.panayotis.gnuplot.GNUPlotException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * This terminal is able to process gnuplot output as an image. 
 * The image produced can be used by any Java object which is able to handle 
 * BufferedImage
 * @author teras
 */
public class ImageTerminal extends FileTerminal {
    private BufferedImage img;
    
    /**
     * Create a new image terminal, and use PNG as it's backend
     */
    public ImageTerminal() {
        super("png");
    }
    
    /**
     * Read the produced image from gnuplot standard output
     * @param stdout The gnuplot output stream
     * @return The error definition, if any
     */
    public String processOutput(InputStream stdout) {
        try {
            img = ImageIO.read(stdout);
        } catch (IOException ex) {
            return "Unable to create PNG image: "+ex.getMessage();
        }
        if (img==null) return "Unable to create image from the gnuplot output. Null image created.";
        return null;
    }
    
    /**
     * Get a handler of the produced image by this terminal
     * @return the plot image
     */
    public BufferedImage getImage() {
        return img;
    }
    
       
}
