package org.clueminer.colors;

import java.awt.Color;
import java.util.Random;
import org.clueminer.dataset.api.ColorGenerator;

/**
 * A pseudo random generator
 *
 * @author Tomas Barton
 */
public class RandomColorsGenerator implements ColorGenerator {

    private final Random rand;

    /**
     * Constructor for objects of class RandomColor initializes the
     * random number generator
     */
    public RandomColorsGenerator() {
        rand = new Random();
    }

    /**
     * randomGray returns a pseudorandom gray Color
     *
     * @return a pseudorandom Color
     */
    public Color randomGray() {
        int intensity = rand.nextInt(256);
        return (new Color(intensity, intensity, intensity));
    }

    /**
     * a pseudorandom Color
     *
     * @return a pseudorandom Color
     */
    @Override
    public Color next() {
        return (new Color(rand.nextInt(256),
                          rand.nextInt(256),
                          rand.nextInt(256)));
    }

    @Override
    public Color next(Color base) {
        return next();
    }

    @Override
    public void reset() {
        //nothing to do
    }
}
