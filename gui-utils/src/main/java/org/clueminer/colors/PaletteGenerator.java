package org.clueminer.colors;

import java.awt.Color;
import java.util.Random;
import org.clueminer.dataset.api.ColorGenerator;

/**
 *
 * @author Tomas Barton
 */
public class PaletteGenerator implements ColorGenerator {

    private static final long serialVersionUID = 3184701217391675333L;
    private Color previous = null;

    @Override
    public Color next() {
        return next(previous);
    }

    @Override
    public Color next(Color base) {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        // mix the color
        if (base != null) {
            red = (red + base.getRed()) / 2;
            green = (green + base.getGreen()) / 2;
            blue = (blue + base.getBlue()) / 2;
        }

        previous = new Color(red, green, blue);

        return previous;
    }

    @Override
    public void reset() {
        previous = null;
    }
}
