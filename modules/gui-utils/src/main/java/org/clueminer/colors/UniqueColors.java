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
import org.clueminer.dataset.api.ColorGenerator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ColorGenerator.class)
public class UniqueColors implements ColorGenerator {

    private static final long serialVersionUID = -8374193292627939796L;
    private int id = 0;
    private static final byte EXPECTED_MAX = 15;
    private static final int HUE_FACTOR = 255 / EXPECTED_MAX;

    private static final String NAME = "unique";

    @Override
    public String getName() {
        return NAME;
    }

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

    @Override
    public void reset() {
        id = 0;
    }

}
