package org.clueminer.dendrogram.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dendrogram.DendrogramData;
import org.clueminer.dendrogram.events.DendrogramDataEvent;
import org.clueminer.dendrogram.events.DendrogramDataListener;
import org.clueminer.gui.ColorGenerator;

/**
 *
 * @author Tomas Barton
 */
public class ClusterAssignment extends JPanel implements DendrogramDataListener, ClusteringListener {

    private static final long serialVersionUID = 7662186965958650502L;
    private DendrogramData dataset;
    private Dimension size = new Dimension(0, 0);
    private DendrogramPanel panel;
    private int stripeWidth = 20;
    private boolean isDrawBorders = true;
    private BufferedImage bufferedImage;
    private Graphics2D bufferedGraphics;
    private Insets insets = new Insets(0, 15, 0, 10);

    public ClusterAssignment(DendrogramPanel panel) {
        this.panel = panel;
        setBackground(panel.bg);
    }

    protected void setDimension(int width, int height) {
        //if there is some change
        if (width != this.size.width || height != this.size.height) {
            this.size.width = width;
            this.size.height = height;
            /**
             * we want exactly this size, when windows is enlarged the heatmap
             * should be bigger
             */
            setPreferredSize(this.size);
            setMinimumSize(this.size);
            setSize(this.size);
        }
    }

    private void updateSize() {
        int width = 0;
        int height = 0;
        if (dataset != null) {
            width = stripeWidth;
            height = panel.getElementSize().height * dataset.getNumberOfRows() + 2;
        }
        setDimension(width, height);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (panel.dendroData == null) {
            return;
        }

        if (size.width <= 0 || size.height <= 0) {
            return;
        }

        //create color palette
        if (bufferedImage == null) {
            drawData(size.width, size.height);
        }
        Graphics2D g2d = (Graphics2D) g;
        //places color bar to canvas
        g2d.drawImage(bufferedImage,
                insets.left, insets.top,
                size.width, size.height,
                null);
        g2d.dispose();
    }

    private void drawData(int width, int height) {
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferedGraphics = bufferedImage.createGraphics();

        if (dataset != null) {
            HierarchicalResult clustering = dataset.getRowsResult();
            int[] clusters = clustering.getClusters(dataset.getNumberOfRows());
            int i = 0;
            int currClust = clusters[i];
            int start = 0;
            int x = 0;
            while (i < clusters.length) {
                if (clusters[clustering.getMappedIndex(i)] != currClust) {
                    drawCluster(bufferedGraphics, x, start, i, false);
                    start = i; //index if new cluster start
                    currClust = clusters[clustering.getMappedIndex(i)];
                }
                i++;
            }
            //close unfinished cluster
            drawCluster(bufferedGraphics, x, start, i, true);
        }
        bufferedGraphics.dispose(); //finished drawing
    }

    private void drawCluster(Graphics g, int x, int start, int end, boolean isLast) {
        int y = start * elemHeight();
        int y2 = (end - start) * elemHeight();
        if (this.isDrawBorders) {
            if (y == 0) {
                g.setColor(ColorGenerator.getRandomColor());
                g.fillRect(x + 1, y + 1, stripeWidth - 2, y2 - 2);
                g.setColor(Color.black);
                g.drawRect(x, y, stripeWidth - 1, y2 - 1);
            } else {
                g.setColor(ColorGenerator.getRandomColor());
                g.fillRect(x + 1, y - 1, stripeWidth - 2, y2);
                g.setColor(Color.black);
                g.drawRect(x, y - 1, stripeWidth - 1, y2);

            }
        }
    }

    private int elemHeight() {
        return panel.elementSize.height;
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramData dataset) {
        this.dataset = dataset;
        updateSize();
        bufferedImage = null; //clear cached image
        repaint();
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        //we don't care
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        updateSize();
        bufferedImage = null; //clear cached image
        repaint();
    }

    private void invalidateCache() {
        bufferedImage = null;
    }

    @Override
    public void clusteringChanged(Clustering clust) {
        invalidateCache();
        repaint();
    }

    @Override
    public void resultUpdate(HierarchicalResult hclust) {
        //
    }
}
