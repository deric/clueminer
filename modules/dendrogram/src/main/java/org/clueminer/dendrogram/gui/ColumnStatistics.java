package org.clueminer.dendrogram.gui;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.clustering.api.dendrogram.TreeCluster;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.stats.AttrNumStats;

/**
 *
 * @author Tomas Barton
 */
public class ColumnStatistics extends JPanel implements DendrogramDataListener, TreeListener {

    private static final long serialVersionUID = 6398091816522414014L;
    private int[] columnsOrder;
    private boolean isAntiAliasing = true;
    protected Dimension elementSize;
    private DendroPane panel;
    private int maxHeight;
    protected Dimension size = new Dimension(1, 1);
    private BufferedImage bufferedImage;
    private Graphics2D g;
    private DendrogramMapping dataset;
    private int firstSelectedColumn = -1;
    private int lastSelectedColumn = -1;
    private Font defaultFont;
    private int lineHeight = 11;
    private int lineSpacing = 2;
    private int fontSize = 10;

    public ColumnStatistics(DendroPane p) {
        this.panel = p;
        this.elementSize = panel.getElementSize();
        setBackground(Color.PINK);
        defaultFont = new Font("verdana", Font.PLAIN, fontSize);
    }

    private void createBufferedGraphics() {

        if (dataset == null) {
            return;
        }
        bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        g = bufferedImage.createGraphics();
        this.setOpaque(true);
        Dataset<? extends Instance> data = dataset.getDataset();
        // clear the panel
        //  g.setColor(panel.bg);
        //  g.fillRect(0, 0, size.width, size.height);

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        if (this.isAntiAliasing) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        //we draw strings in rows and then we rotate the whole image
        if (columnsOrder != null) {
            g.setColor(Color.black);
            int coordX;
            g.setFont(defaultFont);
            FontRenderContext frc = g.getFontRenderContext();
            FontMetrics fm = g.getFontMetrics();
            int height = fm.getHeight();
            maxHeight = (lineHeight + lineSpacing) * 4;

            for (int col = 0; col < dataset.getNumberOfColumns(); col++) {
                coordX = (col + 1) * elementSize.width - elementSize.width / 2 - height / 2;
                Attribute a = data.getAttribute(this.columnsOrder[col]);
                String s = panel.formatNumber(a.statistics(AttrNumStats.MIN));
                drawString(col, frc, s, coordX, 1);
                s = panel.formatNumber(a.statistics(AttrNumStats.MAX));
                drawString(col, frc, s, coordX, 2);
                s = panel.formatNumber(a.statistics(AttrNumStats.AVG));
                drawString(col, frc, s, coordX, 3);
                s = panel.formatNumber(a.statistics(AttrNumStats.STD_DEV));
                drawString(col, frc, s, coordX, 4);
            }

        }
        g.dispose();
    }

    private void drawString(int col, FontRenderContext frc, String s, int coordX, int row) {
        Font f;
        if (col == firstSelectedColumn) {
            f = defaultFont.deriveFont(defaultFont.getStyle() ^ Font.BOLD);
            g.setFont(f);
        }

        //  int width = (int) (g.getFont().getStringBounds(s, frc).getWidth());
        int y = lineHeight * row + lineSpacing;

        g.drawString(s, coordX, y);
        if (col == lastSelectedColumn) {
            g.setFont(defaultFont);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (bufferedImage == null) {
            createBufferedGraphics(); // } //cached image
        }
        g.drawImage(bufferedImage, 0, 0, size.width, size.height, null);
    }

    public void redraw() {

        Graphics gr = this.getGraphics();

        gr.drawImage(bufferedImage, 0, 0, size.width, size.height, null);

    }

    /**
     * Sets a new element height.
     */
    public void setLineHeight(int height) {
        this.lineHeight = height;
    }

    public void setFontSize(int size) {
        if (size > 6) {
            this.fontSize = size;
        }
    }

    /**
     * Updates the bar sizes.
     */
    private void updateSize() {
        int width, height = 150 + maxHeight;
        width = elementSize.width * dataset.getNumberOfColumns() + 1;

        this.size.width = width;
        this.size.height = height;
        //System.out.println("setting columns legend to " + this.size);
        setMinimumSize(this.size);
        setSize(this.size);
        setPreferredSize(size);
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset) {
        this.dataset = dataset;
        updateSize();
        createBufferedGraphics();
        repaint();
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        this.elementSize.width = width;
        updateSize();
        if (isAdjusting) {
            redraw();
        } else {
            createBufferedGraphics();
        }
        repaint();
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
    public void leafOrderUpdated(Object source, HierarchicalResult mapping) {
        if (source != this) {
            columnsOrder = mapping.getMapping();
            repaint();
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
