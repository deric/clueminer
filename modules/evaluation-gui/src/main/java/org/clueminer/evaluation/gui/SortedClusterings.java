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
import org.clueminer.clustering.api.dendrogram.ColorScheme;
import org.clueminer.clustering.gui.colors.ColorSchemeImpl;
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
    static BasicStroke wideStroke = new BasicStroke(8.0f);
    private double strokeW;
    private ColorScheme colorScheme;
    private double minDist;
    private double midDist;
    private double maxDist;

    public SortedClusterings() {
        defaultFont = new Font("verdana", Font.PLAIN, fontSize);
        this.fitToSpace = false;
        this.preserveAlpha = true;
        cLeft = new ClusteringComparator(new AICScore());
        cRight = new ClusteringComparator(new AICScore());
        colorScheme = new ColorSchemeImpl(Color.green, Color.BLACK, Color.RED);
    }

    void setEvaluatorX(ClusterEvaluation provider) {
        cLeft.setEvaluator(provider);
        if (left != null && left.length > 1) {
            Arrays.sort(left, cLeft);
            clusteringChanged();
        }
    }

    void setEvaluatorY(ClusterEvaluation provider) {
        cRight.setEvaluator(provider);
        if (right != null && right.length > 1) {
            Arrays.sort(right, cRight);
            updateMatching();
            clusteringChanged();
        }
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
        //minimal distance (straight line)
        minDist = xB - xA;

        x1 = maxWidth + 10;
        Line2D.Double line;
        double total = 0.0, dist;

        //draw
        for (int row = 0; row < left.length; row++) {
            //left clustering
            clust = left[row];
            g.setColor(Color.BLACK);
            drawClustering(g, clust, xA, row);

            //right clustering
            rowB = matching.getInt(clust);
            drawClustering(g, clust, xB, rowB);

            g.setStroke(wideStroke);
            y1 = row * elemHeight + elemHeight / 2.0 - strokeW / 2.0;
            y2 = rowB * elemHeight + elemHeight / 2.0 - strokeW / 2.0;
            line = new Line2D.Double(x1, y1, xB, y2);
            dist = distance(x1, y1, xB, y2);
            //System.out.println("dist: " + dist);
            total += dist;
            g.setColor(colorScheme.getColor(dist, minDist, midDist, maxDist));
            g.draw(line);

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
            int h = (size.height - insets.top - insets.bottom) / itemsCnt();
            if (h > 0) {
                elemHeight = h;
                fontSize = (int) (0.8 * elemHeight);
                strokeW = 0.05 * elemHeight;
                wideStroke = new BasicStroke((float) strokeW);
                defaultFont = defaultFont.deriveFont(Font.PLAIN, fontSize);
                minDist = size.width - 2 * maxWidth - insets.left - insets.right - 20;
                maxDist = distance(maxWidth, elemHeight / 2.0, elemHeight * itemsCnt(), size.width - maxWidth);
                midDist = (maxDist + minDist) / 2.0;
                //System.out.println("min = " + minDist);
                //System.out.println("mid = " + midDist);
                //System.out.println("max = " + maxDist);
            }
            //use maximum width avaiable
            realSize.width = size.width;
            maxWidth = 0;
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
