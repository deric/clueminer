package org.clueminer.chart;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import org.clueminer.chart.api.ChartProperties;
import org.clueminer.timeseries.DateAxis;
import org.clueminer.timeseries.Grid;
import org.clueminer.timeseries.IndexAxis;
import org.clueminer.timeseries.chart.NormalizationEvent;
import org.clueminer.timeseries.chart.NormalizationListener;
import org.clueminer.timeseries.chart.SelectionEvent;
import org.clueminer.timeseries.chart.SelectionListener;
import org.clueminer.utils.Exportable;

/**
 *
 * @author Tomas Barton
 */
public class MainPanel extends JLayeredPane implements Serializable, SelectionListener, NormalizationListener, Exportable {

    private static final long serialVersionUID = -7306181675217682289L;
    private ChartFrame chartFrame;
    private ChartSplitPanel sPane;
    private Grid grid;
    private DateAxis dateAxis;
    private IndexAxis indexAxis;
    private RectangleSelection selection;

    public MainPanel(ChartFrame frame) {
        chartFrame = frame;
        sPane = new ChartSplitPanel(chartFrame);
        grid = new Grid(chartFrame);
        ChartProperties prop = chartFrame.getChartProperties();
        dateAxis = new DateAxis(chartFrame, prop.getXAxis());
        indexAxis = new IndexAxis(chartFrame, prop.getYAxis());
        selection = new RectangleSelection(chartFrame);

        setOpaque(true);
        setBackground(prop.getBackgroundColor());
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
                return new Dimension(0, 0);
            }

            @Override
            public Dimension minimumLayoutSize(Container parent) {
                return new Dimension(0, 0);
            }

            @Override
            public void layoutContainer(Container parent) {
                Insets offset = chartFrame.getChartData().getDataInsets();
                int right = offset.right;
                int bottom = offset.bottom;
                Insets insets = parent.getInsets();
                int w = parent.getWidth() - insets.left - insets.right - right;
                int h = parent.getHeight() - insets.top - insets.bottom - bottom;

                grid.setBounds(insets.left, insets.top, w, h);
                dateAxis.setBounds(insets.left, insets.top + h, w, bottom);
                indexAxis.setBounds(insets.left + insets.right + w, insets.top, right, insets.top + insets.bottom + h);
                sPane.setBounds(insets.left, insets.top, w, h);
                selection.setBounds(insets.left, insets.top, w, h);
            }
        });

        add(sPane);
        add(dateAxis);
        add(indexAxis);
        add(grid);
        add(selection);

        selection.addSelectionListener((SelectionListener) this);
        //moveToBack(selection);
        moveToFront(selection);
        selection.reset();
        ChartDataImpl cd = (ChartDataImpl) chartFrame.getChartData();
        cd.addNormalizationListener(this);
        putClientProperty("print.printable", Boolean.TRUE);
        putClientProperty("print.name", "");
    }

    @Override
    public String getName() {
        return chartFrame.getChartData().getName();
    }

    public ChartSplitPanel getSplitPanel() {
        return sPane;
    }

    public RectangleSelection getRectangleSelection() {
        return selection;
    }

    @Override
    public void paint(Graphics g) {
        chartFrame.getChartData().calculateRange(chartFrame, sPane.getChartPanel().getOverlays());
        setBackground(chartFrame.getChartProperties().getBackgroundColor());
        super.paint(g);
    }

    @Override
    public void areaSelected(SelectionEvent evt) {
    }

    @Override
    public void markerMoved(NormalizationEvent evt) {
    }

    @Override
    public void normalizationCompleted() {
        moveToFront(selection);
        selection.reset();
    }

    @Override
    public BufferedImage getBufferedImage(int w, int h) {
        return chartFrame.getBufferedImage(h, h);
    }
}
