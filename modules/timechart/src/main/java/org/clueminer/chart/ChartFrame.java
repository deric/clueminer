package org.clueminer.chart;

import org.clueminer.chart.api.ChartListener;
import org.clueminer.chart.api.ChartData;
import org.clueminer.chart.api.ChartProperties;
import org.clueminer.chart.api.Range;
import org.clueminer.chart.api.ChartConfig;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import org.clueminer.chart.api.Annotation;
import org.clueminer.chart.api.Overlay;
import org.clueminer.chart.base.ChartPropertiesImpl;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.events.DatasetEvent;
import org.clueminer.events.DatasetListener;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author Tomas Barton
 */
public class ChartFrame extends JPanel implements ChartConfig, AdjustmentListener, MouseWheelListener, DatasetListener, Serializable {

    public static final Logger LOG = Logger.getLogger(ChartFrame.class.getName());
    private static final long serialVersionUID = -232088291747540420L;
    private boolean restored = false;
    private Template template = null;
    private MainPanel mainPanel = null;
    private JScrollBar scrollBar = null;
    private ChartToolbar chartToolbar = null;
    private ChartDataImpl chartData = null;
    private ChartProperties chartProperties = null;
    private static int barMax = 100;
    private double mouseMultiplicator = 0;

    public ChartFrame(ChartDataImpl data) {
        setChartData(data);
        initComponents();
        addMouseWheelListener((MouseWheelListener) this);
    }

    private synchronized void initComponents() {
        setLayout(new BorderLayout());
        if (!restored) {
            if (chartProperties == null) {
                chartProperties = new ChartPropertiesImpl();
            }
        } else {
            chartProperties.setMarkerVisibility(true);
        }

        chartToolbar = new ChartToolbar(this);
        mainPanel = new MainPanel(this);
        scrollBar = initHorizontalScrollBar();

        add(chartToolbar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(scrollBar, BorderLayout.SOUTH);

        if (restored) {
            getSplitPanel().getChartPanel().setOverlays(chartData.getSavedOverlays());
            chartData.clearSavedOverlays();
            restoreAnnotations();
            chartData.clearAnnotations();
            chartData.clearAnnotationsCount();
        } else {
            if (template != null) {
                chartProperties = template.getChartProperties();
                getSplitPanel().getChartPanel().setOverlays(template.getOverlays());
            }
        }
        this.addComponentListener(new ChartFrameComponentListener());
        setRestored(false);
        revalidate();
        componentFocused();
    }

    private JScrollBar initHorizontalScrollBar() {
        JScrollBar bar = new JScrollBar(JScrollBar.HORIZONTAL);
        BoundedRangeModel model = bar.getModel();
        model.setMinimum(0);
        model.setMaximum(barMax);
        bar.setModel(model);
        bar.setUnitIncrement(5);
        bar.addAdjustmentListener((AdjustmentListener) this);
        return bar;
    }

    private int countScrollBarValue(double chartStart) {
        return (int) ((chartStart / chartData.getLastX()) * barMax);
    }

    public void updateHorizontalScrollBar() {
        double visible = (getBounds().width / chartData.getLastX());

        int start = 0;
        if (chartData.getStartX() > 0) {
            start = countScrollBarValue(chartData.getStartX());
        }
        int vis = (int) (visible * barMax);
        if (scrollBar.getModel().getExtent() != vis) {
            scrollBar.getModel().setExtent(vis);
        }
        if (scrollBar.getModel().getValue() != (start)) {
            scrollBar.getModel().setValue(start);
        }
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getValueIsAdjusting()) {
            double startX = e.getValue() * chartData.getLastX() / barMax;
            int startIndex = chartData.findAbsoluteIndex(startX, getBounds(), 0, chartData.getLast());

            //if (chartData.getStart() != startIndex) {
            updateHorizontalScrollBar();
            chartData.setStart(startIndex, getBounds());
            repaint();
            // }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!chartData.isSampleSetNull()) {
            double maxStart = chartData.getLastX() - getBounds().getWidth();
            double hypoStart = chartData.getStartX() + e.getUnitsToScroll() * mouseMultiplicator;
            //check if we can go further
            int startIndex;
            if (e.getWheelRotation() > 0) {
                //forward

                /*if (hypoStart < maxStart) {                    startIndex = chartData.findAbsoluteIndex(hypoStart, getBounds(), chartData.getStart(), chartData.getLast());
                 } else {
                 startIndex = chartData.findAbsoluteIndex(maxStart, getBounds(), chartData.getStart(), chartData.getLast());
                 }*/
            } else {
                //backwards
                /*if (hypoStart > 0) {                    startIndex = chartData.findAbsoluteIndex(hypoStart, getBounds(), 0, getChartData().getStart());
                 } else {
                 startIndex = 0;
                 }*/
            }
            /*   if (getChartData().getStart() != startIndex) {                chartData.setStart(startIndex, getBounds());
             updateHorizontalScrollBar();
             repaint();
             }*/
        }
    }

