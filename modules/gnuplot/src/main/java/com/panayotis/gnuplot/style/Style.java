package com.panayotis.gnuplot.style;

/**
 * Possible styles to use in gnuplot
 * @author teras
 */
public enum Style {
    LINES (2, false),
    POINTS (2, false),
    LINESPOINTS (2, false),
    IMPULSES (2, false),
    DOTS (2, false),
    STEPS (2, false),
    FSTEPS (2, false),
    HISTEPS (2, false),
    ERRORBARS (2, false),
    LABELS (2, false),
    XERRORBARS (2, false),
    YERRORBARS (2, false),
    XYERRORBARS (2, false),
    ERRORLINES (2, false),
    XERRORLINES (2, false),
    YERRORLINES (2, false),
    XYERRORLINES (2, false),
    BOXES (2, true),
    HISTOGRAMS (2, false),
    FILLEDCURVES (2, false),
    BOXERRORBARS (2, false),
    BOXXYERRORBARS (2, true),
    FINANCEBARS (2, false),
    CANDLESTICKS (2, true),
    VECTORS (2, false),
    IMAGE (2, false),
    RGBIMAGE (2, false),
    PM3D (2, false);
    
    final int columns;   // number of desired columns
    final boolean filled; // could be filled
    
    /**
     * Create a new Style enumeration
     * @param columns how many dimensions is required
     * @param filled whether can be filled or not
     */
    Style(int columns, boolean filled) {
        this.columns = columns;
        this.filled = filled;
    }
}
