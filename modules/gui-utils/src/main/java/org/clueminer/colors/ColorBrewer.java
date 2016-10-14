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
 * A set of fixed colors
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ColorGenerator.class)
public class ColorBrewer implements ColorGenerator {

    private static final long serialVersionUID = -4190241089099580601L;

    private static final String NAME = "brewer";

    private static final Color[] scheme1 = new Color[]{
        new Color(178, 24, 43), new Color(33, 102, 172), new Color(161, 215, 106),
        new Color(5, 48, 97), new Color(244, 165, 130), new Color(153, 112, 171),
        new Color(90, 174, 97), new Color(208, 28, 139)};
    private final RandomColorsGenerator rg = new RandomColorsGenerator();
    private int cnt;

    public ColorBrewer() {
        cnt = 0;
    }

    @Override
    public String getName() {
        return NAME;
    }

    public void seek(int i) {
        if (i < scheme1.length) {
            this.cnt = i;
        }
    }

    @Override
    public Color next() {
        if (cnt < scheme1.length) {
            return scheme1[cnt++];
        }
        return rg.next();

    }

    @Override
    public Color next(Color base) {
        return rg.next();
    }

    @Override
    public void reset() {
        cnt = 0;
    }

}
