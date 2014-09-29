package org.clueminer.dgram;

import org.clueminer.clustering.api.dendrogram.DendroViewer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.text.DecimalFormat;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.ColorScheme;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.clustering.api.dendrogram.DendroHeatmap;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.dendrogram.gui.ClusterAssignment;
import org.clueminer.dendrogram.gui.ColorSchemeImpl;
import org.clueminer.dendrogram.gui.ColumnAnnotation;
import org.clueminer.dendrogram.gui.ColumnStatistics;
import org.clueminer.dendrogram.gui.CutoffLine;
import org.clueminer.dendrogram.gui.CutoffSlider;
import org.clueminer.dendrogram.gui.Heatmap;
import org.clueminer.dendrogram.gui.Legend;
import org.clueminer.dendrogram.gui.RowAnnotation;
import org.clueminer.dendrogram.tree.AbstractScale;
import org.clueminer.dendrogram.tree.HCLColorBar;
import org.clueminer.dendrogram.tree.HorizontalScale;
import org.clueminer.dendrogram.tree.VerticalScale;
import org.clueminer.dgram.eval.SilhouettePlot;
import org.clueminer.events.ListenerList;
import org.clueminer.gui.BPanel;

/**
 *
 * @author Tomas Barton
 */
public class DgPanel extends BPanel implements DendrogramDataListener, DendroPane {

    private static final long serialVersionUID = -5443298776673785208L;
    //component to draw a tree for rows
    private DendrogramTree rowsTree;
    private AbstractScale rowsScale;
    private CutoffSlider slider;
    private CutoffLine cutoff;
    private DendrogramTree columnsTree;
    private AbstractScale columnsScale;
    //component to draw clusters colors and descriptions
    protected HCLColorBar colorBar;
    protected DendrogramMapping dendroData;
    //component to draw an experiment dendroData
    protected Heatmap heatmap;
    //component to draw an experiment annotations
    protected RowAnnotation rowAnnotationBar;
    protected ColumnAnnotation columnAnnotationBar;
    protected ColumnStatistics statistics;
    protected ClusterAssignment clusterAssignment;
    protected SilhouettePlot silhouettePlot;
    private JLayeredPane rowTreeLayered;
    private boolean showColumnsTree = true;
    private boolean showRowsTree = true;
    private boolean showScale = true;
    private boolean showColorBar = true;
    private boolean showLegend = true;
    private boolean showLabels = true;
    private boolean showSlider = true;
    private boolean showEvalPlot = true;
    private boolean fitToPanel = true;
    protected DendroViewer dendroViewer;
    private Legend legend;
    protected Dimension size;
    /**
     * Using gradient with 3 colors instead of just 2 colors
     */
    protected boolean useDoubleGradient = true;
    protected boolean isAntiAliasing = true;
    protected Color bg = Color.WHITE;
    protected ColorSchemeImpl colorScheme;
    protected DecimalFormat decimalFormat = new DecimalFormat("#.##");
    protected Dimension elementSize;
    protected Insets insets = new Insets(5, 5, 40, 5);
    private int cutoffSliderSize = 6;
    private final transient ListenerList<DendrogramDataListener> dataListeners = new ListenerList<>();

    public DgPanel(DendroViewer v) {
        size = new Dimension(10, 10);
        dendroViewer = v;
        elementSize = v.getElementSize();
        initComponents();
        updateLayout();
    }

    private void initComponents() {
        setBackground(bg);
        setOpaque(true);
        colorScheme = new ColorSchemeImpl(useDoubleGradient);
    }

    /**
     * Layout settings of component - should be called when any component is
     * shown or hidden
     *
     */
    private void updateLayout() {
        this.removeAll(); //clean the component

        if (dendroData != null) {
            //check if we have data to display
            if (!dendroData.hasColumnsClustering()) {
                showColumnsTree = false;
            }
            if (!dendroData.hasRowsClustering()) {
                showRowsTree = false;
            }
        }
        prepareComputedLayout();

        if (fitToPanel) {
            this.reqSize = getSize();
            if (!hasData()) {
                return;
            }
            recalculate();
        }
        computeLayout();
        validate();
        revalidate();
        repaint();

        this.updateUI();
    }

