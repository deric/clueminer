package org.clueminer.dendrogram.gui;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;

/**
 * Color stripe for showing assignments to clusters
 *
 * @author Tomas Barton
 */
public class ClusterAssignment extends JPanel implements DendrogramDataListener, ClusteringListener {

    private static final long serialVersionUID = 7662186965958650502L;
    private DendrogramMapping dendroData;
    private Dimension size = new Dimension(0, 0);
    private final DendroPane panel;
    private int stripeWidth = 20;
    private int maxTextWidth = 30;
    private boolean drawBorders = true;
    private boolean drawLabels = true;
    private BufferedImage bufferedImage;
    private Graphics2D buffGr;
    private final Insets insets = new Insets(0, 15, 0, 10);
    private static final Logger logger = Logger.getLogger(ClusterAssignment.class.getName());
    private Font font = new Font("verdana", Font.BOLD, 12);
    private int lineHeight;
    private final int labelOffset = 5;
    private Clustering<Cluster> flatClust;

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
            width = stripeWidth + 2 * labelOffset + maxTextWidth;
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
        buffGr = bufferedImage.createGraphics();
        buffGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        buffGr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (dendroData != null) {
            HierarchicalResult clustering = dendroData.getRowsResult();
            flatClust = dendroData.getRowsClustering();
            int[] clusters = clustering.getClusters(dendroData.getNumberOfRows());
            int i = 0;
            if (clusters.length == 0) {
                logger.log(Level.INFO, "clusters size is 0!!!");
                return;
            }
            buffGr.setFont(font);
            FontMetrics fm = buffGr.getFontMetrics();
            lineHeight = fm.getHeight();
            int currClust = clusters[i];
            int start = 0;
            int x = 0;
            int mapped;
            while (i < clusters.length) {
                mapped = clustering.getMappedIndex(i);
                if (clusters[mapped] != currClust) {
                    drawCluster(buffGr, x, start, i, currClust);
                    start = i; //index if new cluster start
                    currClust = clusters[clustering.getMappedIndex(i)];
                }
                i++;
            }
            //close unfinished cluster
            drawCluster(buffGr, x, start, i, currClust);
        }
        buffGr.dispose(); //finished drawing
    }

    private void drawCluster(Graphics2D g, int x, int start, int end, int clusterNum) {
        int y = start * elemHeight();
        Color color;
        if (flatClust.hasAt(clusterNum - 1)) {
            color = flatClust.get(clusterNum - 1).getColor();
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
            drawLabel(g, y + y2 / 2.0, clusterNum, frc);
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
            xLabel = stripeWidth + labelOffset;
            //logger.log(Level.INFO, "label {0} at {1}, {2}", new Object[]{label, xLabel, yLabel});
            g.drawString(label, (float) xLabel, (float) yLabel);
        }
    }

    private int elemHeight() {
        return panel.getElementSize().height;
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dendroData) {
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
        createBufferedGraphics();
    }

    @Override
    public void clusteringChanged(Clustering clust) {
        createBufferedGraphics();
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

    public boolean isDrawBorders() {
        return drawBorders;
    }

    public void setDrawBorders(boolean drawBorders) {
        this.drawBorders = drawBorders;
    }

    private void checkMax(int width) {
        if (width > maxTextWidth) {
            maxTextWidth = width;
            updateSize();
            createBufferedGraphics();
        }
    }

    private void createBufferedGraphics() {
        bufferedImage = null; //clear cached image
        repaint();
    }
}
