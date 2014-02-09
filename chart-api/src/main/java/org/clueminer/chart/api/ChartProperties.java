package org.clueminer.chart.api;

import java.awt.Color;
import java.awt.Stroke;
import org.clueminer.events.LogListener;
import org.w3c.dom.Element;

/**
 *
 * @author Tomas Barton
 */
public interface ChartProperties extends ElementProperties, PropertyListener {

    public boolean getMarkerVisibility();

    public void setMarkerVisibility(boolean b);

    public Color getBackgroundColor();

    public void setBackgroundColor(Color color);

    public AxisProperties getXAxis();

    public AxisProperties getYAxis();

    /*
     * Grid
     */
    public boolean getGridHorizontalVisibility();

    public void setGridHorizontalVisibility(boolean b);

    public Color getGridVerticalColor();

    public void setGridVerticalColor(Color color);

    public boolean getGridVerticalVisibility();

    public void setGridVerticalVisibility(boolean b);

    public boolean getToolbarVisibility();

    public void setToolbarVisibility(boolean b);

    public boolean getToolbarShowLabels();

    public void toggleToolbarVisibility();

    public boolean getToolbarSmallIcons();

    public void setToolbarSmallIcons(boolean b);

    public Stroke getGridVerticalStroke();

    public Color getGridHorizontalColor();

    public void setGridHorizontalColor(Color color);

    public int getGridHorizontalStrokeIndex();

    public Stroke getGridHorizontalStroke();

    public void loadFromTemplate(Element element);

    public void toggleToolbarSmallIcons();

    public void toggleShowLabels();

    public void addLogListener(LogListener l);

}
