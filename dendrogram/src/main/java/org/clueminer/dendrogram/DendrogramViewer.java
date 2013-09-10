package org.clueminer.dendrogram;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.EventListenerList;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.TreeCluster;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.dendrogram.events.DendrogramDataEvent;
import org.clueminer.dendrogram.events.DendrogramDataListener;
import org.clueminer.dendrogram.gui.DendrogramPanel;
import org.clueminer.utils.Exportable;

/**
 *
 * @author Tomas Barton
 */
public class DendrogramViewer extends JPanel implements Exportable, AdjustmentListener {

    private static final long serialVersionUID = -9145028094444482028L;
    protected ArrayList<TreeCluster> clusters;
    protected DendrogramPanel dendrogramPanel;
    private JScrollPane scroller;
    protected Dimension elementSize;
    protected DendrogramData data;
    private boolean fitToPanel = true;
    private transient EventListenerList datasetListeners = new EventListenerList();
    private transient EventListenerList clusteringListeners = new EventListenerList();

    public DendrogramViewer() {
        setBackground(Color.WHITE);
        elementSize = new Dimension(15, 15);
        initComponents();
        this.setDoubleBuffered(true);
    }

    public void setDataset(DendrogramData dataset) {
        this.data = dataset;
        fireDatasetChanged(new DendrogramDataEvent(this));
        updateLayout();
    }

    public DendrogramData getDendrogramData() {
        return this.data;
    }

    public void setClusters(ArrayList<TreeCluster> clusters) {
        this.clusters = clusters;
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        dendrogramPanel = new DendrogramPanel(this);
        // header = new ExperimentHeader();
        // add(header);
        //    colorBar.setClusters(clusters); //
        // add(colorBar);
        scroller = new JScrollPane();
        scroller.getViewport().add(dendrogramPanel);
        scroller.getViewport().setDoubleBuffered(true);
        scroller.setVisible(true);
        scroller.getVerticalScrollBar().addAdjustmentListener(this);
        setVisible(true);
        this.addComponentListener(new ViewerComponentListener(this));
        this.addDendrogramDataListener(dendrogramPanel);
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(scroller, c);
    }

    private void updateLayout() {
        int heatmapSize = this.getSize().width - dendrogramPanel.getVerticalTreeSize().width - dendrogramPanel.getAnnotationWidth();
        System.out.println("annotation panel = " + dendrogramPanel.getAnnotationWidth());
        
        System.out.println("vertical tree = " + dendrogramPanel.getVerticalTreeSize().width);
        System.out.println("heatmap "+dendrogramPanel.getHeatmapSize());
        System.out.println("clustering view size" + this.getSize() + " heatmap size= " + heatmapSize);

        //System.out.println("feature length = "+featuresLenght+ " heat map width = "+dendrogramPanel.getHeatmapWidth() );
        //dendrogramPanel.getHeatmapWidth() / featuresLenght;///data.getFeaturesSize();
        System.out.println("new element size " + elementSize);
        //setMinimumSize(dendrogramPanel.getSize());
        //update size of scrollbars
        scroller.getViewport().revalidate();
        repaint();
    }

    public void setCellHeight(int height, boolean isAdjusting) {
        if (height > 0) {
            elementSize.height = height;
            fireCellHeightChanged(new DendrogramDataEvent(this), height, isAdjusting);
            updateLayout();
        }
    }

    public void setFitToPanel(boolean fitToPanel) {
        this.fitToPanel = fitToPanel;
        updateLayout();
    }

    public boolean isFitToPanel() {
        return fitToPanel;
    }

    public int getCellHeight() {
        return elementSize.height;
    }

    public void setCellWidth(int width, boolean isAdjusting) {
        if (width > 0) {
            elementSize.width = width;
            fireCellWidthChanged(new DendrogramDataEvent(this), width, isAdjusting);
            updateLayout();
        }
    }

    public int getCellWidth() {
        return elementSize.width;
    }

    public void setHorizontalTreeVisible(boolean show) {
        dendrogramPanel.setHorizontalTreeVisible(show);
    }

    public boolean isHorizontalTreeVisible() {
        return dendrogramPanel.isHorizontalTreeVisible();
    }

