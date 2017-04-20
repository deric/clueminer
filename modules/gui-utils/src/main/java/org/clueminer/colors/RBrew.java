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
import org.clueminer.dataset.api.ColorGenerator;
import org.openide.util.lookup.ServiceProvider;

/**
 * Color generator based on R package ColorBrewer
 *
 * @author deric
 */
@ServiceProvider(service = ColorGenerator.class)
public class RBrew implements ColorGenerator {

    private static final String NAME = "R-brew";

    private RColorBrewer[] colors;
    private int cnt;

    @Override
    public String getName() {
        return NAME;
    }

    public RBrew() {
        colors = RColorBrewer.getDivergingColorPalettes(true);
        cnt = 0;
    }

    @Override
    public Color next() {
        int mod = cnt % colors.length;
        int div = cnt / colors.length;

        Color[] pallete = colors[mod].getColorPalette(div + 1);

        cnt++;
        return pallete[div];
    }

    @Override
    public Color next(Color base) {
        return next();
    }

    @Override
    public void reset() {
        cnt = 0;
    }

}
