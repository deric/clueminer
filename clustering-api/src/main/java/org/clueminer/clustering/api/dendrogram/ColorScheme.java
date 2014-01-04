package org.clueminer.clustering.api.dendrogram;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Tomas Barton
 */
public interface ColorScheme {

    public BufferedImage createGradientImage(Color color1, Color color2);

    public Color getColor(double value);
}
