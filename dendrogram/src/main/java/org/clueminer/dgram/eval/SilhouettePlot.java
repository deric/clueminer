package org.clueminer.dgram.eval;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.eval.Silhouette;
import org.clueminer.gui.BPanel;
import org.clueminer.std.StdScale;
import org.clueminer.utils.Dump;

/**
 *
 * @author Tomas Barton
 */
public class SilhouettePlot extends BPanel implements DendrogramDataListener {

    private Clustering<? extends Cluster> clustering;
    private Dimension element = new Dimension(0, 0);
    private Silhouette silhouette;
    private StdScale scale;
    private double[] score;

    public SilhouettePlot() {
        setLayout(new BorderLayout());
        silhouette = new Silhouette();
        scale = new StdScale();
        size.width = 100;
    }

    @Override
    public void render(Graphics2D g) {
        if (hasData()) {
            Dump.array(score, "sil score");
            Cluster clust;
            int x = 0, y;
            int k = 0;
            double value;
            g.setColor(Color.BLACK);
            for (int i = 0; i < clustering.size(); i++) {
                clust = clustering.get(i);
                for (int j = 0; j < clust.size(); j++) {
                    double s = score[k];
                    value = scale.scaleToRange(s, -1.0, 1.0, 0.0, plotMax());
                    y = element.height * k;
                    g.setColor(Color.BLUE);
                    g.fillRect(x, y, (int) value, element.height);
                    k++;
                }

            }
        }
    }

    /**
     * Width of maximum value on plot
     *
     * @return
     */
    private double plotMax() {
        return size.width;
    }

    @Override
    public void updateSize(Dimension size) {
        if (element.height != size.height) {
            element.height = size.height;
            resetCache();
        }
    }

    @Override
    public boolean hasData() {
        return clustering != null;
    }

    @Override
    public void recalculate() {
        if (hasData()) {
            size.height = clustering.instancesCount() * element.height;
        }
        size.width = getSize().width;

        setMinimumSize(size);
        setSize(size);
        setPreferredSize(size);
    }

    @Override
    public boolean isAntiAliasing() {
        return true;
    }

    void setClustering(Clustering<? extends Cluster> data) {
        this.clustering = data;
        updateScore();
        resetCache();
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping dataset) {
        setClustering(dataset.getRowsClustering());
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        //TODO check if horizontal orientation
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        element.height = height;
    }

    /**
     *
     */
    private void updateScore() {
        if (hasData()) {
            score = new double[clustering.instancesCount()];
            Cluster clust;
            for (int i = 0; i < clustering.size(); i++) {
                clust = clustering.get(i);
                for (int j = 0; j < clust.size(); j++) {
                    score[i] = silhouette.instanceScore(clust, clustering, i, j);
                }
            }
            Dump.array(score, "silhouette score");
        }
    }

}
