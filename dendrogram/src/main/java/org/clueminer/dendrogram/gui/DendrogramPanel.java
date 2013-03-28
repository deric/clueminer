package org.clueminer.dendrogram.gui;

import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.Box;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.dendrogram.DendrogramData;
import org.clueminer.dendrogram.events.DendrogramDataEvent;
import org.clueminer.dendrogram.events.DendrogramDataListener;
import org.clueminer.dendrogram.tree.*;

/**
 *
 * @author Tomas Barton
 */
public class DendrogramPanel extends JPanel implements DendrogramDataListener {

    private static final long serialVersionUID = 2763497503617997604L;
    //component to draw a tree for rows
    private VerticalTree rowsTree;
    private AbstractScale rowsScale;
    private CutoffLine cutoff;
    private HorizontalTree columnsTree;
    private AbstractScale columnsScale;
    //component to draw clusters colors and descriptions
    protected HCLColorBar colorBar;
    protected DendrogramData dendroData;
    //component to draw an experiment dendroData
    protected Heatmap heatmap;
    //component to draw an experiment annotations
    protected RowAnnotation rowAnnotationBar;
    protected ColumnAnnotation columnAnnotationBar;
    protected ColumnStatistics statistics;
    protected ClusterAssignment clusterAssignment;
    private JLayeredPane treeLayered;
    private CutoffSlider slider;
    private boolean showColumnsTree = true;
    private boolean showRowsTree = true;
    private boolean showScale = true;
    private boolean showColorBar = true;
    private boolean showLegend = true;
    private boolean showLabels = true;
    private boolean showSlider = true;
    protected DendrogramViewer dendroViewer;
    private Legend legend;
    protected Dimension size;
    /**
     * Using gradient with 3 colors instead of just 2 colors
     */
    protected boolean useDoubleGradient = true;
    protected boolean isAntiAliasing = true;
    protected Color bg = Color.WHITE;
    protected ColorScheme colorScheme;
    protected DecimalFormat decimalFormat = new DecimalFormat("#.##");
    protected Dimension elementSize;
    protected Insets insets = new Insets(5, 5, 5, 5);

    public DendrogramPanel(DendrogramViewer v) {
        size = new Dimension(10, 10);
        dendroViewer = v;
        elementSize = v.elementSize;
        initComponents();
        updateLayout();
    }

    private void initComponents() {
        setBackground(bg);
        colorScheme = new ColorScheme(this);
    }

    /**
     * Layout settings of component - should be called when any component is
     * shown or hidden
     *
     * |(0,0) | (0, 1) horizontal|
     *
     * |(0,1) | (1, 1) heatmap | |vertical | | \----------------------------/
     */
    private void updateLayout() {
        this.removeAll(); //clean the component
        setLayout(new GridBagLayout());
        int gridy = 0, gridx = 0;
        int lastCol = 0;

        //columns
        if (showColumnsTree) {
            addColumnsTree(gridx, gridy);
            lastCol = gridx + 3;
            if (showLegend) {
                addLegend(lastCol, gridy);
            }
            gridy++; //increase index to move other components to next row
        } else if (showLegend) {
            addLegend(2, gridy++);
            lastCol = gridx + 2;
        }

        //rows
        if (showRowsTree) {
            //move heatmap to next column
            addRowsTree(gridx++, gridy);
            if (showSlider) {
                //should be above row's tree
                addRowSlider(gridx - 1, gridy - 1);
            }
        }
        addHeatmap(gridx, gridy);
        //panel for clusters' assignment
        addClustersAssignment(gridx + 1, gridy);
        addRowAnnotation(lastCol, gridy, isLabelVisible());

        if (showColorBar) {
            colorBar = new HCLColorBar();
            add(colorBar);
            //this.colorBar.addMouseListener(listener); 
        }
        addColumnAnnotationBar(gridx, ++gridy);
        //addColumnStatistics(gridx, ++gridy);

        GridBagConstraints horizontalFill = new GridBagConstraints();
        horizontalFill.anchor = GridBagConstraints.WEST;
        horizontalFill.fill = GridBagConstraints.HORIZONTAL;

        this.add(Box.createHorizontalGlue(), horizontalFill);

        repaint();
        this.updateUI();
    }

    private void addHeatmap(int column, int row) {
        //we call constructor just one
        if (heatmap == null) {
            heatmap = new Heatmap(this);
            heatmap.setOffset(0);
            dendroViewer.addDendrogramDataListener(heatmap);
        }
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.insets = new java.awt.Insets(0, 200, 0, 0);
        c.gridx = column;
        c.gridy = row;
        add(heatmap, c);
    }

    private void addColumnsTree(int column, int row) {
        if (showRowsTree) {
            column++;
        }
        //we call constructor just one
        if (columnsTree == null) {
            columnsTree = new HorizontalTree(this);
            columnsTree.setHorizontalOffset(0);
            //@TODO we should remove listener if component is not displayed
            dendroViewer.addDendrogramDataListener(columnsTree);
        }

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new java.awt.Insets(15, 0, 0, 0);
        c.gridx = column;
        c.gridy = row;
        add(columnsTree, c);

        if (showScale) {
            if (columnsScale == null) {
                columnsScale = new HorizontalScale(columnsTree, this);
                dendroViewer.addDendrogramDataListener(columnsScale);
            }
            c.insets = new java.awt.Insets(0, 0, 0, 0);
            c.gridx = ++column;
            add(columnsScale, c);
        }
    }

