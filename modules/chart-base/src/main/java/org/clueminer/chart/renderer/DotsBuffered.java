package org.clueminer.chart.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import org.clueminer.chart.api.ChartRenderer;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.ChartData;
import org.clueminer.chart.api.Range;
import org.clueminer.dataset.api.Instance;
import org.openide.util.lookup.ServiceProvider;

/**
 * Simple rendering of a ChartRenderer
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ChartRenderer.class)
public class DotsBuffered implements ChartRenderer {

    private static final long serialVersionUID = -7117225235898660411L;
    private BufferedImage bufferedImage;
    private Graphics2D buff;
    private int width = 4;
    private int height = 4;

    public DotsBuffered() {
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
    public String getName() {
        return "Dots (Buffered)";
    }

    @Override
    public void paint(Graphics2D g, ChartConfig cf) {
        ChartData cd = cf.getChartData();
        //ChartProperties cp = cf.getChartProperties();
        Rectangle rect = cf.getChartPanel().getBounds();
        rect.grow(-2, -2);
        Range range = cf.getRange();
        AffineTransform at = new AffineTransform();
        at.scale(1, 1);
        if (!cd.isVisibleNull()) {
            for (Instance sample : cd.getVisible()) {
                createBufferedImage(sample.getColor());
                for (int i = 0; i < sample.size(); i++) {
                    double value = sample.value(i);
                    double x = cd.getX(i, rect);
                    double y = cd.getY(value, rect, range);

                    //filling shapes seems to be very expensive operation
                    //this is probably the fastest way how to draw
                    at.setToIdentity();
                    at.translate(x - 2, y - 2);
                    g.drawImage(bufferedImage, at, null);
                }
            }
        }
    }
}
