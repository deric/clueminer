package org.clueminer.evaluation.gui;

import it.unimi.dsi.fastutil.doubles.Double2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.math3.util.FastMath;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.dendrogram.ColorScheme;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.gui.colors.ColorSchemeImpl;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.AICScore;
import org.clueminer.eval.utils.ClusteringComparator;
import org.clueminer.gui.BPanel;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author Tomas Barton
 */
public class SortedClusterings extends BPanel implements TaskListener {

    private static final long serialVersionUID = -4456572592761477081L;

    private Collection<? extends Clustering> clusterings;
    Clustering[] left;
    Clustering[] right;
    ClusteringComparator cLeft;
    ClusteringComparator cRight;
    protected Font defaultFont;
    protected Font headerFont;
    protected int lineHeight = 12;
    protected int elemHeight = 20;
    protected int fontSize = 10;
    private int headerHeight;
    protected float headerFontSize = 10;
    private int maxWidth;
    private Insets insets = new Insets(5, 5, 5, 5);
    private Object2IntOpenHashMap<Clustering> matching;
    private Double2IntOpenHashMap rightScore;
    static BasicStroke wideStroke = new BasicStroke(8.0f);
    private double strokeW;
    private ColorScheme colorScheme;
    private double minDist;
    private double midDist;
    private double maxDist;
    private static final RequestProcessor RP = new RequestProcessor("sorting...", 100, false, true);
    private Object2DoubleOpenHashMap<String> results;
    private Color fontColor;

