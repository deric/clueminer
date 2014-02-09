package org.clueminer.timeseries;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JPanel;
import org.clueminer.chart.api.AxisProperties;
import org.clueminer.timeseries.utils.CoordCalc;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.ChartProperties;
import org.clueminer.chart.api.ChartData;
import org.clueminer.chart.api.Range;

/**
 *
 * @author Tomas Barton
 */
public class IndexAxis extends JPanel {

    private static final long serialVersionUID = 6491767941539824369L;
    private ChartConfig chartConfig;
    private AxisProperties axConf;

    public IndexAxis(ChartConfig frame, AxisProperties prop) {
        chartConfig = frame;
        axConf = prop;
        setOpaque(false);
    }

    public @Override
    void paint(Graphics g) {
        super.paint(g);
        int height = getHeight();

        Graphics2D g2 = (Graphics2D) g.create();
        setDoubleBuffered(true);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2.setPaintMode();

        ChartData cd = chartConfig.getChartData();
        ChartProperties cp = chartConfig.getChartProperties();

        if (!cd.isVisibleNull() && !cd.getVisible().isEmpty()) {
            g2.setFont(axConf.getFont());
            g2.translate(0, 0);
            g2.setPaint(axConf.getColor());
            g2.setStroke(axConf.getStroke());
            g2.drawLine(0, 0, 0, height);

            // paint values for chart
            Rectangle chartBounds = chartConfig.getChartPanel().getBounds();
            chartBounds.grow(-2, -2);
            Range chartRange = chartConfig.getRange();

            List<Float> values = cd.getIndexValues(chartBounds, chartRange);
            double axisTick = axConf.getMajorTick();
            double axisStick = axConf.getPadding();
            double y;

            g.setFont(axConf.getFont());
            LineMetrics lm = axConf.getFont().getLineMetrics("0123456789", g2.getFontRenderContext());
            DecimalFormat df = new DecimalFormat("#,###.##");

            for (int i = 0; i < values.size(); i++) {
                double value = values.get(i);
                y = cd.getY(value, chartBounds, chartRange);
                if (chartBounds.contains(chartBounds.getCenterX(), y)) {
                    g2.setPaint(axConf.getColor());
                    g2.draw(CoordCalc.line(0, y, axisTick, y));
                    g2.setPaint(axConf.getFontColor());
                    g2.drawString(df.format(value), (float) (axisTick + axisStick), (float) (y + lm.getDescent()));
                }
            }

            // paint chart marker - now it's quite useless
           /*
             * double val = cd.getVisible().get(0).getFirstValue();
             *
             * y = cd.getY(val, chartBounds, chartRange);
             * IndexAxisMarker.paint(g2, chartFrame, val, cp.getBarUpColor(),
             * y);
             *
             * // paint overlays marker if
             * (chartFrame.getSplitPanel().getChartPanel().getOverlaysCount() >
             * 0) { for (Overlay overlay :
             * chartFrame.getSplitPanel().getChartPanel().getOverlays()) { if
             * (overlay.getMarkerVisibility()) { double[] ds =
             * overlay.getValues(chartFrame); if (ds.length > 0) { Color[] cs =
             * overlay.getColors(); for (int i = 0; i < ds.length; i++) { y =
             * cd.getY(ds[i], chartBounds, chartRange);
             * IndexAxisMarker.paint(g2, chartFrame, ds[i], cs[i], y); } } } }
             }
             */
            chartBounds.grow(2, 2);
            double hy = chartBounds.getHeight();

        }
        g2.dispose();
    }
}
