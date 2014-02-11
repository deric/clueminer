package org.clueminer.chart.renderer;

import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.ChartProperties;
import org.clueminer.chart.api.Chart;
import org.clueminer.chart.api.ChartData;
import org.clueminer.chart.api.Range;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import org.clueminer.dataset.api.Instance;
import org.openide.util.lookup.ServiceProvider;

/**
 * Simple rendering of a Chart - line that connects all points
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Chart.class)
public class DottedLine extends Chart {

    private static final long serialVersionUID = -3348374944439589526L;
    private BufferedImage bufferedImage;
    private Graphics2D buff;
    private int width = 4;
    private int height = 4;

    public DottedLine() {
    }

    @Override
    public String getName() {
        return "Dotted Line";
    }

    private BufferedImage createBufferedImage(Color color) {
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        buff = bufferedImage.createGraphics();
        Ellipse2D.Double ellipse = new Ellipse2D.Double(0, 0, width, height);
        buff.setPaint(color);
        buff.draw(ellipse);
        buff.fill(ellipse);
        return bufferedImage;
    }

    @Override
    public void paint(Graphics2D g, ChartConfig cf) {
        ChartData cd = cf.getChartData();
        ChartProperties cp = cf.getChartProperties();
        Rectangle rect = cf.getChartPanel().getBounds();
        rect.grow(-2, -2);
        Range range = cf.getRange();
        double value;
        double x;
        int i = 0;
        int itemCnt = cd.getTimePointsCnt();
        //long start = System.currentTimeMillis();
        if (!cd.isVisibleNull()) {
            cd.setMaxY(Double.MIN_VALUE);
            cd.setMinY(Double.MAX_VALUE);
            AffineTransform at = new AffineTransform();
            at.scale(1, 1);
            for (Instance sample : cd.getVisible()) {
                Point2D.Double prev = null, current;
                g.setPaint(sample.getColor());
                createBufferedImage(sample.getColor());
                g.setColor(sample.getColor());
                while (i < itemCnt) {
                    x = cd.getX(i, rect);
                    value = sample.value(i);
                    cd.checkMax(value);
                    cd.checkMin(value);
                    double y = cd.getY(value, rect, range);
                    current = new Point2D.Double(x, y);
                    if (prev != null) {
                        g.draw(new Line2D.Double(prev, current));
                    }
                    prev = current;
                    //fast way of drawing the very same shape
                    at.setToIdentity();
                    at.translate(x - 2, y - 2);
                    g.drawImage(bufferedImage, at, null);
                    i++; // instance id
                }
            }
        }
        // long end = System.currentTimeMillis();
        // System.out.println("chart rendered in " + (end - start) + "ms");
    }
}
