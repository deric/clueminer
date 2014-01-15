package org.clueminer.dendrogram.gui;

import java.awt.*;
import java.awt.font.FontRenderContext;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.clustering.api.dendrogram.TreeCluster;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.dendrogram.tree.VerticalTree;

public class RowAnnotation extends AbstractAnnotation implements DendrogramDataListener, TreeListener {

    private static final long serialVersionUID = -1724976266346629940L;
    private int[] rowsOrder;
    private int maxWidth;
    private int firstSelectedRow = -1;
    private int lastSelectedRow = -1;
    private static final String unknownLabel = "(unknown)";

    public RowAnnotation(DendroPane p) {
        super(p);
    }

    @Override
    protected void render(Graphics2D g) {
        if (rowsOrder != null) {
            g.setColor(Color.black);
            float annY;
            g.setFont(defaultFont);
            FontRenderContext frc = g.getFontRenderContext();
            FontMetrics fm = g.getFontMetrics();
            Font f;
            int ascent = fm.getMaxAscent();
            int descent = fm.getDescent();
            /*
             * Fonts are not scaling lineraly
            
             *---------------ascent
             *
             * FONT
             * ----- baseline
             *
             * --------------descent
             *
             */
            double offset = (elementSize.height / 2.0) + ((ascent - descent) / 2.0);
            for (int row = 0; row < dendroData.getNumberOfRows(); row++) {
                annY = (float) (row * elementSize.height + offset);
                String s = dendroData.getRowsResult().getInstance(row).getName();
                if (s == null) {
                    s = unknownLabel;
                }
                if (row == firstSelectedRow) {
                    f = defaultFont.deriveFont(defaultFont.getStyle() ^ Font.BOLD);
                    g.setFont(f);
                }

                int width = (int) (g.getFont().getStringBounds(s, frc).getWidth());
                checkMax(width);
                g.drawString(s, 0, annY);

                if (row == lastSelectedRow) {
                    g.setFont(defaultFont);
                }
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
            height = elementSize.height * dendroData.getNumberOfRows() + 1;

        }
        this.size.width = width;
        this.size.height = height;
        setMinimumSize(size);
        setPreferredSize(size);
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

    @Override
    public void clusterSelected(DendrogramTree source, TreeCluster cluster, DendrogramMapping data) {
        if (source instanceof VerticalTree) {
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
}