    @Override
    public void sizeUpdated(Dimension reqSize) {
        if (!hasData()) {
            return;
        }
        if (fitToPanel) {
            this.reqSize = reqSize;
            recalculate();
        }
        computeLayout();
    }

    @Override
    public void render(Graphics2D g) {
    }

    /**
     * Calculate sizes of components in order to fit into given panel size
     */
    @Override
    public void recalculate() {
        //maximal percentage for rows tree
        //rows tree will have at most 200px (30% of the screen)
        int rowsTreeDim = Math.min(200, (int) (reqSize.width * 0.3));
        int colsTreeDim = Math.min(200, (int) (reqSize.height * 0.3));
        int heatmapWidth, heatmapHeight = reqSize.height - 40; //TODO: empiric constant for annotations
        if (showEvalPlot) {
            heatmapWidth = (int) (reqSize.width * 0.4);
        } else {
            heatmapWidth = reqSize.width - rowsTreeDim;
        }

        if (showColumnsTree) {
            heatmapHeight -= colsTreeDim;
            columnsTree.updateSize();
        } else {
            if (slider != null) {
                heatmapHeight -= slider.getSize().height;
            }
        }
        //column annotations is usually bigger than tree annotation
        if (columnAnnotationBar != null) {
            heatmapHeight -= columnAnnotationBar.getSize().height;
        }
        //compute element height
        double perLine = Math.floor(heatmapHeight / (double) dendroData.getNumberOfRows());
        if (perLine < 1) {
            perLine = 1;// 1px line height
        }
        elementSize.height = (int) perLine;
        //int diff = heatmapHeight - (dendroData.getNumberOfRows() * elementSize.height);
        //System.out.println("heatmap h diff = " + diff);

        //compute element width
        perLine = Math.floor(heatmapWidth / (double) dendroData.getNumberOfColumns());
        if (perLine < 1) {
            perLine = 1;// 1px line height
        }
        elementSize.width = (int) perLine;
        // diff = heatmapWidth - (dendroData.getNumberOfColumns() * elementSize.width);
        dendroViewer.setCellHeight(elementSize.height, false, this);
        dendroViewer.setCellWidth(elementSize.width, false, this);
//            rowAnnotationBar.setElement(elementSize.height);

    }

    private void prepareComputedLayout() {
        setLayout(null);

        createHeatmap();
        add(heatmap);

        createLegend();
        add(legend);

        //rows
        if (showRowsTree) {
            createRowsTree();
            add(rowTreeLayered);
            if (showScale) {
                add(rowsScale);
            }

            if (showSlider) {
                createRowSlider();
                add(slider);
            }
        }
        //columns
        if (showColumnsTree) {
            createColumnsTree();
            add((Component) columnsTree);
            if (showScale) {
                add(columnsScale);
            }
        }
        createColumnAnnotation();
        add(columnAnnotationBar);

        if (isLabelVisible()) {
            createRowAnnotation();
            add(rowAnnotationBar);
        }
        createClusterAssignments();
        add(clusterAssignment);

        if (showEvalPlot) {
            createEvaluation();
            add(silhouettePlot);
        }
    }


