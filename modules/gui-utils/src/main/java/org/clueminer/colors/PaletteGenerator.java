/*
 * Copyright (C) 2011-2017 clueminer.org
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
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ColorGenerator.class)
public class PaletteGenerator implements ColorGenerator {

    private static final long serialVersionUID = 3184701217391675333L;
    private Color previous = null;
    private static final String NAME = "palette";

    @Override
    public String getName() {
        return NAME;
    }

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
