package org.clueminer.dendrogram.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dendrogram.DendroPane;
import org.clueminer.dendrogram.DendrogramData;
import org.clueminer.dendrogram.events.DendrogramDataEvent;
import org.clueminer.dendrogram.events.DendrogramDataListener;
import org.clueminer.gui.ColorGenerator;
import org.clueminer.utils.Dump;

/**
 * Color stripe for showing assignments to clusters
 *
 * @author Tomas Barton
 */
public class ClusterAssignment extends JPanel implements DendrogramDataListener, ClusteringListener {

    private static final long serialVersionUID = 7662186965958650502L;
    private DendrogramData dendroData;
    private Dimension size = new Dimension(0, 0);
    private final DendroPane panel;
    private int stripeWidth = 20;
    private boolean isDrawBorders = true;
    private boolean drawLabels = true;
    private BufferedImage bufferedImage;
    private Graphics2D bufferedGraphics;
    private final Insets insets = new Insets(0, 15, 0, 10);
    private static final Logger logger = Logger.getLogger(ClusterAssignment.class.getName());

    public ClusterAssignment(DendroPane panel) {
        this.panel = panel;
        setBackground(panel.getBackground());
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
        if (dendroData != null) {
            width = stripeWidth;
            height = panel.getElementSize().height * dendroData.getNumberOfRows() + 2;
        }
        setDimension(width, height);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (panel.getDendrogramData() == null) {
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

        if (dendroData != null) {
            HierarchicalResult clustering = dendroData.getRowsResult();
            logger.log(Level.INFO, "number of dataset rows {0}", dendroData.getNumberOfRows());
            int[] clusters = clustering.getClusters(dendroData.getNumberOfRows());
            Dump.array(clusters, "cluster assignments");
            int i = 0;
            if (clusters.length == 0) {
                logger.log(Level.INFO, "clusters size is 0!!!");
                return;
            }
            int currClust = clusters[i];
            int start = 0;
            int x = 0;
            int mapped;
            while (i < clusters.length) {
                mapped = clustering.getMappedIndex(i);
                if (clusters[mapped] != currClust) {
                    drawCluster(bufferedGraphics, x, start, i);
                    start = i; //index if new cluster start
                    currClust = clusters[clustering.getMappedIndex(i)];
                }
                i++;
            }
            //close unfinished cluster
            drawCluster(bufferedGraphics, x, start, i);
        }
        bufferedGraphics.dispose(); //finished drawing
    }

    private void drawCluster(Graphics g, int x, int start, int end) {
        int y = start * elemHeight();
        int y2 = (end - start) * elemHeight();
        if (y == 0) {
            g.setColor(ColorGenerator.getRandomColor());
            g.fillRect(x + 1, y + 1, stripeWidth - 2, y2 - 2);
            g.setColor(Color.black);
            if (this.isDrawBorders) {
                g.drawRect(x, y, stripeWidth - 1, y2 - 1);
            }
        } else {
            g.setColor(ColorGenerator.getRandomColor());
            g.fillRect(x + 1, y - 1, stripeWidth - 2, y2);
            g.setColor(Color.black);
            if (this.isDrawBorders) {
                g.drawRect(x, y - 1, stripeWidth - 1, y2);
            }
        }
    }

    private int elemHeight() {
        return panel.getElementSize().height;
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramData dendroData) {
        this.dendroData = dendroData;
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

    public boolean isDrawLabels() {
        return drawLabels;
    }

    public void setDrawLabels(boolean drawLabels) {
        this.drawLabels = drawLabels;
    }
}
