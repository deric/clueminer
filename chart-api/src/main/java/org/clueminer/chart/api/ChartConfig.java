package org.clueminer.chart.api;

import java.awt.MenuContainer;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public interface ChartConfig extends ImageObserver, MenuContainer,Serializable {

    public ChartData getChartData();

    public void setDataset(Dataset<? extends Instance> dataset);

    public ChartProperties getChartProperties();

    public JComponent getChartPanel();

    public boolean hasData();

    public void addChartListener(ChartListener listener);

    /**
     * Visible range
     *
     * @return
     */
    public Range getRange();

    public void deselectAll();

    public Tracker getSplitPanel();

    public JPopupMenu getMenu();

    public void zoomIn();

    public void zoomOut();

    public void repaint();

    public void revalidate();

    public Rectangle getBounds();

}
