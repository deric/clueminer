/*
 * Copyright (C) 2015 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.evaluation.inline;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.dendrogram.ColorScheme;
import org.clueminer.clustering.gui.colors.ColorSchemeImpl;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.AICScore;
import org.clueminer.eval.external.NMI;
import org.clueminer.eval.utils.ClusteringComparator;
import org.clueminer.gui.BPanel;
import org.clueminer.std.StdScale;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author deric
 */
public class ScorePlot extends BPanel implements TaskListener {

    private static final long serialVersionUID = -4456572592761477081L;

    private Collection<? extends Clustering> clusterings;
    private Clustering[] internal;
    private Clustering[] external;
    private ClusteringComparator compInternal;
    private ClusteringComparator compExternal;
    protected Font defaultFont;
    protected Font headerFont;
    protected int lineHeight = 12;
    protected int elemHeight = 20;
    protected int fontSize = 10;
    private int headerHeight;
    protected float headerFontSize = 10;
    private int maxWidth;
    private Insets insets = new Insets(5, 5, 10, 5);
    static BasicStroke wideStroke = new BasicStroke(8.0f);
    private double strokeW;
    private ColorScheme colorScheme;
    private double minDist;
    private double midDist;
    private double maxDist;
    private static final RequestProcessor RP = new RequestProcessor("sorting...", 100, false, true);
    private Color fontColor;
    private final StdScale scale;
    private int scaleTickLength = 6;
    protected DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private int labelOffset = 13;

