package org.clueminer.evaluation.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.util.Collection;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.gui.BPanel;

/**
 *
 * @author Tomas Barton
 */
public class SortedClusterings extends BPanel {

    private ClusterEvaluation evaluatorX;
    private ClusterEvaluation evaluatorY;
    private Collection<? extends Clustering> clusterings;
    protected Font defaultFont;
    protected int lineHeight = 12;
    protected int elemHeight = 20;
    protected int fontSize = 10;
    private int maxWidth;
    private Insets insets = new Insets(5, 5, 5, 5);

    public SortedClusterings() {
        //setBackground(Color.red);
        defaultFont = new Font("verdana", Font.PLAIN, fontSize);
        this.fitToSpace = false;
        this.preserveAlpha = true;
    }

    void setEvaluator(ClusterEvaluation provider) {
        this.evaluatorX = provider;
    }

    void setEvaluatorY(ClusterEvaluation provider) {
        this.evaluatorY = provider;
    }

    public void setClusterings(Collection<? extends Clustering> clusterings) {
        this.clusterings = clusterings;
        clusteringChanged();
    }

    protected void clusteringChanged() {
        if (clusterings != null) {
            //           Clustering[] clust = new Clustering[5];
//            Arrays.sort(clust, Collections.reverseOrder());
        }
        resetCache();
    }

    private int itemsCnt() {
        if (clusterings == null) {
            return 0;
        }
        return clusterings.size();
    }

    @Override
    public void render(Graphics2D g) {

        g.setColor(Color.BLACK);
        g.setFont(defaultFont);
        FontRenderContext frc = g.getFontRenderContext();
        FontMetrics fm = g.getFontMetrics();
        Font f;
        String str;
        int width;
        float x = 0.0f, y;
        int row = 0;
        for (Clustering c : clusterings) {
            str = c.getName();
            if (str == null) {
                str = "unknown |" + c.size() + "|";
            }

            width = (int) (g.getFont().getStringBounds(str, frc).getWidth());
            checkMax(width);
            y = (row * elemHeight + elemHeight / 2f + fm.getDescent() / 2f);
            g.drawString(str, x, y);
            row++;
        }
        g.dispose();
    }

    private void checkMax(int width) {
        if (width > maxWidth) {
            maxWidth = width;
            resetCache();
        }
    }

    @Override
    public void sizeUpdated(Dimension size) {
        System.out.println("component " + size);
        //realSize.width = size.width;
        //realSize.height = size.height;
        if (hasData()) {
            int h = (size.height - insets.top - insets.bottom) / (itemsCnt() + 1);
            if (h > 0) {
                System.out.println("new height = " + h);
                elemHeight = h;
                fontSize = (int) (0.8 * elemHeight);
                defaultFont = defaultFont.deriveFont(Font.PLAIN, fontSize);
            }
        }
    }

    @Override
    public boolean hasData() {
        return clusterings != null;
    }

    @Override
    public void recalculate() {
        int width = 40 + maxWidth;
        int height = 0;
        if (elemHeight > lineHeight) {
            height = elemHeight * clusterings.size();
        }
        realSize.width = width;
        //reqSize.width = width;
        realSize.height = height;
        //reqSize.height = height;
        //setMinimumSize(realSize);
        //setPreferredSize(realSize);
        //setSize(realSize);

    }

    @Override
    public boolean isAntiAliasing() {
        return true;
    }

}
