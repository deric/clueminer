package org.clueminer.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import org.clueminer.chart.api.ChartData;
import org.clueminer.timeseries.chart.SelectionEvent;
import org.clueminer.timeseries.chart.SelectionListener;

/**
 * Inspired by @link
 * http://stackoverflow.com/questions/1115359/how-to-draw-a-rectangle-on-a-java-applet-using-mouse-drag-event-and-make-it-stay
 *
 * @author Tomas Barton
 */
public class RectangleSelection extends JPanel implements MouseListener, MouseMotionListener {

    final static float dash1[] = {10.0f};
    final static BasicStroke dashed = new BasicStroke(1.0f,
                                                      BasicStroke.CAP_BUTT,
                                                      BasicStroke.JOIN_MITER,
                                                      10.0f, dash1, 0.0f);
    private static final long serialVersionUID = 1L;
    int x1, x2, y1 = 0, y2;
    int x, y, w, h;
    boolean isNewRect = true;
    ChartFrame chartFrame;

    public RectangleSelection(ChartFrame chartFrame) {
        setOpaque(false);
        addMouseListener((MouseListener) this); // listens for own mouse and
        addMouseMotionListener((MouseMotionListener) this); // mouse-motion events
        setVisible(true);
        this.chartFrame = chartFrame;
    }

    public ChartData getChartData() {
        return chartFrame.getChartData();
    }

    /**
     * Clears painted rectangle, if any
     */
    public void reset() {
        this.isNewRect = true;
        repaint();
    }

    /**
     * handle event when mouse released immediately after press
     *
     * @param event
     */
    @Override
    public void mouseClicked(final MouseEvent event) {
    }

    /**
     * handle event when mouse pressed
     *
     * @param event
     */
    @Override
    public void mousePressed(final MouseEvent event) {
        this.x1 = event.getX();
        this.y1 = event.getY();
        this.isNewRect = true;
        repaint();
    }

    /**
     * handle event when mouse released after dragging
     */
    @Override
    public void mouseReleased(final MouseEvent event) {
        this.x2 = event.getX();
        this.y2 = event.getY();
        repaint();
    }

    @Override
    public void mouseEntered(final MouseEvent event) {
    }

    @Override
    public void mouseExited(final MouseEvent event) {
    }

    /**
     * handle event when user drags mouse with button pressed
     *
     * @param event
     */
    @Override
    public void mouseDragged(final MouseEvent event) {
        this.x2 = event.getX();
        this.y2 = event.getY();
        this.isNewRect = false;
        repaint();
    }

    @Override
    public void mouseMoved(final MouseEvent event) {
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        int width = this.x1 - this.x2;
        int height = this.getSize().height;//this.y1 - this.y2;

        this.w = Math.abs(width);
        this.h = Math.abs(height);
        this.x = width < 0 ? this.x1
                : this.x2;
        this.y = height < 0 ? this.y1
                : this.y2;

        if (!this.isNewRect) {
            g2.setStroke(dashed);
            g2.setColor(Color.GRAY);
            g2.drawRect(this.x, 0, this.w, this.h);

            int start = chartFrame.getChartData().findIndex(x, chartFrame.getBounds());
            int end = chartFrame.getChartData().findIndex(x + w, chartFrame.getBounds());
            SelectionEvent evt = new SelectionEvent(this, start, end);
            fireAreaSelected(evt);
        }
    }
    private transient EventListenerList selectionListeners = new EventListenerList();

    public void addSelectionListener(SelectionListener listener) {
        if (selectionListeners == null) {
            selectionListeners = new EventListenerList();
        }
        selectionListeners.add(SelectionListener.class, listener);
    }

    public void removeOverlaysDatasetListeners(SelectionListener listener) {
        if (selectionListeners == null) {
            selectionListeners = new EventListenerList();
            return;
        }
        selectionListeners.remove(SelectionListener.class, listener);
    }

    public boolean fireAreaSelected(SelectionEvent evt) {
        SelectionListener[] listeners;

        if (selectionListeners != null) {
            listeners = selectionListeners.getListeners(SelectionListener.class);
            for (SelectionListener listener : listeners) {
                listener.areaSelected(evt);
            }
        }
        return true;
    }
}
