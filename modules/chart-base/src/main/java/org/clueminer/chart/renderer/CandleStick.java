package org.clueminer.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import org.clueminer.chart.api.Chart;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.ChartProperties;
import org.clueminer.chart.api.ChartData;
import org.clueminer.chart.api.Range;
import org.clueminer.dataset.api.Instance;
import org.openide.util.lookup.ServiceProvider;

/**
 * @TODO this type of chart does not make much sense in this context
 * 
 * 
 * @author Tomas Barton
 */
@ServiceProvider(service = Chart.class)
public class CandleStick extends Chart {

    private static final long serialVersionUID = -6716317882350583308L;

    public CandleStick() {
    }

    @Override
    public String getName() {
        return "Candle Stick";
    }

    @Override
    public void paint(Graphics2D g, ChartConfig cf) {
        ChartData cd = cf.getChartData();
        ChartProperties cp = cf.getChartProperties();
        Rectangle rect = cf.getChartPanel().getBounds();
        rect.grow(-2, -2);
        Range range = cf.getRange();

        if (!cd.isVisibleNull()) {
            for (Instance sample : cd.getVisible()) {
                g.setPaint(sample.getColor());
                for (int i = 0; i < sample.size(); i++) {
                    double value = sample.value(i);
                 /*  double middle = sample.getMiddleAt(i);
                    
                    double high = sample.getHighAt(i);
                    double low = sample.getLowAt(i);*/

                    double x = cd.getX(i, rect);
            //        double yOpen = cd.getY(middle, rect, range);
                    double yClose = cd.getY(value, rect, range);
              /*      double yHigh = cd.getY(high, rect, range);
                    double yLow = cd.getY(low, rect, range);

                    double candleWidth = cp.getBarWidth();
                    double candleHeight = Math.abs(yOpen - yClose);

                /*    if (middle > value ? cp.getBarDownVisibility() : cp.getBarUpVisibility()) {
                        g.setPaint(middle > value ? cp.getBarDownColor() : cp.getBarUpColor());
                        g.fill(CoordCalc.rectangle(x - candleWidth / 2, (middle > value ? yOpen : yClose), candleWidth, candleHeight));
                    }

                    if (cp.getBarVisibility()) {

                        g.setStroke(cp.getBarStroke());
                        g.draw(CoordCalc.line(x, (middle > value ? yOpen : yClose), x, yHigh));
                        g.draw(CoordCalc.line(x, (middle > value ? yClose : yOpen), x, yLow));
                        g.draw(CoordCalc.rectangle(x - candleWidth / 2, (middle > value ? yOpen : yClose), candleWidth, candleHeight));
                    }*/
                }
            }
        }
    }
}
