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
package org.clueminer.gui;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 *
 * @author Tomas Barton
 */

public final class StrokeGenerator {

    public static final Stroke DEFAULT_STROKE = new BasicStroke(1.0f);
    private static final Stroke[] strokes = {
        DEFAULT_STROKE,
        new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[] {9.0f,6.0f}, 0.0f),
        new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] {1.0f,1.0f}, 0.0f),
        new BasicStroke(2.0f),
        new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[] {9.0f,6.0f}, 0.0f),
        new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, new float[] {2.0f,2.0f}, 0.0f),
        new BasicStroke(3.0f),
        new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[] {9.0f,6.0f}, 0.0f),
        new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3.0f, new float[] {3.0f,3.0f}, 0.0f)
    };

    private StrokeGenerator() {}

    public static Stroke[] getStrokes() { return strokes; }

    public static int getStrokeIndex(Stroke stroke) {
        for (int i = 0; i < strokes.length; i++) {
            if (strokes[i].equals(stroke)) {
                return i;
            }
        }
        return -1;
    }

    public static Stroke getStroke(int i) { return i != -1 ? strokes[i] : null; }

}
