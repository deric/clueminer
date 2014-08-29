package org.clueminer.timeseries.utils;

import java.awt.geom.Rectangle2D;

/**
 *
 * @author Tomas Barton
 */
public class Bounds extends Rectangle2D.Double {

    private static final long serialVersionUID = 2850356702726001960L;

    public Bounds(double x, double y, double width, double height) {
        super(x, y, width, height);
    }
}