    /*
     * Compute precisely each component size for given space
     *
     * |                      |              |                 |
     * |    rowsTree (30%)    |  heatmap 40% | cluster         |  eval plot (30%)
     * |                      |              | assignment 30px |    if present
     * |                      |              | (fixed)
     */
    private void computeLayout() {
        Dimension dim, dimHeatmap, clustAssign, dimSlider = null;
        int heatmapXoffset, heatmapYoffset;
        int totalWidth, totalHeight;

        //X, Y position of heatmap's top left corner
        heatmapXoffset = insets.left + rowsTree.getSize().width;
        heatmapYoffset = updateHeatmapYoffset(heatmapXoffset);

        dimHeatmap = heatmap.getSize();
        totalHeight = dimHeatmap.height;

        if (showRowsTree) {
            dim = rowsTree.getSize();
            if (showSlider) {
                //slider height is constant
                slider.setSize(dim.width, 15);
                dimSlider = slider.getSize();
            }
            if (!showColumnsTree && dimSlider != null) {
                heatmapYoffset += dimSlider.height;
                totalHeight += dimSlider.height;
            }
            updateRowTreePosition(dim, heatmapYoffset, dimSlider);
        }

        if (dimSlider != null) {
            totalHeight += Math.max(dimSlider.height, heatmapYoffset);
        } else {
            totalHeight += heatmapYoffset;
        }
        heatmap.setBounds(heatmapXoffset, heatmapYoffset, dimHeatmap.width, dimHeatmap.height);
        dim = columnAnnotationBar.getSize();
        columnAnnotationBar.setBounds(heatmapXoffset, heatmapYoffset + dimHeatmap.height, dim.width, dim.height);
        int rowsScaleHeight = rowsScale.getHeight();
        int thirdLineHeight = Math.max(rowsScaleHeight, dim.height);
        totalHeight += thirdLineHeight;

        if (showLegend) {
            dim = legend.getSize();
            if (showColumnsTree) {
                legend.setBounds(heatmapXoffset + dimHeatmap.width, insets.top, dim.width, dim.height);
            } else {
                //we don't have extra space above heatmap
                legend.setBounds(heatmapXoffset + dimHeatmap.width, totalHeight, dim.width, dim.height);
            }
        }

        clustAssign = clusterAssignment.getSize();
        //row tree + heatmap
        totalWidth = heatmapXoffset + dimHeatmap.width;
        clusterAssignment.setBounds(totalWidth, heatmapYoffset, clustAssign.width, clustAssign.height);
        totalWidth += clustAssign.width;

        if (isLabelVisible()) {
            dim = rowAnnotationBar.getSize();
            rowAnnotationBar.setBounds(totalWidth, heatmapYoffset, dim.width, dim.height);
            totalWidth += dim.width;
        }

        if (showEvalPlot) {
            dim = silhouettePlot.getSize();
            silhouettePlot.setBounds(totalWidth, heatmapYoffset, dim.width, dim.height);
        }
        //setPreferredSize(new Dimension(totalWidth, totalHeight));
        setMinimumSize(new Dimension(totalWidth, totalHeight));
    }

    private void updateColumnTreePosition(Dimension dim, int heatmapXoffset) {
        dim = columnsTree.getSize();
        columnsTree.setBounds(heatmapXoffset, insets.top, dim.width, dim.height);
        if (showScale) {
            //columnsScale.setSize(scaleHeight, dim.height);
            columnsScale.updateSize();
            columnsScale.setBounds(heatmapXoffset + dim.width, insets.top, columnsScale.getSize().width, dim.height);
        }
    }

    private void updateRowTreePosition(Dimension dim, int heatmapYoffset, Dimension dimSlider) {
        rowTreeLayered.setBounds(insets.left, heatmapYoffset, dim.width, dim.height);
        if (showSlider) {
            slider.setBounds(insets.left, heatmapYoffset - dimSlider.height, dimSlider.width, dimSlider.height);
        }
        if (showScale) {
            //rowsScale.setSize(dim.width, scaleHeight);
            int scaleYoffset;
            scaleYoffset = heatmapYoffset + dim.height;
            if (dimSlider != null) {
                scaleYoffset += dimSlider.height;
            }
            rowsScale.updateSize();
            rowsScale.setBounds(insets.left, scaleYoffset, dim.width, rowsScale.getSize().height);
        }
    }

