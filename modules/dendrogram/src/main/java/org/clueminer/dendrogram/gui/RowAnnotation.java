package org.clueminer.dendrogram.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.clustering.api.dendrogram.TreeCluster;
import org.clueminer.clustering.api.dendrogram.TreeListener;

public class RowAnnotation extends AbstractAnnotation implements DendrogramDataListener, TreeListener {

    private static final long serialVersionUID = -1724976266346629940L;
    private int[] rowsOrder;
    private int maxWidth;
    private int firstSelectedRow = -1;
    private int lastSelectedRow = -1;

    public RowAnnotation(DendroPane p) {
        super(p);
    }

    @Override
    protected void render(Graphics2D g) {
        if (!hasData()) {
            return;
        }
        g.setColor(Color.BLACK);
        g.setFont(defaultFont);
        FontRenderContext frc = g.getFontRenderContext();
        FontMetrics fm = g.getFontMetrics();
        Font f;
        String str;
        int width;
        float x = 0.0f, y;
        for (int row = 0; row < dendroData.getNumberOfRows(); row++) {
            str = dendroData.getRowsResult().getVector(row).getName();
            if (str == null) {
                //if name empty, try to use ID
                str = dendroData.getRowsResult().getInstance(row).getId();
                if (str == null) {
                    //index is number of line in dataset, starting from 0
                    str = String.valueOf(dendroData.getRowsResult().getVector(row).getIndex());
                }
            }
            if (row == firstSelectedRow) {
                f = defaultFont.deriveFont(defaultFont.getStyle() ^ Font.BOLD);
                g.setFont(f);
            }

            width = (int) (g.getFont().getStringBounds(str, frc).getWidth());
            checkMax(width);
            y = (row * elementSize.height + elementSize.height / 2f + fm.getDescent() / 2f);
            g.drawString(str, x, y);

            if (row == lastSelectedRow) {
                g.setFont(defaultFont);
            }
        }
    }

    private void checkMax(int width) {
        if (width > maxWidth) {
            maxWidth = width;
            updateSize();
            createBufferedGraphics();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bufferedImage == null) {
            createBufferedGraphics();
        }
        //cached image
        g.drawImage(bufferedImage,
                0, 0,
                size.width, size.height,
                null);
    }

    /**
     * Updates the bar sizes.
     */
    @Override
    protected void updateSize() {
        int width = 40 + maxWidth;
        int height;
        if (elementSize.height < lineHeight) {
            //no need to display unreadable text
            visible = false;
            width = 0;
            height = 0;
            bufferedImage = null;
        } else {
            visible = true;
            if (dendroData != null) {
                height = lineHeight * dendroData.getNumberOfRows() + 1;
            } else {
                height = lineHeight * 2 + 1;
            }
        }
        this.size.width = width;
        this.size.height = height;
        setMinimumSize(size);
        setPreferredSize(size);
        setSize(size);
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dendroData) {
        setDendrogramData(dendroData);
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        //we don't care about this
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        setLineHeight(height);
        updateSize();
        if (!isAdjusting) {
            createBufferedGraphics();
        }
        resetCache();
    }

    private void selectRows(int first, int last) {
        this.firstSelectedRow = first;
        this.lastSelectedRow = last;
        createBufferedGraphics();
        redraw();
    }

    public void setElement(int dimension) {
        //TODO: support vertical orientation
        setLineHeight(dimension);
        updateSize();
        createBufferedGraphics();
        resetCache();
    }

    @Override
    public void clusterSelected(DendrogramTree source, TreeCluster cluster, DendrogramMapping data) {
        if (source.isVertical()) {
            this.selectRows(cluster.firstElem, cluster.lastElem);
        } else {
            //horizontal tree
        }
    }

    @Override
    public int getWidth() {
        return this.size.width;
    }

    @Override
    public void treeUpdated(DendrogramTree source, int width, int height) {
        //we don't care about this
    }

    @Override
    public void setDendrogramData(DendrogramMapping dendroData) {
        this.dendroData = dendroData;
        if (dendroData.hasRowsClustering()) {
            rowsOrder = dendroData.getRowsResult().getMapping();
            resetCache();
        }
    }

    @Override
    public void leafOrderUpdated(Object source, HierarchicalResult mapping) {
        if (source != this) {
            rowsOrder = mapping.getMapping();
            resetCache();
        }
    }

    @Override
    public boolean hasData() {
        return dendroData != null && dendroData.hasRowsClustering() && rowsOrder != null;
    }
}
