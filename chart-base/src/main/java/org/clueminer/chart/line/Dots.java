package org.clueminer.chart.line;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import org.clueminer.chart.api.Chart;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.ChartData;
import org.clueminer.chart.api.Range;
import org.clueminer.dataset.api.Instance;
import org.openide.util.lookup.ServiceProvider;

/**
 * Simple rendering of a Chart
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Chart.class)
public class Dots extends Chart {

    private static final long serialVersionUID = 1369577225005657430L;

    public Dots() {
    }

    @Override
    public String getName() {
        return "Dots";
    }

    @Override
    public void paint(Graphics2D g, ChartConfig cf) {
        ChartData cd = cf.getChartData();
        //ChartProperties cp = cf.getChartProperties();
        Rectangle rect = cf.getChartPanel().getBounds();
        rect.grow(-2, -2);
        Range range = cf.getRange();
        long start = System.currentTimeMillis();
        if (!cd.isVisibleNull()) {
            for (Instance sample : cd.getVisible()) {
                g.setPaint(sample.getColor());
                for (int i = 0; i < sample.size(); i++) {
                    double value = sample.value(i);
                    double x = cd.getX(i, rect);
                    double y = cd.getY(value, rect, range);

                    // double dotWidth = cp.getBarWidth() < 4.0d ? cp.getBarWidth() : 4.0d;
                    Ellipse2D.Double ellipse = new Ellipse2D.Double(x - 1.5, y - 1.5, 3, 3);
                    g.draw(ellipse);
                    g.fill(ellipse);
                    //g.draw(CoordCalc.rectangle(x - dotWidth / 2, yClose - dotHeight / 2, dotWidth, dotHeight));

                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("chart rendered in " + (end - start) + "ms");
    }
}