    public SortedClusterings() {
        defaultFont = new Font("verdana", Font.PLAIN, fontSize);
        headerFont = defaultFont.deriveFont(Font.BOLD);
        this.fitToSpace = false;
        this.preserveAlpha = true;
        cLeft = new ClusteringComparator(new AICScore());
        cRight = new ClusteringComparator(new AICScore());
        colorScheme = new ColorSchemeImpl(Color.green, Color.BLACK, Color.RED);
        results = new Object2DoubleOpenHashMap<>();
        try {
            initialize();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void initialize() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        fontColor = defaults.getColor("controlText");
        setBackground(defaults.getColor("window"));
    }

    void setEvaluatorX(final ClusterEvaluation provider) {
        if (left != null && left.length > 1) {
            final ProgressHandle ph = ProgressHandleFactory.createHandle("computing " + provider.getName());
            RP.post(new Runnable() {

                @Override
                public void run() {
                    ph.start();
                    Arrays.sort(left, new ClusteringComparator(provider));
                    cLeft.setEvaluator(provider);
                    clusteringChanged();
                    ph.finish();
                    results = new Object2DoubleOpenHashMap<>();
                }
            });

        }
    }

    void setEvaluatorY(final ClusterEvaluation provider) {
        if (right != null && right.length > 1) {
            final ProgressHandle ph = ProgressHandleFactory.createHandle("computing " + provider.getName());
            RequestProcessor.Task task = RP.post(new Runnable() {

                @Override
                public void run() {
                    ph.start();
                    ClusteringComparator compare = new ClusteringComparator(provider);
                    try {
                        Arrays.sort(right, compare);
                    } catch (IllegalArgumentException e) {
                        System.err.println("sorting error during " + provider.getName());
                        double[] score = new double[right.length];
                        EvaluationTable et;
                        for (int i = 0; i < score.length; i++) {
                            et = compare.evaluationTable(right[i]);
                            score[i] = et.getScore(provider);
                        }
                        System.out.println(Arrays.toString(score));
                    }
                    cRight.setEvaluator(provider);
                    ph.finish();
                }
            });
            task.addTaskListener(this);
        }
    }

    public void setClusterings(final Collection<Clustering> clusters) {
        RequestProcessor.Task task = RP.post(new Runnable() {

            @Override
            public void run() {
                results = new Object2DoubleOpenHashMap<>();
                left = clusters.toArray(new Clustering[clusters.size()]);
                Arrays.sort(left, cLeft);

                right = clusters.toArray(new Clustering[clusters.size()]);
                Arrays.sort(right, cRight);
                clusterings = clusters;
            }
        });
        task.addTaskListener(this);
    }

    /**
     * Compute sorting distance for all evaluation metrics
     */
    private void computeAll() {
        InternalEvaluatorFactory ief = InternalEvaluatorFactory.getInstance();
        ClusteringComparator compare = new ClusteringComparator();
        for (ClusterEvaluation eval : ief.getAll()) {

            if (!results.containsKey(eval.getName())) {
                System.out.println("computing " + eval.getName());
                compare.setEvaluator(eval);
                try {
                    Arrays.sort(right, compare);
                } catch (IllegalArgumentException e) {
                    System.err.println("sorting error during " + eval.getName());
                }
                updateMatching();
                updateResults();
            }
        }
    }

    private void updateResults() {
        Clustering clust;
        int rowB;

        double total = 0.0, dist;
        int offset = 0;
        double curr = Double.NaN;
        double score;
        //draw
        for (int row = 0; row < left.length; row++) {
            //left clustering
            clust = left[row];
            //right clustering
            rowB = matching.getInt(clust);
            if (clust.getEvaluationTable() != null) {
                score = clust.getEvaluationTable().getScore(cRight.getEvaluator());
                if (curr == score) {
                    offset--;
                } else {
                    offset = rightScore.get(score) + 1;
                    curr = score;
                }
                rowB -= offset;
                rowB += 1;
            }

            //dist = distance(x1, y1, xB, y2);
            //row distance (relative) - difference of row indexes
            dist = Math.abs(row - rowB);
            total += dist;
        }
        double sc = total / (double) left.length;
        results.put(cRight.getEvaluator().getName(), sc);
    }

    @Override
    public void taskFinished(Task task) {
        updateMatching();
        clusteringChanged();
    }

    private void updateMatching() {
        matching = new Object2IntOpenHashMap<>(right.length);
        rightScore = new Double2IntOpenHashMap(right.length);
        int cnt;
        double score;
        Clustering clust;
        for (int i = 0; i < right.length; i++) {
            //row index corresponding to a clustering
            clust = right[i];
            matching.put(clust, i);

            if (clust.getEvaluationTable() != null) {
                score = clust.getEvaluationTable().getScore(cRight.getEvaluator());
                //occurences of given number in table
                if (rightScore.containsKey(score)) {
                    cnt = rightScore.get(score) + 1;
                } else {
                    cnt = 0;
                }
                rightScore.put(score, cnt);
            }
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
        float xA = 0.0f, xB = getSize().width - maxWidth;
        Clustering clust;
        int rowB;
        double x1, y1, y2;
        //minimal distance (straight line)
        //minDist = xB - xA;

        x1 = maxWidth + 10;
        Line2D.Double line;
        double total = 0.0, dist;

        headerHeight = drawHeader(g);
        //set font for rendering rows
        g.setFont(defaultFont);
        int offset = 0;
        double curr = Double.NaN;
        double score;
        //draw
        for (int row = 0; row < left.length; row++) {
            //left clustering
            clust = left[row];
            g.setColor(fontColor);
            drawClustering(g, clust, xA, row, headerHeight);

            //right clustering
            rowB = matching.getInt(clust);
            if (clust.getEvaluationTable() != null) {
                score = clust.getEvaluationTable().getScore(cRight.getEvaluator());
                if (curr == score) {
                    offset--;
                } else {
                    offset = rightScore.get(score) + 1;
                    curr = score;
                }
                rowB -= offset;
                rowB += 1;
            }

            drawClustering(g, clust, xB, rowB, headerHeight);

            g.setStroke(wideStroke);
            y1 = headerHeight + row * elemHeight + elemHeight / 2.0 - strokeW / 2.0;
            y2 = headerHeight + rowB * elemHeight + elemHeight / 2.0 - strokeW / 2.0;
            line = new Line2D.Double(x1, y1, xB, y2);
            //dist = distance(x1, y1, xB, y2);
            //row distance (relative) - difference of row indexes
            dist = Math.abs(row - rowB);
            total += dist;
            g.setColor(colorScheme.getColor(dist, minDist, midDist, maxDist));
            g.draw(line);
            // g.setStroke(wideStroke);
            //  g.draw(new Line2D.Double(10.0, 50.0, 100.0, 50.0));
        }
        g.setColor(fontColor);
        //average distance per item
        double res = total / (double) left.length;
        drawDistance(g, res);
        results.put(cRight.getEvaluator().getName(), res);
        g.dispose();
    }

    /**
     * Euclidean distance between two points
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private double distance(double x1, double y1, double x2, double y2) {
        double res = FastMath.pow(x1 - x2, 2) + FastMath.pow(y1 - y2, 2);

        return FastMath.sqrt(res);
    }

    /**
     *
     * @param g
     * @return height of drawn header
     */
    private int drawHeader(Graphics2D g) {
        g.setColor(fontColor);
        //approx one third
        int colWidth = getSize().width / 3;
        g.setFont(headerFont);
        String eval1 = cLeft.getEvaluator().getName();
        String eval2 = cRight.getEvaluator().getName();
        updateHeaderFont(eval1, eval2, colWidth, g);

        int strWidth = stringWidth(headerFont, g, eval1);
        int x = (colWidth - strWidth) / 2;
        int y = (int) (headerFontSize + g.getFontMetrics().getDescent() * 2);
        g.drawString(eval1, x, y);

        //3rd column
        strWidth = stringWidth(headerFont, g, eval2);
        x = 2 * colWidth + (colWidth - strWidth) / 2;
        g.drawString(eval2, x, y);
        return y + 20;
    }

    private int stringWidth(Font f, Graphics2D g2, String str) {
        return (int) (f.getStringBounds(str, g2.getFontRenderContext()).getWidth());
    }

    private void drawDistance(Graphics2D g2, double distance) {
        g2.setColor(fontColor);
        int colWidth = getSize().width / 3;
        String str = String.format("%.2f", distance);
        g2.setFont(headerFont);
        int strWidth = stringWidth(headerFont, g, str);
        // 2nd column
        int x = colWidth + (colWidth - strWidth) / 2;
        int y = (int) (headerFontSize + g.getFontMetrics().getDescent() * 2);
        g.drawString(str, x, y);
    }

    /**
     * Adjust font size to given 3 columns layout
     *
     * @param s1
     * @param s2
     * @param colWidth
     * @param g2
     */
    private void updateHeaderFont(String s1, String s2, int colWidth, Graphics2D g2) {
        int maxW = Math.max(stringWidth(headerFont, g2, s1), stringWidth(headerFont, g2, s2));
        //decrease font size
        while (maxW > (0.8 * colWidth)) {
            headerFontSize *= 0.9;
            headerFont = headerFont.deriveFont(headerFontSize);
            maxW = Math.max(stringWidth(headerFont, g2, s1), stringWidth(headerFont, g2, s2));
        }
        //increase font
        while (maxW < (0.5 * colWidth)) {
            headerFontSize *= 1.1;
            headerFont = headerFont.deriveFont(headerFontSize);
            maxW = Math.max(stringWidth(headerFont, g2, s1), stringWidth(headerFont, g2, s2));
        }
    }

    private void drawClustering(Graphics2D g, Clustering clust, float x, int row, int yOffset) {
        String str = clust.getName();
        int width;
        float y;
        if (str == null) {
            str = "unknown |" + clust.size() + "|";
        }

        width = stringWidth(defaultFont, g, str);
        checkMax(width);
        y = yOffset + (row * elemHeight + elemHeight / 2f + g.getFontMetrics().getDescent() / 2f);
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
                minDist = 0;
                maxDist = itemsCnt();
                //for euclidean distance
                //minDist = size.width - 2 * maxWidth - insets.left - insets.right - 20;
                //maxDist = distance(maxWidth, elemHeight / 2.0, elemHeight * itemsCnt(), size.width - maxWidth);
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
        int height = headerHeight;
        //elemHeight = (realSize.height - insets.top - insets.bottom) / itemsCnt();
        //if (elemHeight > lineHeight) {
        height += elemHeight * clusterings.size();
        //}
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

    /**
     * Computes sorting distance for all measures available (might take a while)
     *
     * @return
     */
    public Object2DoubleOpenHashMap<String> getResults() {
        //make sure everything is computed
        computeAll();
        return results;
    }

    public Dataset<? extends Instance> getDataset() {
        if (clusterings != null && clusterings.size() > 0) {
            Clustering c = clusterings.iterator().next();
            return c.getLookup().lookup(Dataset.class);
        }
        return null;
    }

    public Collection<? extends Clustering> getClusterings() {
        return clusterings;
    }

}
