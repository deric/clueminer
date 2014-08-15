package org.clueminer.dendrogram.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroPane;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tomas Barton
 */
public class CutoffLine extends JPanel implements DendrogramDataListener {

    private static final long serialVersionUID = -8874221664051165124L;
    private final DendroPane panel;
    private final DendrogramTree tree;
    private HierarchicalResult clustering;
    final static float dash1[] = {10.0f};
    //start at tree root
    private int linepos = 100;
    final static BasicStroke dashed
            = new BasicStroke(1.0f,
                              BasicStroke.CAP_BUTT,
                              BasicStroke.JOIN_MITER,
                              10.0f, dash1, 0.0f);
    private static final RequestProcessor RP = new RequestProcessor("computing new cutoff");
    private int sliderDiameter = 6;

    public CutoffLine(DendroPane p, DendrogramTree tree) {
        this.panel = p;
        this.tree = tree;
        this.sliderDiameter = p.getSliderDiameter();
        this.setOpaque(false); //don't paint background (parent component is responsible for that)
    }

    private void drawLine(Graphics2D g2) {
        g2.setColor(Color.RED);

        //no data available
        if (clustering == null) {
            return;
        }

        linepos = computePosition(clustering.getCutoff(), clustering);
        g2.setStroke(dashed);
        //draw dashed line across whole tree width
        // x1, y1, x2, y2
        g2.drawLine(linepos, 0, linepos, tree.getTreeWidth());
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
     * @param clustering
     *
     * @return
     */
    protected int computePosition(double cutoff, HierarchicalResult clustering) {
        //there's a gap on tree root side which is equal to sliderDiameter
        int treeHeight = tree.getTreeHeight() - sliderDiameter;
        return (int) (treeHeight - (cutoff / clustering.getMaxTreeHeight() * treeHeight)) + sliderDiameter;
    }

    /**
     * Translates cutoff
     *
     * @param pos position from 0 to 100
     * @return
     */
    private double computeCutoff(int pos) {
        //min tree distance is distance of lowest level, not leaves!
        if (tree != null && tree.hasData()) {
            double cut = (pos * clustering.getMaxTreeHeight() / 100.0);
            //inverse value (slider min is on left)
            return cut;
        } else {
            return 0.0;
        }
    }

    public int getLinePosition() {
        return linepos;
    }

    public double getMaxDistance() {
        return tree.getTreeHeight();
    }

    public double getMinDistance() {
        return tree.getTreeWidth();
    }

    /**
     * Cutoff is in range from 0 to max_pixels of dendrogram tree
     *
     * @param pos
     * @param isAdjusting
     */
    public void setCutoff(int pos, boolean isAdjusting) {
        final double cut = computeCutoff(pos);
        if (!isAdjusting && clustering != null) {
            RequestProcessor.Task task = RP.create(new Runnable() {

                @Override
                public void run() {
                    //quite expensive to compute
                    Clustering c = clustering.updateCutoff(cut);
                    if (c != null) {
                        //@TODO depending on horizontal or vertical position we shoud choose rows or columns
                        panel.fireClusteringChanged(c);
                    }
                }
            });
            RP.post(task);
            repaint();
        }
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset) {
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
