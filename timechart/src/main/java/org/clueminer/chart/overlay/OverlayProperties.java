package org.clueminer.chart.overlay;

import java.awt.Color;
import java.awt.Stroke;

/**
 *
 * @author Tomas Barton
 */
public class OverlayProperties {

    private Color color;
    private Stroke stroke;
    private int strokeIndex = 0;
    private boolean useMarker = true;

    public OverlayProperties() {

    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public int getStrokeIndex() {
        return strokeIndex;
    }

    public void setStrokeIndex(int strokeIndex) {
        this.strokeIndex = strokeIndex;
    }

    public boolean isUseMarker() {
        return useMarker;
    }

    public void setUseMarker(boolean useMarker) {
        this.useMarker = useMarker;
    }

}
