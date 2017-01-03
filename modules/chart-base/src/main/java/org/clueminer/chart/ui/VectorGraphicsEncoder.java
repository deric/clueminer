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
package org.clueminer.chart.ui;

import java.io.IOException;
import org.clueminer.chart.api.Drawable;

/**
 *
 * @author deric
 */
public final class VectorGraphicsEncoder {

    /**
     * Constructor - Private constructor to prevent instantiation
     */
    private VectorGraphicsEncoder() {

    }

    public enum VectorGraphicsFormat {

        EPS, PDF, SVG;
    }

    public static void saveVectorGraphic(Drawable chart, String fileName, VectorGraphicsFormat vectorGraphicsFormat) throws IOException {
        throw new UnsupportedOperationException("not supported yet");
        /*   VectorGraphics2D g = null;

         switch (vectorGraphicsFormat) {
         case EPS:
         g = new EPSGraphics2D(0.0, 0.0, chart.getWidth(), chart.getHeight());
         break;
         case PDF:
         g = new PDFGraphics2D(0.0, 0.0, chart.getWidth(), chart.getHeight());
         break;
         case SVG:
         g = new SVGGraphics2D(0.0, 0.0, chart.getWidth(), chart.getHeight());
         break;

         default:
         break;
         }

         chart.paint(g, chart.getWidth(), chart.getHeight());

         // Write the vector graphic output to a file
         FileOutputStream file = new FileOutputStream(fileName + "." + vectorGraphicsFormat.toString().toLowerCase());

         try {
         file.write(g.getBytes());
         } finally {
         file.close();
         }*/
    }

}