    public void setVerticalTreeVisible(boolean show) {
        dendrogramPanel.setVerticalTreeVisible(show);
    }

    public boolean isVerticalTreeVisible() {
        return dendrogramPanel.isVerticalTreeVisible();
    }

    public void setLegendVisible(boolean show) {
        dendrogramPanel.setLegendVisible(show);
    }

    public boolean isLegendVisible() {
        return dendrogramPanel.isLegendVisible();
    }

    public void setLabelsVisible(boolean show) {
        dendrogramPanel.setLabelsVisible(show);
    }

    public boolean isLabelVisible() {
        return dendrogramPanel.isLabelVisible();
    }

    public boolean fireDatasetChanged(DendrogramDataEvent evt) {
        DendrogramDataListener[] listeners;

        if (datasetListeners != null) {
            listeners = datasetListeners.getListeners(DendrogramDataListener.class);
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].datasetChanged(evt, data);
            }
        }
        return true;
    }

    public boolean fireCellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        DendrogramDataListener[] listeners;

        if (datasetListeners != null) {
            listeners = datasetListeners.getListeners(DendrogramDataListener.class);
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].cellWidthChanged(evt, width, isAdjusting);
            }
        }
        return true;
    }

    protected boolean fireCellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        DendrogramDataListener[] listeners;

        if (datasetListeners != null) {
            listeners = datasetListeners.getListeners(DendrogramDataListener.class);
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].cellHeightChanged(evt, height, isAdjusting);
            }
        }
        return true;
    }

    public void addDendrogramDataListener(DendrogramDataListener listener) {
        datasetListeners.add(DendrogramDataListener.class, listener);
    }

    public void removeDendrogramDataListener(DendrogramDataListener listener) {
        datasetListeners.remove(DendrogramDataListener.class, listener);
    }

    public void addRowsTreeListener(TreeListener listener) {
        dendrogramPanel.addRowsTreeListener(listener);
    }

    public void addClusteringListener(ClusteringListener listener) {
        clusteringListeners.add(ClusteringListener.class, listener);
    }

    public void removeClusteringListener(ClusteringListener listener) {
        clusteringListeners.remove(ClusteringListener.class, listener);
    }

    public void fireClusteringChanged(Clustering clust) {
        ClusteringListener[] listeners;

        listeners = clusteringListeners.getListeners(ClusteringListener.class);
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].clusteringChanged(clust);
        }
    }

    public Dimension getElementSize() {
        return elementSize;
    }

    public void setElementSize(Dimension elementSize) {
        this.elementSize = elementSize;
    }
    
    public void fireResultUpdate(HierarchicalResult clust) {
        ClusteringListener[] listeners;

        listeners = clusteringListeners.getListeners(ClusteringListener.class);
        System.out.println("fireing results update, listeners size: " + listeners.length);
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].resultUpdate(clust);
            System.out.println("listerner: " + listeners[i].getClass().toString());
        }
    }

    @Override
    public BufferedImage getBufferedImage(int w, int h) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        // System.out.println("adjusted");
     /*
         * dendrogramPanel.updateSize();
         * scroller.setPreferredSize(dendrogramPanel.getSize());
         * scroller.getViewport().revalidate();
         * scroller.getViewport().repaint();
         */
        //  dendrogramPanel.heatmap.invalidate();
    }

    private class ViewerComponentListener implements ComponentListener {

        DendrogramViewer viewer;

        ViewerComponentListener(DendrogramViewer inst) {
            viewer = inst;
        }

        @Override
        public void componentResized(ComponentEvent ce) {
            Component c = (Component) ce.getSource();
            //this method invalidate buffered image which is usually necessary 
            //on component dimensions change (in case that the heatmap panel is bigger
            //than the frame)
            dendrogramPanel.getHeatmap().drawData();
            viewer.updateLayout();
            repaint();
        }

        @Override
        public void componentMoved(ComponentEvent ce) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void componentShown(ComponentEvent ce) {
            Component c = (Component) ce.getSource();
            System.out.println(c.getName() + " component shown");
            viewer.updateLayout();
        }

        @Override
        public void componentHidden(ComponentEvent ce) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
