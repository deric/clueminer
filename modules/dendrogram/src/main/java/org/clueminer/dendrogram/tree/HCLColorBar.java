package org.clueminer.dendrogram.tree;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.JPanel;
import org.clueminer.clustering.api.dendrogram.TreeCluster;

public class HCLColorBar extends JPanel {

    private static final int BAR_WIDTH = 50;
    private static final long serialVersionUID = -8727887190804172114L;
    private ArrayList<TreeCluster> clusters = new ArrayList<TreeCluster>();
    private int featuresSize = 0;
    private boolean isAntiAliasing = true;
    private int elementHeight = 15;
    private Color bg = Color.WHITE;

    /**
     * Constructs a
     * <code>HCLColorBar</code> for specified hcl clusters.
     */
    public HCLColorBar() {
        setBackground(bg);
        setFont(new Font("Dialog", Font.PLAIN, elementHeight));
    }

    public ArrayList<TreeCluster> getClusters() {
        return clusters;
    }

    public int getFeaturesSize() {
        return featuresSize;
    }

    public void setClusters(ArrayList<TreeCluster> a) {
        this.clusters = a;
//        updateSize();
        repaint();
    }

    public void setFeaturesSize(int fs) {
        this.featuresSize = fs;
    }
    //EH end additions

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        updateSize(g2);
        this.setOpaque(true);
        g2.setPaintMode();
        // clear the panel
        //  g2.setColor(bg);
        //  g2.fillRect(0, 0, width, height);

        if (this.isAntiAliasing) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        // FontRenderContext frc = g2.getFontRenderContext();

        TreeCluster cluster;
        int x = 10, y, size;
        int COUNT_CLUSTERS = clusters.size();
        for (int i = 0; i < COUNT_CLUSTERS; i++) {
            cluster = clusters.get(i);
            y = cluster.firstElem * this.elementHeight + this.elementHeight / 2;
            size = (cluster.lastElem - cluster.firstElem) * this.elementHeight;
            g2.setColor(cluster.getColor());
            g2.fillRect(x, y, x + BAR_WIDTH, size);
            String s = cluster.getText();
            if (s != null) {
                // float w = (float) (this.getFont().getStringBounds(s, frc).getWidth());
                g2.drawString(s, x + BAR_WIDTH + 10, y + size / 2 + 7);
            }
        }
        //g2.dispose();
    }

    /**
     * Updates the component when clusters were changed.
     */
    public void onClustersChanged(ArrayList<TreeCluster> clusters) {
        this.clusters = clusters;
        //  updateSize();
        repaint();
    }

    /**
     * Sets a new element height.
     */
    private void setElementHeight(int height) {
        this.elementHeight = height;
        setFont(elementHeight);
    }

    private void setFont(int height) {
        setFont(new Font("Dialog", Font.PLAIN, height));
    }

    /**
     * Updates the component sizes.
     */
    private void updateSize(Graphics2D g) {
        int width = 10 + BAR_WIDTH + 10 + getMaxWidth(g);
        int height = this.elementHeight * this.featuresSize + 1;
        setSizes(width, height);
    }

    /**
     * Calculates max description width.
     */
    private int getMaxWidth(Graphics2D g) {
        if (g == null || this.clusters == null) {
            return 0;
        }
        if (this.isAntiAliasing) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        FontMetrics fm = g.getFontMetrics();
        int max = 0;
        String str;
        final int size = this.clusters.size();
        TreeCluster cluster;
        for (int i = 0; i < size; i++) {
            cluster = this.clusters.get(i);
            if (cluster != null) {
                str = cluster.getText() == null ? "" : cluster.getText();
                max = Math.max(max, fm.stringWidth(str)); //
            }
        }
        return max;
    }

    /**
     * Sets the component sizes.
     */
    private void setSizes(int width, int height) {
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
    }
}
