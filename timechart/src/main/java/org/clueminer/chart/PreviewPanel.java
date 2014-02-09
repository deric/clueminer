package org.clueminer.chart;

import java.awt.*;
import java.io.Serializable;
import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.ChartProperties;
import org.clueminer.timeseries.DateAxis;
import org.clueminer.timeseries.Grid;
import org.clueminer.timeseries.IndexAxis;

/**
 *
 * @author Tomas Barton
 */
public class PreviewPanel extends JLayeredPane implements Serializable {

    private static final long serialVersionUID = 6550621744163262992L;
    private Grid grid;
    private DateAxis dateAxis;
    private IndexAxis indexAxis;
    private final ChartConfig config;
    private ChartPanel panel;

    public PreviewPanel(ChartConfig config) {
        this.config = config;
        initializeComponents();
    }

    private void initializeComponents() {
        grid = new Grid(config);
        ChartProperties prop = config.getChartProperties();
        dateAxis = new DateAxis(config, prop.getXAxis());
        indexAxis = new IndexAxis(config, prop.getYAxis());
        panel = new ChartPanel(config);

        setOpaque(false);
        setBackground(config.getChartProperties().getBackgroundColor());
        setBorder(BorderFactory.createEmptyBorder(2, 20, 0, 0));
        setLayout(new LayoutManager() {

            @Override
            public void addLayoutComponent(String name, Component comp) {
            }

            @Override
            public void removeLayoutComponent(Component comp) {
            }

            @Override
            public Dimension preferredLayoutSize(Container parent) {
                return new Dimension(500, 500);
            }

            @Override
            public Dimension minimumLayoutSize(Container parent) {
                return new Dimension(400, 400);
            }

            @Override
            public void layoutContainer(Container parent) {
                int right = (int) ChartDataImpl.dataOffset.right;
                int bottom = (int) ChartDataImpl.dataOffset.bottom;
                Insets insets = parent.getInsets();
                int w = parent.getWidth() - insets.left - insets.right - right;
                int h = parent.getHeight() - insets.top - insets.bottom - bottom;

                grid.setBounds(insets.left, insets.top, w, h);
                panel.setBounds(new Rectangle(insets.left, insets.top, w, h));

                dateAxis.setBounds(insets.left, insets.top + h, w, bottom);
                indexAxis.setBounds(insets.left + insets.right + w, insets.top, right, insets.top + insets.bottom + h);
            }
        });

        add(dateAxis);
        add(indexAxis);
        add(grid);
        moveToBack(grid);
        add(panel);
        moveToFront(panel);

        putClientProperty("print.printable", Boolean.TRUE);
        putClientProperty("print.name", "");
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        setDoubleBuffered(true);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2.setPaintMode();

        if (config.getChartData() != null) {
            config.getChartData().calculateRange(config, null);
            config.getChartData().getChart().paint(g2, config);
            config.getChartData().updateLastX(getBounds());
            super.paint(g);
        }

        g2.dispose();
    }
}
