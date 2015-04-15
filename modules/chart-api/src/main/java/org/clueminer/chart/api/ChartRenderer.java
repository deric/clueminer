package org.clueminer.chart.api;

import java.awt.Graphics2D;
import java.io.Serializable;

/**
 *
 * @author Tomas Barton
 */
public interface ChartRenderer extends Serializable {

    String getName();

    void paint(Graphics2D g, ChartConfig cf);

}