    private int updateHeatmapYoffset(int heatmapXoffset) {
        int heatmapYoffset;
        if (showColumnsTree) {
            Dimension dim = columnsTree.getSize();
            heatmapYoffset = insets.top + dim.height;
            updateColumnTreePosition(dim, heatmapXoffset);
        } else {
            heatmapYoffset = insets.top;
        }
        return heatmapYoffset;
    }

    private void createHeatmap() {
        if (heatmap == null) {
            heatmap = new Heatmap(this);
            heatmap.setOffset(0);
            dendroViewer.addDendrogramDataListener(heatmap);
        }
    }

    private void createColumnsTree() {
        //we call constructor just one
        if (columnsTree == null) {
            columnsTree = new DgBottomTree(this);
            //columnsTree.setHorizontalOffset(0);
            //@TODO we should remove listener if component is not displayed
            dendroViewer.addDendrogramDataListener(columnsTree);
        }
        if (columnsScale == null) {
            columnsScale = new HorizontalScale(columnsTree, this);
            dendroViewer.addDendrogramDataListener(columnsScale);
        }
    }

    private void createRowsTree() {
        //we call constructor just one
        if (rowsTree == null) {
            rowsTree = new DgRightTree(this);
            dendroViewer.addDendrogramDataListener(rowsTree);
            //listen to cluster selection
            rowsTree.addTreeListener(heatmap);
            cutoff = new CutoffLine(this, rowsTree);
            dendroViewer.addDendrogramDataListener(cutoff);
        }
        if (rowsScale == null) {
            rowsScale = new VerticalScale(rowsTree, this);
            dendroViewer.addDendrogramDataListener(rowsScale);
        }
        if (rowTreeLayered == null) {
            rowTreeLayered = new JLayeredPane();
            rowTreeLayered.setLayout(new LayoutManager() {
                @Override
                public void addLayoutComponent(String name, Component comp) {
                }

                @Override
                public void removeLayoutComponent(Component comp) {
                }

                @Override
                public Dimension preferredLayoutSize(Container parent) {
                    return rowsTree.getSize();
                }

                @Override
                public Dimension minimumLayoutSize(Container parent) {
                    return rowsTree.getSize();
                }

                @Override
                public void layoutContainer(Container parent) {
                    Insets insets = parent.getInsets();
                    int w = parent.getWidth() - insets.left - insets.right;
                    int h = parent.getHeight() - insets.top - insets.bottom;
                    cutoff.setBounds(insets.left, insets.top, w, h);
                    rowsTree.setBounds(insets.left, insets.top, w, h);
                }
            });

            rowTreeLayered.add(cutoff, 0); //lower level
            rowTreeLayered.add((Component) rowsTree, 1); //upper level
        }
    }

    private void createRowSlider() {
        if (slider == null) {
            slider = new CutoffSlider(this, SwingConstants.HORIZONTAL, cutoff, cutoffSliderSize);
            dendroViewer.addDendrogramDataListener(slider);
            rowsTree.addTreeListener(slider);
        }
    }

    private void createRowAnnotation() {
        //we call constructor just one
        if (rowAnnotationBar == null) {
            //heatmap annotations
            rowAnnotationBar = new RowAnnotation(this);
            dendroViewer.addDendrogramDataListener(rowAnnotationBar);
            if (rowsTree != null) {
                rowsTree.addTreeListener(rowAnnotationBar);
            }
        }
        if (dendroData != null) {
            rowAnnotationBar.setDendrogramData(dendroData);
        }
    }

    private void createEvaluation() {
        //we call constructor just one
        if (silhouettePlot == null) {
            //heatmap annotations
            silhouettePlot = new SilhouettePlot(false);
            silhouettePlot.setElementSize(elementSize.width, elementSize.height);
            dendroViewer.addDendrogramDataListener(silhouettePlot);
            dendroViewer.addClusteringListener(silhouettePlot);
        }
        if (dendroData != null) {
            silhouettePlot.setDendrogramData(dendroData);
        }
    }

