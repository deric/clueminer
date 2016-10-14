/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.colors;

import java.awt.Color;
import java.util.Random;
import org.clueminer.dataset.api.ColorGenerator;
import org.openide.util.lookup.ServiceProvider;

/**
 * A pseudo random generator
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ColorGenerator.class)
public class RandomColorsGenerator implements ColorGenerator {

    private final Random rand;
    private static final String NAME = "random";

    /**
     * Constructor for objects of class RandomColor initializes the
     * random number generator
     */
    public RandomColorsGenerator() {
        rand = new Random();
    }

    @Override
    public String getName() {
        return NAME;
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
