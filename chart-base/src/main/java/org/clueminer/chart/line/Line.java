package org.clueminer.chart.line;

import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.ChartProperties;
import org.clueminer.chart.api.Chart;
import org.clueminer.chart.api.ChartData;
import org.clueminer.chart.api.Range;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import org.clueminer.dataset.api.Instance;
import org.openide.util.lookup.ServiceProvider;

/**
 * Simple rendering of a Chart - line that connects all points
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Chart.class)
public class Line extends Chart {

    private static final long serialVersionUID = -3078916604543993282L;

    public Line() {
    }

    @Override
    public String getName() {
        return "Line";
    }

    @Override
    public void paint(Graphics2D g, ChartConfig cf) {
        ChartData cd = cf.getChartData();
        //System.out.println("chart data size= " + cd.getTimePointsCnt());
        ChartProperties cp = cf.getChartProperties();
        Rectangle rect = cf.getChartPanel().getBounds();
        rect.grow(-2, -2);
        Range range = cf.getRange();
        //System.out.println("rectangle "+rect);
        //System.out.println("range "+range);
        cd.updateLastX(rect);
        double x = 0, y, value, yPrev, xPrev;
        if (!cd.isVisibleNull()) {
            //long start = System.currentTimeMillis();
            cd.setMin(Double.MAX_VALUE);
            cd.setMax(Double.MIN_VALUE);
            double chartWidth = rect.getWidth();
            int i = 0;
            int itemCnt = cd.getTimePointsCnt();
            //  System.out.println("items in dataset "+itemCnt);
            //  g.drawString("items in dataset "+itemCnt, 20, 20);
            //  System.out.println("canvas size "+g.getClipBounds());
            for (Instance sample : cd.getVisible()) {
                if (!sample.isEmpty()) {
                    //   System.out.println("rendering sample "+sample.getName());
                    yPrev = 0;
                    xPrev = Double.NaN;
                    g.setPaint(sample.getColor());
                    i = cd.getStart();
                    x = 0;
                    while (x < chartWidth && i < itemCnt) {
                        value = sample.value(i);
                        cd.checkMax(value);
                        cd.checkMin(value);
                        x = cd.getX(i, rect);
                        ///  System.out.println("lx  "+x+ " i= "+i);

                        y = cd.getY(value, rect, range);
                        if (xPrev != Double.NaN) {
                            g.draw(new Line2D.Double(x, y, xPrev, yPrev));
                        }
                        // System.out.println("x= "+x+ " y= "+y);
                        xPrev = x;
                        yPrev = y;
                        i++;
                    }
                }
            }
            cd.setFinish(i, x); //last rendered point
            //long end = System.currentTimeMillis();
            //System.out.println("line chart rendered in " + (end - start) + "ms last index " + i);
        }
    }
}