    private void createLegend() {
        //we call constructor just one
        if (legend == null) {
            legend = new Legend(this);
            dendroViewer.addDendrogramDataListener(legend);
            if (dendroData != null) {
                legend.setData(dendroData);
            }
        }
    }

    private void createColumnAnnotation() {
        //we call constructor just one
        if (columnAnnotationBar == null) {
            columnAnnotationBar = new ColumnAnnotation(this);
            dendroViewer.addDendrogramDataListener(columnAnnotationBar);
            rowsTree.addTreeListener(columnAnnotationBar);
        }
    }

    private void addColumnStatistics(int column, int row) {
        //we call constructor just one
        if (statistics == null) {
            statistics = new ColumnStatistics(this);
            dendroViewer.addDendrogramDataListener(statistics);
            rowsTree.addTreeListener(statistics);
        }
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new java.awt.Insets(5, 0, 0, 0);
        c.gridx = column;
        c.gridy = row;
        add(statistics, c);
    }

    private void createClusterAssignments() {
        //we call constructor just one
        if (clusterAssignment == null) {
            //heatmap annotations
            clusterAssignment = new ClusterAssignment(this);
            dendroViewer.addDendrogramDataListener(clusterAssignment);
            dendroViewer.addClusteringListener(clusterAssignment);
        }
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset) {
        this.dendroData = dataset;
        updateLayout();
        //sizeUpdated(getSize());
        if (rowsTree != null) {
            rowsTree.fireTreeUpdated();
        }
        if (columnsTree != null) {
            columnsTree.fireTreeUpdated();
        }
        slider.updatePosition();
        //@TODO probably call repaint
        repaint();
    }

    public void fireRowsOrderUpdated(Object source, HierarchicalResult rows) {
        if (rowsTree != null) {
            rowsTree.fireTreeUpdated();
        }
    }

    public void fireColsOrderUpdated(Object source, HierarchicalResult cols) {
        if (columnsTree != null) {
            columnsTree.fireTreeUpdated();
        }
    }

    private void updateWidth(int elemWidth) {
        int width = dendroData.getNumberOfColumns() * elemWidth;

        if (showRowsTree) {
            width += rowsTree.getWidth();
        }
        width += insets.left + insets.right;
        //add annotation
        if (rowAnnotationBar != null) {
            width += rowAnnotationBar.getWidth();
        }
        if (columnsScale != null) {
            width += columnsScale.getWidth();
        }
        size.width = width;
    }

    /**
     * Computed current component height
     *
     * @param elemHeight
     */
    private void updateHeight(int elemHeight) {
        //height of heatmap
        int heatmapXoffset, heatmapYoffset;
        int height = dendroData.getNumberOfRows() * elemHeight;
        //X, Y position of heatmap's top left corner
        heatmapXoffset = insets.left + rowsTree.getSize().width;
        if (showColumnsTree && columnsTree != null) {
            height += columnsTree.getHeight();
        }
        heatmapYoffset = updateHeatmapYoffset(heatmapXoffset);
        height += heatmapYoffset;
        Dimension dim = rowsTree.getSize();
        Dimension dimSlider = null;
        if (showSlider) {
            //slider height is constant
            slider.setSize(dim.width, 15);
            dimSlider = slider.getSize();
        }
        updateRowTreePosition(dim, heatmapYoffset, dimSlider);
        height += insets.bottom + insets.top;
        if (columnAnnotationBar != null) {
            int colsize = columnAnnotationBar.getSize().height;
            if (rowsScale != null) {
                colsize = Math.max(colsize, rowsScale.getHeight());
            }
            height += colsize;
        }
        size.height = height;
    }

    private void setSizes() {
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
    }

    /**
     * Dimensions of tree component
     *
     * @return
     */
    public Dimension getVerticalTreeSize() {
        return columnsTree.getSize();
    }

