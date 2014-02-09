package org.clueminer.timeseries;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import javax.swing.JPanel;
import org.clueminer.chart.api.AxisProperties;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.ChartProperties;
import org.clueminer.chart.api.ChartData;

public class DateAxis extends JPanel {

    private static int mSecInHour = 3600000;
    private static final long serialVersionUID = -6590135689925740058L;
    private ChartConfig config;
    private AxisProperties axConf;

    public DateAxis(ChartConfig frame, AxisProperties prop) {
        config = frame;
        axConf = prop;
        setOpaque(false);
        setLayout(new BorderLayout());

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int width = getWidth();

        Graphics2D g2 = (Graphics2D) g.create();
        setDoubleBuffered(true);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2.setPaintMode();

        ChartData cd = config.getChartData();
        ChartProperties cp = config.getChartProperties();
        if (!cd.isVisibleNull() && !cd.getVisible().isEmpty()) {
            Rectangle bounds = config.getChartPanel().getBounds();
            bounds.grow(-2, -2);

            g2.setFont(axConf.getFont());
            g2.setPaint(axConf.getColor());
            g2.setStroke(axConf.getStroke());
            g2.drawLine(0, 0, width, 0);

            double axisTick = axConf.getMajorTick();
            double axisStick = axConf.getPadding();

            g.setFont(axConf.getFont());
            FontRenderContext frc = g2.getFontRenderContext();
            LineMetrics lm = axConf.getFont().getLineMetrics("0123456789/", g2.getFontRenderContext());

            long hourInMs = 60 * 60 * 1000;

            double chartWidth = bounds.getWidth();
            int start = cd.getStart();
            long startTime = cd.getTimeAt(start);

            int h = (int) (startTime / mSecInHour);
            long current = h * hourInMs;
            double prevX = 0, x = 0;
            while (x < chartWidth) {
                g2.setPaint(axConf.getColor());

                x = cd.getXFromTime(current, bounds);
                //    System.out.println("dx  "+x+ " i= "+h);
                g2.draw(new Line2D.Double(x, 0, x, axisTick));
                g2.setPaint(axConf.getFontColor());
                String s = Integer.toString(h);

                float w = (float) (axConf.getFont().getStringBounds(s, frc).getWidth());
                //some constant to keep hours in readable distance
                if ((x - prevX - w) > 1) {
                    g2.drawString(s, (float) (x - w / 2), (float) (axisTick + axisStick + lm.getAscent()));
                    prevX = x;
                }
                current += hourInMs;
                h++;
            }
        }
        g2.dispose();
    }
}
