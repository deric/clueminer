package org.clueminer.chart;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.GeneralPath;
import java.text.DecimalFormat;

/**
 *
 * @author Tomas Barton
 */
public class IndexAxisMarker {

    private IndexAxisMarker() {
    }

    public static void paint(Graphics2D g, ChartFrame cf, double value, Color color, double y) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        if (value < 10f) {
            df = new DecimalFormat("#,##0.00000");
        }
        Insets dataOffset = cf.getChartData().getDataInsets();
        FontMetrics fm = g.getFontMetrics();

        g.setPaint(color);
        double x = 1;
        double w = dataOffset.right - 6;
        double h = fm.getHeight() + 4;

        GeneralPath gp = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 5);
        gp.moveTo(x, y);
        gp.lineTo((x + 6), (y - h / 2));
        gp.lineTo((x + w + 8), (y - h / 2));
        gp.lineTo((x + w + 8), (y + h / 2));
        gp.lineTo((x + 6), (y + h / 2));
        gp.closePath();
        g.fill(gp);

        g.setPaint(new Color(0xffffff));
        g.drawString(df.format(value), (float) (x + 6 + 1), (float) (y + fm.getDescent()));
    }
}
