package org.clueminer.chart.api;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 *
 * @author Tomas Barton
 */
public abstract class ChartConfig extends JPanel {

    private static final long serialVersionUID = -5267569701344658390L;

    public abstract ChartData getChartData();

    public abstract ChartProperties getChartProperties();

    public abstract JComponent getChartPanel();

    public abstract void addChartListener(ChartListener listener);

    /**
     * Visible range
     *
     * @return
     */
    public abstract Range getRange();

    public abstract void deselectAll();

    public abstract Tracker getSplitPanel();

    public abstract JPopupMenu getMenu();

    public abstract void zoomIn();

    public abstract void zoomOut();
}