    private void addRowsTree(int column, int row) {
        //we call constructor just one
        if (rowsTree == null) {
            rowsTree = new VerticalTree(this);
            dendroViewer.addDendrogramDataListener(rowsTree);
            //listen to cluster selection
            rowsTree.addTreeListener(heatmap);
            cutoff = new CutoffLine(this, rowsTree);
            dendroViewer.addDendrogramDataListener(cutoff);
        }
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0;
        c.weighty = 0;
        //c.gridwidth = GridBagConstraints.RELATIVE;
        //c.gridheight = GridBagConstraints.REMAINDER; //last in column
        c.insets = new java.awt.Insets(0, 5, 0, 0);
        c.gridx = column;
        c.gridy = row;

        treeLayered = new JLayeredPane();
        treeLayered.setLayout(new LayoutManager() {
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

        treeLayered.add(cutoff, 0); //lower level
        treeLayered.add(rowsTree, 1); //upper level
        add(treeLayered, c);

        if (showScale) {
            if (rowsScale == null) {
                rowsScale = new VerticalScale(rowsTree, this);
                dendroViewer.addDendrogramDataListener(rowsScale);
            }
            c.insets = new java.awt.Insets(0, 0, 0, 0);
            c.gridy = ++row;
            add(rowsScale, c);
        }
    }

    private void addRowSlider(int column, int row) {
        if (slider == null) {
            slider = new CutoffSlider(this, SwingConstants.HORIZONTAL, cutoff);
            dendroViewer.addDendrogramDataListener(slider);
            rowsTree.addTreeListener(slider);
        }
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.SOUTH;
        c.insets = new java.awt.Insets(0, 0, 0, 0);
        c.gridx = column;
        c.gridy = row;
        add(slider, c);

    }

    private void addRowAnnotation(int column, int row, boolean visible) {
        if (!visible) {
            if (rowAnnotationBar != null) {
                rowsTree.removeTreeListener(rowAnnotationBar);
                dendroViewer.removeDendrogramDataListener(rowAnnotationBar);
                rowAnnotationBar = null;
            }
        } else {
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
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.NORTHWEST;
            /**
             * at least one component must be stretching in the free space or
             * there must be some glue to fill the empty space (if no,
             * components would be centered to middle)
             */
            c.weightx = 1.0;
            c.weighty = 1.0;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = new java.awt.Insets(0, 0, 0, 0);
            c.gridx = column;
            c.gridy = row;
            add(rowAnnotationBar, c);
        }
    }

    private void addLegend(int column, int row) {
        //we call constructor just one
        if (legend == null) {
            legend = new Legend(this);
        }
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new java.awt.Insets(0, 0, 0, 0);
        c.gridx = column;
        c.gridy = row;
        add(legend, c);
    }

    private void addColumnAnnotationBar(int column, int row) {
        //we call constructor just one
        if (columnAnnotationBar == null) {
            columnAnnotationBar = new ColumnAnnotation(this);
            dendroViewer.addDendrogramDataListener(columnAnnotationBar);
            rowsTree.addTreeListener(columnAnnotationBar);
        }
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0.0;
        //component in last row should be streatched to fill space at the bottom
        c.weighty = 1.0;
        // c.gridwidth = GridBagConstraints.REMAINDER;
        //  c.gridheight = GridBagConstraints.REMAINDER;
        c.insets = new java.awt.Insets(5, 0, 0, 0);
        c.gridx = column;
        c.gridy = row;
        add(columnAnnotationBar, c);
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

    private void addClustersAssignment(int column, int row) {
        //we call constructor just one
        if (clusterAssignment == null) {
            //heatmap annotations
            clusterAssignment = new ClusterAssignment(this);
            dendroViewer.addDendrogramDataListener(clusterAssignment);
            dendroViewer.addClusteringListener(clusterAssignment);
        }
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        /**
         * at least one component must be stretching in the free space or there
         * must be some glue to fill the empty space (if no, components would be
         * centered to middle)
         */
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.insets = new java.awt.Insets(0, 5, 0, 5);
        c.gridx = column;
        c.gridy = row;
        add(clusterAssignment, c);
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramData dataset) {
        System.out.println("DendroPanel: dataset changed");
        this.dendroData = dataset;
        rowsTree.fireTreeUpdated();
        columnsTree.fireTreeUpdated();
        //@TODO probably call repaint
        repaint();
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
        width += columnsScale.getWidth();
        System.out.println("new requested width= "+width);
        System.out.println("rows treee "+rowsTree.getWidth()+" size: "+rowsTree.getSize());
        size.width = width;
    }

    private void updateHeight(int elemHeight) {
        //height of heatmap
        int height = dendroData.getNumberOfRows() * elemHeight;
        if (showColumnsTree) {
            height += columnsTree.getHeight();
        }
        height += insets.bottom + insets.top;
        if (columnAnnotationBar != null) {
            int colsize = Math.max(columnAnnotationBar.size.height, rowsScale.getHeight());
            height += colsize;
        }
        size.height = height;
    }

    private void setSizes() {
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
        return rowAnnotationBar.getWidth();
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
        updateHeight(height);
        setSizes();
    }

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
}
