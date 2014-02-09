package org.clueminer.chart.api;

import java.awt.Stroke;

/**
 *
 * @author Tomas Barton
 */
public interface AxisProperties extends ElementProperties {

    public int getStrokeIndex();

    public void setStrokeIndex(int i);

    public Stroke getStroke();

    public void setStroke(Stroke s);

    public boolean isLogarithmic();

    public void setLogarithmic(boolean b);

    public double getMajorTick();

    public double getMinorTick();

    public double getPadding();

}
