package org.clueminer.evaluation.gui;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.awt.BasicStroke;
import org.clueminer.eval.utils.ClusteringComparator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.math3.util.FastMath;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.eval.AICScore;
import org.clueminer.gui.BPanel;

/**
 *
 * @author Tomas Barton
 */
public class SortedClusterings extends BPanel {

    private Collection<? extends Clustering> clusterings;
    Clustering[] left;
    Clustering[] right;
    ClusteringComparator cLeft;
    ClusteringComparator cRight;
    protected Font defaultFont;
    protected int lineHeight = 12;
    protected int elemHeight = 20;
    protected int fontSize = 10;
    private int maxWidth;
    private Insets insets = new Insets(5, 5, 5, 5);
    private Object2IntOpenHashMap<Clustering> matching;
    final static BasicStroke stroke = new BasicStroke(2.0f);
    final static BasicStroke wideStroke = new BasicStroke(8.0f);

    public SortedClusterings() {
        setBackground(Color.red);
        defaultFont = new Font("verdana", Font.PLAIN, fontSize);
        this.fitToSpace = false;
        this.preserveAlpha = true;
        cLeft = new ClusteringComparator(new AICScore());
        cRight = new ClusteringComparator(new AICScore());
    }

    void setEvaluatorX(ClusterEvaluation provider) {
        cLeft.setEvaluator(provider);
        Arrays.sort(left, cLeft);
        clusteringChanged();
    }

    void setEvaluatorY(ClusterEvaluation provider) {
        cRight.setEvaluator(provider);
        Arrays.sort(right, cRight);
        updateMatching();
        clusteringChanged();
    }

    public void setClusterings(Collection<Clustering> clusterings) {
        this.clusterings = clusterings;
        left = clusterings.toArray(new Clustering[clusterings.size()]);
        Arrays.sort(left, cLeft);

        right = clusterings.toArray(new Clustering[clusterings.size()]);
        Arrays.sort(right, cRight);
        updateMatching();
        clusteringChanged();
    }

    private void updateMatching() {
        matching = new Object2IntOpenHashMap<>();
        for (int i = 0; i < right.length; i++) {
            matching.put(right[i], i);
        }
    }

    protected void clusteringChanged() {
        if (hasData()) {
            resetCache();
        }
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
        float xA = 0.0f, xB = getSize().width - maxWidth;
        Clustering clust;
        int rowB;
        double x1, y1, y2;

        x1 = maxWidth + 10;
        Line2D.Double line;
        double dist = 0.0;

        //draw
        for (int row = 0; row < left.length; row++) {
            //left clustering
            clust = left[row];
            drawClustering(g, clust, xA, row);

            //right clustering
            rowB = matching.getInt(clust);
            drawClustering(g, clust, xB, rowB);

            g.setStroke(wideStroke);
            y1 = row * elemHeight + elemHeight / 2.0 - wideStroke.getLineWidth();
            y2 = rowB * elemHeight + elemHeight / 2.0 - wideStroke.getLineWidth();
            line = new Line2D.Double(x1, y1, xB, y2);
            g.draw(line);
            dist += distance(x1, y1, xB, y2);
            // g.setStroke(wideStroke);
            //  g.draw(new Line2D.Double(10.0, 50.0, 100.0, 50.0));

        }
        //System.out.println("distance: " + dist);
        g.dispose();
    }

    private double distance(double x1, double y1, double x2, double y2) {
        double res = FastMath.pow(x1 - x2, 2) + FastMath.pow(y1 - y2, 2);

        return FastMath.sqrt(res);
    }

    private void drawClustering(Graphics2D g, Clustering clust, float x, int row) {
        String str = clust.getName();
        int width;
        float y;
        if (str == null) {
            str = "unknown |" + clust.size() + "|";
        }

        width = (int) (g.getFont().getStringBounds(str, g.getFontRenderContext()).getWidth());
        checkMax(width);
        y = (row * elemHeight + elemHeight / 2f + g.getFontMetrics().getDescent() / 2f);
        g.drawString(str, x, y);
    }

    private void checkMax(int width) {
        if (width > maxWidth) {
            maxWidth = width;
            resetCache();
        }
    }

    @Override
    public void sizeUpdated(Dimension size) {
        if (hasData()) {
            int h = (size.height - insets.top - insets.bottom) / (itemsCnt() + 1);
            if (h > 0) {
                elemHeight = h;
                fontSize = (int) (0.8 * elemHeight);
                defaultFont = defaultFont.deriveFont(Font.PLAIN, fontSize);
            }
            //use maximum width avaiable
            realSize.width = size.width;
        }
    }

    @Override
    public boolean hasData() {
        return clusterings != null;
    }

    @Override
    public void recalculate() {
        //int width = 40 + maxWidth;
        int height = 0;
        if (elemHeight > lineHeight) {
            height = elemHeight * clusterings.size();
        }
        //realSize.width = width;
        //reqSize.width = width;
        realSize.height = height;
        //reqSize.height = height;
        //setMinimumSize(realSize);
        setPreferredSize(realSize);
        //setSize(realSize);

    }

    @Override
    public boolean isAntiAliasing() {
        return true;
    }

}