    public void componentFocused() {
        if (getMainPanel() != null) {
            if (getSplitPanel() != null) {
                getSplitPanel().getChartPanel().getAnnotationPanel().requestFocusInWindow();
            }
        }
    }

    public BufferedImage getBufferedImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g.setColor(chartProperties.getBackgroundColor());
        g.fillRect(0, 0, width, height);

        mainPanel.paintComponents(g);

        g.dispose();

        return image;
    }

    public boolean getRestored() {
        return restored;
    }

    public void setRestored(boolean b) {
        restored = b;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
        this.chartProperties = template.getChartProperties();

        // overlays
        getSplitPanel().getChartPanel().clearOverlays();
        chartData.removeAllOverlaysDatasetListeners();
        getSplitPanel().getChartPanel().setOverlays(template.getOverlays());

        revalidate();
        repaint();
    }

    @Override
    public ChartProperties getChartProperties() {
        return chartProperties;
    }

    public void setChartProperties(ChartProperties cp) {
        chartProperties = cp;
    }

    @Override
    public ChartData getChartData() {
        return chartData;
    }

    @Override
    public void setDataset(Dataset<? extends Instance> dataset) {
        getChartData().setDataset(dataset);
    }

    @Override
    public JComponent getChartPanel() {
        return getSplitPanel().getChartPanel();
    }

    @Override
    public Range getRange() {
        return chartData.getVisibleRange();
    }

    public final void setChartData(ChartDataImpl data) {
        chartData = data;
        chartData.addDatasetListener(this);
        addChartListener(data);
    }

    public MainPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public ChartSplitPanel getSplitPanel() {
        if (mainPanel != null) {
            return mainPanel.getSplitPanel();
        }
        return null;
    }

    public AbstractNode getNode() {
        return new ChartNode(chartProperties);
    }

    @Override
    public void deselectAll() {
        getSplitPanel().getChartPanel().getAnnotationPanel().deselectAll();

    }

    public boolean hasCurrentAnnotation() {
        return getSplitPanel().getChartPanel().getAnnotationPanel().hasCurrent();
    }

    public AnnotationImpl getCurrentAnnotation() {
        if (getSplitPanel().getChartPanel().getAnnotationPanel().hasCurrent()) {
            return getSplitPanel().getChartPanel().getAnnotationPanel().getCurrent();
        }

        return null;
    }

    public void removeAllAnnotations() {
        getSplitPanel().getChartPanel().getAnnotationPanel().removeAllAnnotations();

    }

    public List<Integer> getAnnotationCount() {
        List<Integer> list = new ArrayList<Integer>();
        int i = 0;
        i += getSplitPanel().getChartPanel().getAnnotationPanel().getAnnotations().length;
        list.add(new Integer(i));
        return list;
    }

    public List<AnnotationImpl> getAnnotations() {
        List<AnnotationImpl> list = new ArrayList<AnnotationImpl>();
        list.addAll(getSplitPanel().getChartPanel().getAnnotationPanel().getAnnotationsList());
        return list;
    }

    public synchronized void restoreAnnotations() {
        List<Integer> count = chartData.getAnnotationsCount();
        List<Annotation> annotations = chartData.getAnnotations();

        for (int i = 0; i < count.size(); i++) {
            if (i == 0) // chart panel annotations
            {
                List<Annotation> newList = annotations.subList(0, count.get(i));
                getSplitPanel().getChartPanel().getAnnotationPanel().setAnnotationsList(newList);
            }
        }
    }

    @Override
    public JPopupMenu getMenu() {
        JPopupMenu popup = new JPopupMenu();
        popup.add(MainActions.generateChartsMenu(this)); // change chart
   /*     popup.add(new JMenuItem(MainActions.openIndicators(this))); // add indicators
         popup.add(new JMenuItem(MainActions.openOverlays(this))); // add overlays
         popup.add(MainActions.generateAnnotationsMenu(this)); // add annotation
         popup.add(new JMenuItem(MainActions.exportImage(this))); // export image
         popup.add(new JMenuItem(MainActions.printChart(this))); // print
         popup.add(new JMenuItem(MainActions.chartProperties(this))); // chart settings
         popup.add(new JMenuItem(MainActions.toggleToolbarVisibility(this))); // hide/show toolbar
         if (!MainActions.isInFavorites(this))
         {
         popup.add(new JMenuItem(MainActions.addToFavorites(this))); // add to favorites
         }*/
        //  popup.add(MainActions.generateTemplatesMenu(this)); // save to template
        return popup;
    }

    @Override
    public void zoomIn() {
        ChartListener[] listeners = listenerList().getListeners(ChartListener.class);
        for (ChartListener listener : listeners) {
            double zoom = listener.zoomIn(getBounds());
            updateProportions();
            System.out.println("rendering chart, zoom in: " + zoom);
            repaint();
        }
    }

    @Override
    public void zoomOut() {
        ChartListener[] listeners = listenerList().getListeners(ChartListener.class);
        for (ChartListener listener : listeners) {
            double zoom = listener.zoomOut(getBounds());
            updateProportions();
            System.out.println("rendering chart, zoom out: " + zoom);
            repaint();
        }
    }

    public void setZoom(double z) {
        chartData.setZoom(z);
        resetRenderedData();
        updateProportions();
    }

    public void setToolbarVisibility() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private transient EventListenerList chartListeners;

    private EventListenerList listenerList() {
        if (chartListeners == null) {
            chartListeners = new EventListenerList();
        }
        return chartListeners;
    }

    @Override
    public void addChartListener(ChartListener listener) {
        listenerList().add(ChartListener.class, listener);
    }

    public void removeChartListener(ChartListener listener) {
        listenerList().remove(ChartListener.class, listener);
    }

    public void updateMouseMultiplicator() {
        double diff, avg;
        if (chartData.getLastX() > 0) {
            diff = chartData.getX(1, getBounds()) - chartData.getX(0, getBounds());
            avg = chartData.getLastX() / chartData.getTimePointsCnt();
            if (diff > avg) {
                mouseMultiplicator = diff * 1.5;
            } else {
                mouseMultiplicator = avg * 1.5;
            }
        } else {
            mouseMultiplicator = getSize().width / 10;
        }
        System.out.println("mulplt " + mouseMultiplicator);
    }

    @Override
    public void datasetChanged(DatasetEvent evt) {
        repaint();
    }

    @Override
    public void datasetOpened(DatasetEvent evt) {
        System.out.println("dataset opened");
    }

    @Override
    public void datasetClosed(DatasetEvent evt) {
    }

    public void updateProportions() {
        updateMouseMultiplicator();
        updateHorizontalScrollBar();
        repaint();
    }

    public void resetRenderedData() {
        chartData.setStart(0, getBounds());
        repaint();
    }

    @Override
    public void datasetCropped(DatasetEvent evt) {
        resetRenderedData();
    }

    @Override
    public boolean hasData() {
        if (chartData != null) {
            if (chartData.getVisible() != null) {
                return chartData.getVisible().size() > 0;
            }
            return false;
        }
        return false;
    }

    @Override
    public void addOverlay(Overlay overlay) {
        List<Overlay> overlays = getSplitPanel().getChartPanel().getOverlays();
        overlays.add(overlay);
        getSplitPanel().getChartPanel().setOverlays(overlays);
    }

    private class ChartFrameComponentListener implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent ce) {
            resetRenderedData();
        }

        @Override
        public void componentMoved(ComponentEvent ce) {
        }

        @Override
        public void componentShown(ComponentEvent ce) {
            System.out.println("component chart frame shown");
            updateHorizontalScrollBar();
            updateMouseMultiplicator();
        }

        @Override
        public void componentHidden(ComponentEvent ce) {
        }
    }
}
