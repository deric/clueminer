package org.clueminer.dendrogram.gui;

import java.awt.*;
import java.awt.font.FontRenderContext;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.clustering.api.dendrogram.TreeCluster;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dendrogram.DendrogramData;
import org.clueminer.dendrogram.events.DendrogramDataEvent;
import org.clueminer.dendrogram.events.DendrogramDataListener;
import org.clueminer.dendrogram.tree.HorizontalTree;

public class ColumnAnnotation extends AbstractAnnotation implements DendrogramDataListener, TreeListener {

    private static final long serialVersionUID = -1984233578189090692L;
    private int[] columnsOrder;
    private int maxTextWidth;
    private int firstSelectedColumn = -1;
    private int lastSelectedColumn = -1;

    public ColumnAnnotation(DendrogramPanel p) {
        super(p);
    }

    @Override
    protected void render(Graphics2D g) {
        //we draw strings in rows and then we rotate the whole image

        if (columnsOrder != null) {
            Dataset<? extends Instance> data = dendroData.getInstances();
            g.setColor(Color.black);
            int coordX;
            g.setFont(defaultFont);
            FontRenderContext frc = g.getFontRenderContext();
            FontMetrics fm = g.getFontMetrics();
            Font f;
            int height = fm.getHeight();
            // clockwise 90 degrees 
            g.rotate(Math.PI / 2.0);
            for (int col = 0; col < dendroData.getNumberOfColumns(); col++) {
                coordX = (col + 1) * elementSize.width - elementSize.width / 2 - height / 2;
                Attribute a = data.getAttribute(this.columnsOrder[col]);
                String s = a.getName();
                if (col == firstSelectedColumn) {
                    f = defaultFont.deriveFont(defaultFont.getStyle() ^ Font.BOLD);
                    g.setFont(f);
                }

                int width = (int) (g.getFont().getStringBounds(s, frc).getWidth());
                checkMax(width);

                g.drawString(s, 0, -coordX);
                if (col == lastSelectedColumn) {
                    g.setFont(defaultFont);
                }
            }
            g.rotate(-Math.PI / 2.0);

        }
    }

    private void checkMax(int width) {
        if (width > maxTextWidth) {
            maxTextWidth = width;
            updateSize();
            createBufferedGraphics();
        }
    }

    /**
     * Updates the bar sizes.
     */
    @Override
    protected void updateSize() {
        int width = 50;
        int height = 50 + maxTextWidth;
        if (this.columnsOrder != null) {
            width = elementSize.width * dendroData.getNumberOfColumns() + 1;
        }
        this.size.width = width;
        this.size.height = height;
        setMinimumSize(this.size);
        setSize(this.size);
        setPreferredSize(size);
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramData dataset) {
        setDendrogramData(dataset);
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        if (width < 8) {
            visible = false;
            bufferedImage = null;
        } else {
            visible = true;
            this.elementSize.width = width;
            updateSize();
            if (isAdjusting) {
                redraw();
            } else {
                createBufferedGraphics();
            }
        }
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        //we don't care about this
    }

    private void selectColumns(int first, int last) {
        this.firstSelectedColumn = first;
        this.lastSelectedColumn = last;
        createBufferedGraphics();
        redraw();
    }

    @Override
    public void clusterSelected(DendrogramTree source, TreeCluster cluster, DendrogramMapping data) {
        if (source instanceof HorizontalTree) {
            this.selectColumns(cluster.firstElem, cluster.lastElem);
        } else {
            //vertical tree
        }
    }

    @Override
    public void treeUpdated(DendrogramTree source, int width, int height) {
        //nothing to do
    }

    @Override
    public void setDendrogramData(DendrogramData dendroData) {
        this.dendroData = dendroData;
        if (dendroData.hasColumnsClustering()) {
            columnsOrder = dendroData.getColsResult().getMapping();
            resetCache();
        }
    }
}
