package org.clueminer.dendrogram.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.util.logging.Logger;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.clustering.api.dendrogram.TreeCluster;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

public class ColumnAnnotation extends AbstractAnnotation implements DendrogramDataListener, TreeListener {

    private static final long serialVersionUID = -1984233578189090692L;
    private int[] columnsOrder;
    private int maxTextWidth;
    private int firstSelectedColumn = -1;
    private int lastSelectedColumn = -1;
    private static final Logger log = Logger.getLogger(ColumnAnnotation.class.getName());

    public ColumnAnnotation(DendroPane p) {
        super(p);
    }

    @Override
    protected void render(Graphics2D g) {
        //we draw strings in rows and then we rotate the whole image
        String s;
        if (!hasData()) {
            return;
        }
        Dataset<? extends Instance> data = dendroData.getDataset();
        g.setColor(Color.black);
        int coordX;
        g.setFont(defaultFont);
        FontRenderContext frc = g.getFontRenderContext();
        FontMetrics fm = g.getFontMetrics();
        Font f;
        int height = fm.getHeight();
        int width;
        // clockwise 90 degrees
        g.rotate(Math.PI / 2.0);
        for (int col = 0; col < dendroData.getNumberOfColumns(); col++) {
            coordX = (col + 1) * elementSize.width - elementSize.width / 2 - height / 2;
            Attribute a = data.getAttribute(this.columnsOrder[col]);
            if (a != null) {
                s = a.getName();
            } else {
                s = "(unknown)";
            }
            if (col == firstSelectedColumn) {
                f = defaultFont.deriveFont(defaultFont.getStyle() ^ Font.BOLD);
                g.setFont(f);
            }

            width = (int) (g.getFont().getStringBounds(s, frc).getWidth());
            checkMax(width);

            g.drawString(s, 0, -coordX);
            if (col == lastSelectedColumn) {
                g.setFont(defaultFont);
            }
        }
        g.rotate(-Math.PI / 2.0);

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
            if (dendroData != null) {
                width = elementSize.width * dendroData.getNumberOfColumns() + 1;
            } else {
                width = elementSize.width * 2 + 1;
            }
        }
        this.size.width = width;
        this.size.height = height;
        setMinimumSize(this.size);
        setSize(this.size);
        setPreferredSize(size);
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset) {
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
            createBufferedGraphics();
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
        //TODO: implement selection
    }

    @Override
    public void treeUpdated(DendrogramTree source, int width, int height) {
        //nothing to do
    }

    @Override
    public void setDendrogramData(DendrogramMapping dendroData) {
        this.dendroData = dendroData;
        if (dendroData.hasColumnsClustering()) {
            columnsOrder = dendroData.getColsResult().getMapping();
        } else {
            columnsOrder = createMapping(dendroData.getDataset());
        }
        resetCache();
    }

    @Override
    public void leafOrderUpdated(Object source, HierarchicalResult mapping) {
        if (source != this) {
            columnsOrder = mapping.getMapping();
        } else {
            columnsOrder = createMapping(dendroData.getDataset());
        }
        resetCache();
    }

    private int[] createMapping(Dataset dataset) {
        int[] mapping = new int[dataset.attributeCount()];
        for (int i = 0; i < mapping.length; i++) {
            mapping[i] = i;
        }
        return mapping;
    }

    @Override
    public boolean hasData() {
        return dendroData != null && dendroData.hasColumnsClustering() && columnsOrder != null;
    }
}
