package org.clueminer.dgram;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.EventListenerList;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.TreeCluster;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.clustering.gui.ClusterPreviewer;
import org.clueminer.dataset.api.Instance;
import org.clueminer.project.api.ProjectController;
import org.clueminer.project.api.Workspace;
import org.clueminer.utils.Exportable;
import org.openide.util.Lookup;

/**
 * Component for dendrogram visualization
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class DgViewer<E extends Instance, C extends Cluster<E>> extends JPanel implements Exportable, AdjustmentListener, DendroViewer<E, C> {

    private static final long serialVersionUID = -9145028094444482028L;
    protected ArrayList<TreeCluster> clusters;
    protected DgPanel dendrogramPanel;
    private JScrollPane scroller;
    protected Dimension elementSize;
    protected DendrogramMapping data;
    private boolean fitToPanel = false;
    private final transient EventListenerList clusteringListeners = new EventListenerList();
    private static final Logger logger = Logger.getLogger(DgViewer.class.getName());

    public DgViewer() {
        setBackground(Color.WHITE);
        elementSize = new Dimension(15, 15);
        initComponents();
        this.setDoubleBuffered(true);
    }

    @Override
    public String getName() {
        if (data != null) {
            return data.getDataset().getName();
        } else {
            return DgViewer.class.getName();
        }
    }

    @Override
    public void setDataset(DendrogramMapping dataset) {
        this.data = dataset;
        updateLayout();
        //update all components to work with same data
        fireDatasetChanged(new DendrogramDataEvent(this), data);
        dendrogramPanel.triggerUpdate();
    }

    public DendrogramMapping getDendrogramData() {
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
        dendrogramPanel = new DgPanel(this);
        // header = new ExperimentHeader();
        // add(header);
        //    colorBar.setClusters(clusters); //
        // add(colorBar);
        scroller = new JScrollPane();
        scroller.getViewport().add(dendrogramPanel);
        scroller.getViewport().setDoubleBuffered(true);
        scroller.getViewport().setOpaque(true);
        scroller.setVisible(true);
        scroller.getVerticalScrollBar().addAdjustmentListener(this);
        setVisible(true);
        this.addComponentListener(new ViewerComponentListener(this));
        this.addDendrogramDataListener(dendrogramPanel);
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(scroller, c);
        dendrogramPanel.addMouseWheelListener(new MouseWheelDriver(this, scroller));
    }

    private void updateLayout() {
        //int heatmapSize = this.getSize().width - dendrogramPanel.getVerticalTreeSize().width - dendrogramPanel.getAnnotationWidth();
        /*  System.out.println("annotation panel = " + dendrogramPanel.getAnnotationWidth());

         System.out.println("vertical tree = " + dendrogramPanel.getVerticalTreeSize().width);
         System.out.println("heatmap " + dendrogramPanel.getHeatmapSize());
         System.out.println("clustering view size" + this.getSize() + " heatmap size= " + heatmapSize);

         //System.out.println("feature length = "+featuresLenght+ " heat map width = "+dendrogramPanel.getHeatmapWidth() );
         //dendrogramPanel.getHeatmapWidth() / featuresLenght;///data.getFeaturesSize();
         System.out.println("new element size " + elementSize);*/
        //setMinimumSize(dendrogramPanel.getSize());
        //update size of scrollbars
        scroller.getViewport().revalidate();
        setPreferredSize(dendrogramPanel.getSize());
        validate();
        revalidate();
        repaint();
    }

    @Override
    public void setCellHeight(int height, boolean isAdjusting, Object source) {
        if (height > 0) {
            elementSize.height = height;
            fireCellHeightChanged(new DendrogramDataEvent(this), height, isAdjusting, source);
            updateLayout();
        }
    }

    @Override
    public void setFitToPanel(boolean fitToPanel) {
        this.fitToPanel = fitToPanel;
        dendrogramPanel.setFitToPanel(fitToPanel);
        updateLayout();
    }

    @Override
    public boolean isFitToPanel() {
        return fitToPanel;
    }

    public int getCellHeight() {
        return elementSize.height;
    }

    @Override
    public void setCellWidth(int width, boolean isAdjusting, Object source) {
        if (width > 0) {
            elementSize.width = width;
            fireCellWidthChanged(new DendrogramDataEvent(this), width, isAdjusting, source);
            updateLayout();
        }
    }

    public int getCellWidth() {
        return elementSize.width;
    }

    @Override
    public void setHorizontalTreeVisible(boolean show) {
        dendrogramPanel.setHorizontalTreeVisible(show);
    }

    @Override
    public boolean isHorizontalTreeVisible() {
        return dendrogramPanel.isHorizontalTreeVisible();
    }

    @Override
    public void setVerticalTreeVisible(boolean show) {
        dendrogramPanel.setVerticalTreeVisible(show);
    }

    @Override
    public boolean isVerticalTreeVisible() {
        return dendrogramPanel.isVerticalTreeVisible();
    }

    @Override
    public void setLegendVisible(boolean show) {
        dendrogramPanel.setLegendVisible(show);
    }

    @Override
    public boolean isLegendVisible() {
        return dendrogramPanel.isLegendVisible();
    }

    @Override
    public void setLabelsVisible(boolean show) {
        dendrogramPanel.setLabelsVisible(show);
    }

    @Override
    public boolean isLabelVisible() {
        return dendrogramPanel.isLabelVisible();
    }

    /**
     * Triggered e.g. when user selects a clustering
     *
     * @param evt
     * @param dataset
     * @return
     */
    public boolean fireDatasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset) {
        //first notify panel itself
        dendrogramPanel.datasetChanged(evt, dataset);
        //int i = 0;
        for (DendrogramDataListener listener : dendrogramPanel.getDataListeners()) {

            if (listener != null) {
                //System.out.println("dataset changed [" + (i++) + "]: " + listener.getClass().toString());
                listener.datasetChanged(evt, dataset);
            } else {
                throw new RuntimeException("listener is null");
            }
        }
        //updateLayout();
        return true;
    }

    public boolean fireCellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting, Object source) {
        for (DendrogramDataListener listener : dendrogramPanel.getDataListeners()) {
            if (source != listener) {
                listener.cellWidthChanged(evt, width, isAdjusting);
            }
        }
        return true;
    }

    protected boolean fireCellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting, Object source) {
        for (DendrogramDataListener listener : dendrogramPanel.getDataListeners()) {
            if (listener != source) {
                listener.cellHeightChanged(evt, height, isAdjusting);
            }
        }
        return true;
    }

    @Override
    public void addDendrogramDataListener(DendrogramDataListener listener) {
        dendrogramPanel.addDendrogramDataListener(listener);
    }

    @Override
    public void removeDendrogramDataListener(DendrogramDataListener listener) {
        dendrogramPanel.removeDendrogramDataListener(listener);
    }

    public void addRowsTreeListener(TreeListener listener) {
        dendrogramPanel.addRowsTreeListener(listener);
    }

    /**
     *
     * @param listener
     */
    @Override
    public void addClusteringListener(ClusteringListener<E, C> listener) {
        clusteringListeners.add(ClusteringListener.class, listener);
    }

    public void removeClusteringListener(ClusteringListener<E, C> listener) {
        clusteringListeners.remove(ClusteringListener.class, listener);
    }

    @Override
    public void fireClusteringChanged(Clustering<E, C> clust) {
        ClusteringListener[] listeners;

        listeners = clusteringListeners.getListeners(ClusteringListener.class);
        for (ClusteringListener listener : listeners) {
            listener.clusteringChanged(clust);
        }
    }

    @Override
    public Dimension getElementSize() {
        return elementSize;
    }

    public void setElementSize(Dimension elementSize) {
        this.elementSize = elementSize;
    }

    public void fireResultUpdate(HierarchicalResult clust) {
        ClusteringListener[] listeners;

        listeners = clusteringListeners.getListeners(ClusteringListener.class);
        for (ClusteringListener listener : listeners) {
            listener.resultUpdate(clust);
        }
    }

    @Override
    public BufferedImage getBufferedImage(int w, int h) {
        Dimension dendroDim = dendrogramPanel.getSize();
        int width, height;
        Dimension dimPrev = null;
        BufferedImage prev = null;

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        if (pc != null) {
            Workspace workspace = pc.getCurrentWorkspace();
            JPanel previews = null;
            if (workspace != null) {
                previews = (JPanel) workspace.getLookup().lookup(ClusterPreviewer.class);
            }

            if (previews != null) {
                dimPrev = previews.getSize();
                prev = new BufferedImage(dimPrev.width, dimPrev.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D gp = prev.createGraphics();
                previews.paint(gp);
            }
        }

        width = Math.max(w, dendroDim.width);
        height = Math.max(h, dendroDim.height);

        dendrogramPanel.setSize(width, height);
        if (dimPrev != null) {
            width += dimPrev.width;
            height = Math.max(dendroDim.height, dimPrev.height);
        }

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        dendrogramPanel.render(g, width, height);
        logger.log(Level.INFO, "exporting dendrogram to bitmap, export size: {0}x{1}", new Object[]{width, height});
        if (prev != null) {
            //combine images
            g.drawImage(prev, dendroDim.width, 0, null);
        }

        g.dispose();
        return bi;
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

    /**
     * Set clustering as the data source. However clustering structure does not
     * contain hierarchical data, we have to find them either in cache or
     * compute hierarchical structure
     *
     * @param clustering
     */
    @Override
    public void setClustering(Clustering clustering) {
        Lookup lookup = clustering.getLookup();
        if (lookup != null) {
            DendrogramMapping mapping = lookup.lookup(DendrogramMapping.class);
            if (mapping != null) {
                setDataset(mapping);
            }
        }
    }

    @Override
    public void setEvaluationVisible(boolean show) {
        dendrogramPanel.setShowEvalPlot(show);
    }

    @Override
    public void fireRowMappingChanged(Object source, HierarchicalResult rows) {
        dendrogramPanel.fireRowsOrderUpdated(source, rows);
    }

    @Override
    public void fireColumnsMappingChanged(Object source, HierarchicalResult columns) {
        dendrogramPanel.fireColsOrderUpdated(source, columns);
    }

    @Override
    public DendrogramMapping getDendrogramMapping() {
        return data;
    }

    @Override
    public boolean isEvaluationVisible() {
        return dendrogramPanel.isShowEvalPlot();
    }

    private class ViewerComponentListener implements ComponentListener {

        DgViewer viewer;

        ViewerComponentListener(DgViewer inst) {
            viewer = inst;
        }

        @Override
        public void componentResized(ComponentEvent ce) {
            Component c = (Component) ce.getSource();
            //this method invalidate buffered image which is usually necessary
            //on component dimensions change (in case that the heatmap panel is bigger
            //than the frame)
            //@TODO is it really necessary?
            if (dendrogramPanel.getHeatmap() != null) {
                dendrogramPanel.getHeatmap().resetCache();
            }
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
