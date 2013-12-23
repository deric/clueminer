package org.clueminer.dendrogram.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dendrogram.DendroPane;
import org.clueminer.dendrogram.DendrogramData;
import org.clueminer.dendrogram.events.DendrogramDataEvent;
import org.clueminer.dendrogram.events.DendrogramDataListener;
import org.clueminer.dendrogram.tree.AbstractTree;

/**
 *
 * @author Tomas Barton
 */
public class CutoffLine extends JPanel implements DendrogramDataListener {

    private static final long serialVersionUID = -8874221664051165124L;
    private DendroPane panel;
    private AbstractTree tree;
    private HierarchicalResult clustering;
    final static float dash1[] = {10.0f};
    private int linepos;
    final static BasicStroke dashed
            = new BasicStroke(1.0f,
                              BasicStroke.CAP_BUTT,
                              BasicStroke.JOIN_MITER,
                              10.0f, dash1, 0.0f);

    public CutoffLine(DendroPane p, AbstractTree tree) {
        this.panel = p;
        this.tree = tree;
        this.setOpaque(false); //don't paint background (parent component is responsible for that)
    }

    private void drawLine(Graphics2D g2) {
        g2.setColor(Color.RED);

        //no data available
        if (clustering == null) {
            return;
        }

        linepos = computePosition(clustering.getCutoff());
        g2.setStroke(dashed);
        // x1, y1, x2, y2
        g2.drawLine(linepos, 0, linepos, tree.getSize().height);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        //@TODO use cached graphics
        if (tree != null && tree.hasData()) {
            drawLine(g2);
        }
        g2.dispose();
    }

    /**
     * Computes position of line on the dendrogram tree
     *
     * @param cutoff
     *
     * @return
     */
    private int computePosition(double cutoff) {
        return (int) (tree.getMaxDistance() - (cutoff / clustering.getMaxTreeHeight() * tree.getMaxDistance()));
    }

    private double computeCutoff(int pos) {
        //min tree distance is distance of lowest level, not leaves!
        double cut = (pos - tree.getMinDistance()) * clustering.getMaxTreeHeight() / (tree.getMaxDistance() - tree.getMinDistance());
        return (clustering.getMaxTreeHeight() - cut);
    }

    public int getLinePosition() {
        return linepos;
    }

    public double getMaxDistance() {
        return tree.getMaxDistance();
    }

    public double getMinDistance() {
        return tree.getMinDistance();
    }

    /**
     * Cutoff is in range from 0 to max_pixels of dendrogram tree
     *
     * @param pos
     * @param isAdjusting
     */
    public void setCutoff(int pos, boolean isAdjusting) {
        double cut = computeCutoff(pos);
        if (!isAdjusting) {
            //quite expensive to compute
            clustering.setCutoff(cut);
            //@TODO depending on horizontal or vertical position we shoud choose rows or columns
            panel.fireClusteringChanged(panel.getDendrogramData().getRowsClustering());
        }
        repaint();
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramData dataset) {
        this.clustering = dataset.getRowsResult();
        updateSize();
        repaint();
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        //we don't care
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        updateSize();
    }

    protected void updateSize() {
        setPreferredSize(tree.getSize());
        setMinimumSize(tree.getSize());
        setSize(tree.getSize());
    }
}
