package org.clueminer.timeseries;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JPanel;
import org.clueminer.timeseries.utils.CoordCalc;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.ChartProperties;
import org.clueminer.chart.api.ChartData;
import org.clueminer.chart.api.Range;

/**
 *
 * @author Tomas Barton
 */
public class Grid extends JPanel {
    private static final long serialVersionUID = 5205435055211152104L;

    private ChartConfig chartConfig;

    public Grid(ChartConfig frame)
    {
        chartConfig = frame;
        setOpaque(false);
    }
    
    @Override
    public synchronized void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g.create();
        setDoubleBuffered(true);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2.setPaintMode();
        
        ChartData cd = chartConfig.getChartData();
        ChartProperties cp = chartConfig.getChartProperties();
        if (!cd.isVisibleNull() && !cd.getVisible().isEmpty())
        {
            Rectangle chartBounds = chartConfig.getChartPanel().getBounds();
            //System.out.println("grid bounds "+chartBounds);
            chartBounds.grow(-2, -2);
            Range chartRange = chartConfig.getRange();
            double x, y;

            // Vertical Grid
            if (cp.getGridVerticalVisibility())
            {
                g2.setPaint(cp.getGridVerticalColor());
                g2.setStroke(cp.getGridVerticalStroke());
                List<Integer> list = cd.getDateValues();
                for (int i = 0; i < list.size(); i++)
                {
                    x = cd.getX(list.get(i), chartBounds);
                  /*  System.out.println("grid x "+x+" for "
                            +list.get(i));*/
                    g2.draw(CoordCalc.line(x, 0, x, getHeight()));
                }
            }

            // Horizontal Grid
            if (cp.getGridHorizontalVisibility())
            {
                // paint grid for chart
                g2.setPaint(cp.getGridHorizontalColor());
                g2.setStroke(cp.getGridHorizontalStroke());
                List<Float> list = cd.getIndexValues(chartBounds, chartRange);
                for (int i = 0; i < list.size(); i++)
                {
                    y = cd.getY(list.get(i), chartBounds, chartRange);
                    if (chartBounds.contains(2, y))
                    {
                        g2.draw(CoordCalc.line(0, y, getWidth(), y));
                    }
                }

                chartBounds.grow(2, 2);
            }
        }
        g2.dispose();
    }

}
