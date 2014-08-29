package org.clueminer.gnuplot;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author Tomas Barton
 */
public class ColorGenerator {

    private static Random rand = new Random();
    private static float luminance = 0.9f;

    private ColorGenerator() {
    }

    public static Color getRandomColor() {
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }
    
    /**
     * Generates hexadecimal format of colors (compatible with CSS)
     * @return 
     */
    public static String getHexColor(){
        Color color = ColorGenerator.getRandomColor();
        String rgb = Integer.toHexString(color.getRGB());
        return "#"+rgb.substring(2, rgb.length());
    }

    public static Color getTransparentColor(Color color, int alpha) {
        return new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                alpha);
    }

    public static Color getBrightColor() {
        float hue = rand.nextFloat();
// Saturation between 0.1 and 0.3
        float saturation = (rand.nextInt(2000) + 1000) / 10000f;
        
        return Color.getHSBColor(hue, saturation, luminance);
    }
}