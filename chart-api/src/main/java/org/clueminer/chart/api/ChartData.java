package org.clueminer.chart.api;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.events.DatasetListener;

/**
 *
 * @author Tomas Barton
 */
public interface ChartData {

    public String getName();

    public void setName(String name);

    public Timeseries<? extends ContinuousInstance> getVisible();

    /**
     * Algorithm for rendering a chart
     *
     * @return
     */
    public Chart getChart();

    public void setChart(Chart chart);
    
    public boolean isChartNull();

    public boolean isVisibleNull();

    public double getX(int index, Rectangle rect);

    public double getY(double value, Rectangle rect, Range range);

    public double getY(double value, Rectangle bounds, Range range, boolean axisLogarithmicFlag);

    public int getTimePointsCnt();

    /**
     * Index of first point
     *
     * @return
     */
    public int getStart();

    public void setMin(double min);

    public void setMax(double max);

    public void checkMax(double value);

    public void checkMin(double value);

    public int getFinish();

    public void setFinish(int index, Rectangle rect);

    /**
     *
     * @param index
     * @param x
     * @deprecated we should buffer all charts
     */
    public void setFinish(int index, double x);

    /**
     * @deprecated @param bounds
     */
    public void updateLastX(Rectangle bounds);

    public double getXFromTime(long time, Rectangle bounds);

    public int getLast();

    public double getXFromRatio(double ratio, Rectangle rect);

    public long getTimeFromX(double x, Rectangle rect);

    public void calculateRange(ChartConfig chartConfig, List<Overlay> overlays);

    public long getTimeAt(int index);

    public List<Integer> getDateValues();

    public List<Float> getIndexValues(Rectangle rect, Range range);

    public Point2D getPoint(int i, double value1, Range range, Rectangle bounds, boolean isLog);

    public void addDatasetListener(DatasetListener listener);

    /**
     * Converts position in graph to time points index, is restricted by
     * currently rendered interval
     *
     * @param x
     * @param rect
     * @return
     */
    public int findIndex(double x, Rectangle rect);
    
    public int getIndex(Point p, Rectangle rect);
    
    public Insets getDataInsets();
}
