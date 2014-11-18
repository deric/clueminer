package org.clueminer.dendrogram.gui;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.gui.BPanel;

/**
 * Color stripe for showing assignments to clusters
 *
 * @author Tomas Barton
 */
public class ClusterAssignment extends BPanel implements DendrogramDataListener, ClusteringListener {

    private static final long serialVersionUID = 7662186965958650502L;
    private final DendroPane panel;
    protected int stripeWidth = 20;
    private int maxTextWidth = 20;
    protected boolean drawBorders = true;
    private boolean drawLabels = true;
    protected final Insets insets = new Insets(0, 10, 0, 10);
    private static final Logger logger = Logger.getLogger(ClusterAssignment.class.getName());
    protected Font font = new Font("verdana", Font.BOLD, 12);
    protected int lineHeight;
    private final int labelOffset = 5;
    protected Clustering<Cluster> flatClust;
    protected HierarchicalResult hieraRes;

    public ClusterAssignment(DendroPane panel) {
        this.panel = panel;
        setBackground(panel.getBackground());
        //this.preserveAlpha = true;
        //this.fitToSpace = true;
        recalculate();
    }

    protected void drawData(Graphics2D g) {
        if (flatClust != null) {
            HierarchicalResult res = flatClust.getLookup().lookup(HierarchicalResult.class);
            if (res != null) {
                hieraRes = res;
            }
        }
        if (flatClust != null && hieraRes != null) {
            //param for getClusters is not used
            int[] clusters = hieraRes.getClusters(0);
            int i = 0;
            if (clusters == null || clusters.length == 0) {
                logger.log(Level.INFO, "clusters size is 0!!!");
                return;
            }
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            lineHeight = fm.getHeight();
            int currClust = clusters[i];
            int start = 0;
            int x = insets.left;
            int mapped;
            while (i < clusters.length) {
                mapped = hieraRes.getMappedIndex(i);
                if (clusters[mapped] != currClust) {
                    drawCluster(g, x, start, i, currClust);
                    start = i; //index if new cluster start
                    currClust = clusters[mapped];
                }
                i++;
            }
            //close unfinished cluster
            drawCluster(g, x, start, i, currClust);
        }
        g.dispose(); //finished drawing
    }

    private void drawCluster(Graphics2D g, int x, int start, int end, int clusterNum) {
        int y = start * elemHeight();
        Color color;
        if (flatClust.hasAt(clusterNum)) {
            color = flatClust.get(clusterNum).getColor();
        } else {
            color = Color.GRAY;
        }

        int y2 = (end - start) * elemHeight();
        FontRenderContext frc = g.getFontRenderContext();
        if (y == 0) {
            g.setColor(color);
            g.fillRect(x + 1, y + 1, stripeWidth - 2, y2 - 2);
            g.setColor(Color.black);
            if (this.drawBorders) {
                g.drawRect(x, y, stripeWidth - 1, y2 - 1);
            }
        } else {
            g.setColor(color);
            g.fillRect(x + 1, y - 1, stripeWidth - 2, y2);
            g.setColor(Color.black);
            if (this.drawBorders) {
                g.drawRect(x, y - 1, stripeWidth - 1, y2);
            }

        }
        if (y2 > 0) {
            //clusterNum starts from 0, we make it more human readable
            drawLabel(g, y + y2 / 2.0, clusterNum + 1, frc);
        }
    }

    private void drawLabel(Graphics2D g, double y, int clusterNum, FontRenderContext frc) {
        String label;
        int labelWidth;
        double xLabel, yLabel;

        if (drawLabels) {
            label = String.valueOf(clusterNum);
            labelWidth = (int) (g.getFont().getStringBounds(label, frc).getWidth()) + 10;
            checkMax(labelWidth);
            yLabel = y + lineHeight / 2.0;
            xLabel = stripeWidth + labelOffset + insets.left;
            //logger.log(Level.INFO, "label {0} at {1}, {2}", new Object[]{label, xLabel, yLabel});
            g.drawString(label, (float) xLabel, (float) yLabel);
        }
    }

    protected int elemHeight() {
        return panel.getElementSize().height;
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dendroData) {
        hieraRes = dendroData.getRowsResult();
        //flatClust = hieraRes.getClustering();
        resetCache();
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        //we don't care
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        resetCache();
    }

    @Override
    public void clusteringChanged(Clustering clust) {
        flatClust = clust;
        resetCache();
    }

    @Override
    public void resultUpdate(HierarchicalResult hclust) {
        hieraRes = hclust;
        resetCache();
    }

    public boolean isDrawLabels() {
        return drawLabels;
    }

    public void setDrawLabels(boolean drawLabels) {
        this.drawLabels = drawLabels;
    }

    public boolean isDrawBorders() {
        return drawBorders;
    }

    public void setDrawBorders(boolean drawBorders) {
        this.drawBorders = drawBorders;
    }

    private void checkMax(int width) {
        if (width > maxTextWidth) {
            maxTextWidth = width;
            resetCache();
        }
    }

    @Override
    public void render(Graphics2D g) {
        if (!hasData()) {
            return;
        }

        drawData(g);
    }

    @Override
    public void sizeUpdated(Dimension size) {
        realSize = size;

        setPreferredSize(realSize);
        setMinimumSize(realSize);
    }

    @Override
    public boolean hasData() {
        return hieraRes != null;
    }

    @Override
    public final void recalculate() {
        if (hasData()) {
            realSize.width = insets.left + stripeWidth + 2 * labelOffset + maxTextWidth + insets.right;
            realSize.height = insets.top + panel.getElementSize().height * hieraRes.getDataset().size() + 2 + insets.bottom;
            reqSize.width = realSize.width;
            reqSize.height = realSize.height;

            setSize(realSize);
        }
    }

    @Override
    public boolean isAntiAliasing() {
        return true;
    }
}
