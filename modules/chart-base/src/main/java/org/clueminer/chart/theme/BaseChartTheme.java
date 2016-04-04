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
package org.clueminer.chart.theme;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;

/**
 *
 * @author deric
 */
public class BaseChartTheme implements ChartTheme {

    public static final Color bg = new Color(210, 210, 210);
    public static final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14);

    @Override
    public Paint getBackground() {
        return bg;
    }

    @Override
    public Font getFont() {
        return font;
    }

}