    public ScorePlot() {
        defaultFont = new Font("verdana", Font.PLAIN, fontSize);
        headerFont = defaultFont.deriveFont(Font.BOLD);
        scale = new StdScale();
        this.fitToSpace = false;
        this.preserveAlpha = true;
        compInternal = new ClusteringComparator(new AICScore());
        compExternal = new ClusteringComparator(new NMI());
        //colorScheme = new ColorSchemeImpl(Color.RED, Color.BLACK, Color.GREEN);
        colorScheme = new ColorSchemeImpl(Color.GREEN, Color.BLACK, Color.RED);
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

    protected void setEvaluatorY(final ClusterEvaluation provider) {
        if (internal != null && internal.length > 1) {
            final ProgressHandle ph = ProgressHandleFactory.createHandle("computing " + provider.getName());
            RP.post(new Runnable() {

                @Override
                public void run() {
                    ph.start();
                    Arrays.sort(internal, new ClusteringComparator(provider));
                    compInternal.setEvaluator(provider);
                    clusteringChanged();
                    ph.finish();
                }
            });

        }
    }

    protected void setEvaluatorX(final ClusterEvaluation provider) {
        if (external != null && external.length > 1) {
            final ProgressHandle ph = ProgressHandleFactory.createHandle("computing " + provider.getName());
            RequestProcessor.Task task = RP.post(new Runnable() {

                @Override
                public void run() {
                    ph.start();
                    ClusteringComparator compare = new ClusteringComparator(provider);
                    try {
                        Arrays.sort(external, compare);
                    } catch (IllegalArgumentException e) {
                        System.err.println("sorting error during " + provider.getName());
                        double[] score = new double[external.length];
                        EvaluationTable et;
                        for (int i = 0; i < score.length; i++) {
                            et = compare.evaluationTable(external[i]);
                            score[i] = et.getScore(provider);
                        }
                        System.out.println(Arrays.toString(score));
                    }
                    compExternal.setEvaluator(provider);
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
                internal = clusters.toArray(new Clustering[clusters.size()]);
                Arrays.sort(internal, compInternal);

                external = clusters.toArray(new Clustering[clusters.size()]);
                Arrays.sort(external, compExternal);
                clusterings = clusters;
            }
        });
        task.addTaskListener(this);
    }

    @Override
    public void taskFinished(Task task) {
        clusteringChanged();
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

    private double scoreMin(Clustering[] clust, ClusteringComparator comp) {
        double res = Double.NaN;
        if (clust != null && clust.length > 0) {
            int i = 0;
            while (Double.isNaN(res) && i < clust.length) {
                res = comp.getScore(clust[i++]);
            }
        } else {
            System.out.println("missing data");
        }

        return res;
    }

    private double scoreMax(Clustering[] clust, ClusteringComparator comp) {
        double res = Double.NaN;
        if (clust != null && clust.length > 0) {
            int i = clust.length - 1;
            while (Double.isNaN(res) && i >= 0) {
                res = comp.getScore(clust[i--]);
            }
        } else {
            System.out.println("missing data");
        }

        return res;
    }

    @Override
    public void render(Graphics2D g) {
        //canvas dimensions
        int cxMin, cxMax, cyMin, cyMax;
        cxMin = insets.left + 20;
        cyMin = insets.top + 15;
        cyMax = getSize().height - insets.bottom;
        int cyMid = (int) ((cyMax - cyMin) / 2) + cyMin;
        cxMax = drawXLabel(g, compInternal.getEvaluator().getName(), getSize().width - insets.right, cyMid);
        int cxMid = (int) ((cxMax - cxMin) / 2) + cxMin;
        drawYLabel(g, compExternal.getEvaluator().getName(), cyMin, cxMid);

        double xmin, xmax, xmid, ymin, ymax, ymid;

        xmin = scoreMin(internal, compInternal);
        xmax = scoreMax(internal, compInternal);
        xmid = (xmax - xmin) / 2.0 + xmin;
        ymin = scoreMin(external, compExternal);
        ymax = scoreMax(external, compExternal);
        ymid = (ymax - ymin) / 2.0 + ymin;

        //set font for rendering rows
        g.setFont(defaultFont);
        double xVal, yVal, score, hypo, diff;
        int rectWidth = 10; //TODO: fix this
        //draw
        Rectangle2D rect;
        drawHorizontalScale(g, cxMin, cxMax, cyMid, xmin, xmax);
        drawVerticalScale(g, cyMin, cyMax, cxMid, ymin, ymax);

        for (Clustering clust : external) {
            //left clustering
            score = compInternal.getScore(clust);
            xVal = scale.scaleToRange(score, xmin, xmax, cxMin, cxMax) - rectWidth / 2;
            score = compExternal.getScore(clust);
            //color according to position difference to external score placement
            hypo = scale.scaleToRange(score, ymin, ymax, cxMin, cxMax) - rectWidth / 2;
            diff = Math.abs(xVal - hypo);
            //last one is min rect. height
            yVal = scale.scaleToRange(score, ymin, ymax, cyMin, cyMax);
            g.setComposite(AlphaComposite.SrcOver.derive(0.5f));
            //g.setColor(colorScheme.getColor(diff, ymin, ymid, ymax));
            g.setColor(colorScheme.getColor(diff, cxMin, cxMid, cxMax));
            if (yVal < cyMid) {
                rect = new Rectangle2D.Double(xVal, yVal, rectWidth, cyMid - yVal);
            } else {
                rect = new Rectangle2D.Double(xVal, cyMid, rectWidth, yVal - cyMid);
            }
            g.fill(rect);
            g.draw(rect);
            g.setComposite(AlphaComposite.SrcOver);
            g.setColor(Color.black);
            //drawNumberX(score, (int) xVal, (int) yVal);
            //drawNumberX(diff, (int) xVal, (int) yVal);
        }
        g.setColor(fontColor);

        //average distance per item
        g.dispose();
    }

    private void drawVerticalScale(Graphics2D g, int cyMin, int cyMax, int xPos, double scMin, double scMax) {
        g.drawLine(xPos, cyMin, xPos, cyMax);
        g.setColor(Color.black);

        //min
        g.drawLine(xPos - scaleTickLength / 2, cyMin, xPos + scaleTickLength / 2, cyMin);
        drawNumberY(scMin, xPos + scaleTickLength, cyMin, g.getFontMetrics());
        //max
        g.drawLine(xPos - scaleTickLength / 2, cyMax, xPos + scaleTickLength / 2, cyMax);
        drawNumberY(scMax, xPos + scaleTickLength, cyMax, g.getFontMetrics());
    }

    private void drawHorizontalScale(Graphics2D g, int cxMin, int cxMax, int yPos, double scMin, double scMax) {
        g.drawLine(cxMin, yPos, cxMax, yPos);
        g.setColor(Color.black);

        //min
        g.drawLine(cxMin, yPos - scaleTickLength / 2, cxMin, yPos + scaleTickLength / 2);
        drawNumberX(scMin, cxMin, yPos + scaleTickLength / 2 + labelOffset);
        //max
        g.drawLine(cxMax, yPos - scaleTickLength / 2, cxMax, yPos + scaleTickLength / 2);
        drawNumberX(scMax, cxMax, yPos + scaleTickLength / 2 + labelOffset);
    }

    private void drawNumberX(double value, int x, int y) {
        String lb = decimalFormat.format(value);
        int sw = stringWidth(defaultFont, g, lb);
        //center the number
        g.drawString(lb, x - sw / 2, y);
    }

    private void drawNumberY(double value, int x, int y, FontMetrics hfm) {
        String lb = decimalFormat.format(value);

        //center the number
        g.drawString(lb, x, y + hfm.getHeight() / 2 - hfm.getDescent());
    }

    /**
     * Compute string width for given string
     *
     * @param f
     * @param g2
     * @param str
     * @return
     */
    private int stringWidth(Font f, Graphics2D g2, String str) {
        return (int) (f.getStringBounds(str, g2.getFontRenderContext()).getWidth());
    }

    /**
     *
     * @param g2
     * @param label
     * @param xmax
     * @param ymid
     * @return position where should x axis end
     */
    private int drawXLabel(Graphics2D g2, String label, int xmax, int ymid) {
        g2.setColor(fontColor);
        g2.setFont(defaultFont);
        int strWidth = stringWidth(defaultFont, g, label);
        int x = xmax - strWidth - 5;
        int y = (int) (ymid - defaultFont.getSize() - g.getFontMetrics().getDescent() * 2.0);
        g.drawString(label, x, y);
        return x;
    }

    private int drawYLabel(Graphics2D g2, String label, int ymax, int xmid) {
        g2.setColor(fontColor);
        g2.setFont(defaultFont);
        int strHeight = g2.getFontMetrics().getHeight() - g2.getFontMetrics().getDescent();
        int strWidth = stringWidth(defaultFont, g2, label);
        int y = ymax - strHeight;
        int x = (int) (xmid - strWidth / 2.0);
        g.drawString(label, x, y);
        return x;
    }

    private void drawDistance(Graphics2D g2, double distance) {
        g2.setColor(fontColor);
        int colWidth = getSize().width / 3;
        String str = String.format("%.2f", distance) + " (" + clusterings.size() + ")";
        g2.setFont(headerFont);
        int strWidth = stringWidth(headerFont, g, str);
        // 2nd column
        int x = colWidth + (colWidth - strWidth) / 2;
        int y = (int) (headerFontSize + g.getFontMetrics().getDescent() * 2);
        g.drawString(str, x, y);
    }

    private void drawClustering(Graphics2D g, Clustering clust, int rectWidth, double xVal, double yVal, int mid) {
        String str = clust.getName();
        int width;
        int x, y;
        if (str == null) {
            str = "unknown |" + clust.size() + "|";
        }
        g.setFont(defaultFont);

        x = (int) xVal;
        // y = (int) (mid - yVal);

        width = stringWidth(defaultFont, g, str);
        checkMax(width);
        y = (int) (x + g.getFontMetrics().getDescent() / 2f);
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
            //use maximum width avaiable
            realSize.width = size.width;
            maxWidth = 0;
        }
    }

    @Override
    public boolean hasData() {
        return internal != null && external != null;
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