    public Dimension getHeatmapSize() {
        /**
         * @TODO count size of annotations on right side
         */
        int width = 10, cols = 1;
        if (heatmap != null) {
            return heatmap.getSize();
        }
        if (dendroData != null) {
            cols = dendroData.getNumberOfColumns();
        }
        return new Dimension(width, cols * 10);
    }

    public int getAnnotationWidth() {
        if (rowAnnotationBar != null) {
            return rowAnnotationBar.getWidth();
        }
        return 0;
    }

    public void setHorizontalTreeVisible(boolean show) {
        this.showColumnsTree = show;
        updateLayout();
    }

    public void setLegendVisible(boolean show) {
        if (this.showLegend != show) {
            this.showLegend = show;
            updateLayout();
        }
    }

    public boolean isLegendVisible() {
        return this.showLegend;
    }

    public boolean isHorizontalTreeVisible() {
        return showColumnsTree;
    }

    public void setVerticalTreeVisible(boolean show) {
        this.showRowsTree = show;
        updateLayout();
    }

    public boolean isVerticalTreeVisible() {
        return showRowsTree;
    }

    public void setScaleVisible(boolean show) {
        this.showScale = show;
    }

    public boolean isScaleVisible() {
        return this.showScale;
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        elementSize.width = width;
        updateWidth(width);
        setSizes();
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        elementSize.height = height;
        //make sure rows tree gets notified before we compute size of the component
        rowsTree.cellHeightChanged(evt, height, isAdjusting);
        updateHeight(height);
        setSizes();
    }

    @Override
    public Dimension getElementSize() {
        return this.elementSize;
    }

    /**
     * @return the showLabels
     */
    public boolean isLabelVisible() {
        return showLabels;
    }

    /**
     * @param showLabels if true row's legend is displayed
     */
    public void setLabelsVisible(boolean showLabels) {
        this.showLabels = showLabels;
        updateLayout();
    }

    public void addRowsTreeListener(TreeListener listener) {
        rowsTree.addTreeListener(listener);
    }

    @Override
    public DendrogramMapping getDendrogramData() {
        return dendroData;
    }

    @Override
    public boolean useDoubleGradient() {
        return useDoubleGradient;
    }

    @Override
    public ColorScheme getScheme() {
        return colorScheme;
    }

    @Override
    public void fireClusteringChanged(Clustering clust) {
        dendroViewer.fireClusteringChanged(clust);
    }

    @Override
    public boolean isAntiAliasing() {
        return isAntiAliasing;
    }

    @Override
    public String formatNumber(Object number) {
        return decimalFormat.format(number);
    }

    @Override
    public DendroHeatmap getHeatmap() {
        return heatmap;
    }

    @Override
    public void setSliderDiameter(int sliderDiam) {
        this.cutoffSliderSize = sliderDiam;
    }

    @Override
    public int getSliderDiameter() {
        return cutoffSliderSize;
    }

    public boolean isFitToPanel() {
        return fitToPanel;
    }

    public void setFitToPanel(boolean fitToPanel) {
        if (this.fitToPanel != fitToPanel) {
            this.fitToPanel = fitToPanel;
            updateLayout();
        }
    }

    public boolean isShowEvalPlot() {
        return showEvalPlot;
    }

    public void setShowEvalPlot(boolean showEvalPlot) {
        if (this.showEvalPlot != showEvalPlot) {
            this.showEvalPlot = showEvalPlot;
            updateLayout();
        }
    }

    @Override
    public boolean hasData() {
        return dendroData != null;
    }

    public void addDendrogramDataListener(DendrogramDataListener listener) {
        dataListeners.add(listener);
    }

    public void removeDendrogramDataListener(DendrogramDataListener listener) {
        dataListeners.remove(listener);
    }

    public ListenerList<DendrogramDataListener> getDataListeners() {
        return dataListeners;
    }

}
