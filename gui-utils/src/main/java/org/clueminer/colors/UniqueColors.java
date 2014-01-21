package org.clueminer.colors;

import java.awt.Color;
import org.clueminer.dataset.api.ColorGenerator;

/**
 *
 * @author Tomas Barton
 */
public class UniqueColors implements ColorGenerator {

    private int id = 0;
    private static final byte EXPECTED_MAX = 15;
    private static final int HUE_FACTOR = 255 / EXPECTED_MAX;

    @Override
    public Color next() {
        HsbColor hsb = new HsbColor(175, 175);
        hsb.setHue((id * HUE_FACTOR) % 255);
        id++;
        return hsb.toColor();
    }

    @Override
    public Color next(Color base) {
        return next();
    }

}
