/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.chart;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.EventListenerList;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.ChartData;
import org.clueminer.chart.api.ChartListener;
import org.clueminer.chart.api.ChartProperties;
import org.clueminer.chart.api.Overlay;
import org.clueminer.chart.api.Range;
import org.clueminer.chart.api.Tracker;
import org.clueminer.chart.base.ChartPropertiesImpl;
import org.clueminer.chart.renderer.Line;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.DataType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.impl.TimeseriesDataset;
import org.clueminer.events.DatasetEvent;
import org.clueminer.events.DatasetListener;
import org.openide.util.lookup.ServiceProvider;

/**
 * Component should be used for simple preview of data, optimized for displaying
 * just one instance
 *
 * @author Tomas Barton
 * @param <E> type of base data type
 */
@ServiceProvider(service = Plotter.class)
public class PreviewFrame<E extends Instance> extends JPanel implements ChartConfig, DatasetListener, Serializable, Plotter<E> {

    private static final long serialVersionUID = 6847417134740120657L;
    private ChartDataImpl chartData = null;
    private ChartProperties chartProperties = null;
    private PreviewPanel previewPanel;
    private Timeseries dataset;
    private Timeseries visible;

    public PreviewFrame() {
        initComponents();
    }

    private synchronized void initComponents() {
        setLayout(new BorderLayout());
        setSize(400, 400);

        if (chartProperties == null) {
            chartProperties = new ChartPropertiesImpl();
        }

        previewPanel = new PreviewPanel(this);
        add(previewPanel, BorderLayout.CENTER);

        //this.addComponentListener(new ChartFrame.ChartFrameComponentListener());
        revalidate();
    }

    @Override
    public void datasetChanged(DatasetEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void datasetOpened(DatasetEvent evt) {
        System.out.println("preview panel: dataset opened");
    }

    @Override
    public void datasetClosed(DatasetEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void datasetCropped(DatasetEvent evt) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChartData getChartData() {
        return chartData;
    }

    @Override
    public ChartProperties getChartProperties() {
        return chartProperties;
    }

    @Override
    public JComponent getChartPanel() {
        return previewPanel;
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

    @Override
    public Range getRange() {
        return chartData.getVisibleRange();
    }

    @Override
    public void paint(Graphics g) {
        previewPanel.paint(g);

    }

    /*
     @Override
     public void clusterSelected(DendrogramTree source, TreeCluster cluster, DendrogramMapping data) {
     if (dataset != null) {
     if (cluster.firstElem > -1) {
     chartData.clearVisible();
     //System.out.println("cluster selected: " + cluster.toString());
     Timeseries ts = new TimeseriesDataset(dataset.size());
     ts.setTimePoints(dataset.getTimePoints());

     for (int i = cluster.firstElem; i <= cluster.lastElem; i++) { //
     //System.out.println("data row index: " + data.getRowIndex(i)); //
     //System.out.println(dataset.instance(data.getRowIndex(i)).getName());
     ts.add(dataset.instance(data.getRowIndex(i)));
     }
     chartData.setVisible(ts);
     previewPanel.repaint();
     }
     }
     }

     @Override
     public void treeUpdated(DendrogramTree source, int width, int height) {
     //not used not
     }*/
    @Override
    public void deselectAll() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Tracker getSplitPanel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JPopupMenu getMenu() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void zoomIn() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void zoomOut() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addInstance(E instance) {
        chartData.setVisible(visible);
        previewPanel.repaint();
    }

    @Override
    public void clearAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTitle(String title) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setXBounds(double min, double max) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setYBounds(double min, double max) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public void setDataset(Dataset<? extends Instance> dataset) {
        Timeseries<? extends ContinuousInstance> ts = (Timeseries<? extends ContinuousInstance>) dataset;
        chartData = new ChartDataImpl(ts);
        chartData.setChart(new Line());
        chartData.addDatasetListener(this);
        chartData.clearVisible();
        //System.out.println("cluster selected: " + cluster.toString());
        visible = new TimeseriesDataset(dataset.size());
        visible.setTimePoints(ts.getTimePoints());

        chartData.setVisible(ts);
        //System.out.println("visible data: " + dataset.size());
        previewPanel.revalidate();
    }

    @Override
    public void addOverlay(Overlay overlay) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void prepare(DataType type) {
        //
    }

    @Override
    public void addInstance(E instance, String clusterName) {
        addInstance(instance);
    }

    @Override
    public boolean isSupported(DataType type) {
        return type == DataType.TIMESERIES;
    }

    @Override
    public Collection<E> instanceAt(double[] coord, int maxK) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void focus(Collection<E> instance, MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
