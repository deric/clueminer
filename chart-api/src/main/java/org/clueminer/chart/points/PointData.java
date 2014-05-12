package org.clueminer.chart.points;

import java.util.Collections;
import java.util.List;
import org.clueminer.chart.api.Axis;
import org.clueminer.chart.api.AxisRenderer;
import org.clueminer.chart.data.Row;

/**
 * Class for storing data that will be used to create a data point in a plot.
 */
public class PointData {

    /**
     * Axes that will be used to project the point.
     */
    public final List<Axis> axes;
    /**
     * Renderers for the axes that will be used to project the point.
     */
    public final List<? extends AxisRenderer> axisRenderers;
    /**
     * The data row that will get projected.
     */
    public final Row row;
    /**
     * The index of the column in the row that contains the data value.
     */
    public final int col;

    /**
     * Initializes a new instance with the specified data.
     *
     * @param axes          Axes that are used to project the point.
     * @param axisRenderers Renderers for the axes.
     * @param row           Data row containing that will be projected on the
     *                      axes.
     * @param col           Index of the column in the row that contains the
     *                      data value.
     */
    public PointData(List<Axis> axes, List<? extends AxisRenderer> axisRenderers,
            Row row, int col) {
        this.axes = Collections.unmodifiableList(axes);
        this.axisRenderers = Collections.unmodifiableList(axisRenderers);
        this.row = row;
        this.col = col;
    }
}
