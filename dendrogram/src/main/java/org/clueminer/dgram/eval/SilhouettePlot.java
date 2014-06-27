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

    private static final long serialVersionUID = 4887302917255522954L;

    private Clustering<? extends Cluster> clustering;
    private Dimension element = new Dimension(5, 5);
    private Silhouette silhouette;
    private StdScale scale;
    private double[] score;
    private DendrogramMapping dendroData;

    public SilhouettePlot() {
        super();
        setLayout(new BorderLayout());
        silhouette = new Silhouette();
        scale = new StdScale();
    }

    @Override
    public void render(Graphics2D g) {
        if (hasData()) {
            //Dump.array(score, "sil score");
            Cluster clust;
            int x = 0, y;
            int k = 0;
            double value;
            //g.setColor(Color.BLACK);
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
        return reqSize.width;
    }

    @Override
    public void sizeUpdated(Dimension size) {
        if (hasData()) {
            //we dont care about width
            realSize.width = size.width;
            realSize.height = size.height;
            double perLine = Math.ceil(size.height / (double) clustering.instancesCount());
            if (perLine < 1) {
                perLine = 1;// 1px line height
                realSize.height = clustering.instancesCount();
            }
            element.height = (int) perLine;
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
            realSize.width = reqSize.width;
            realSize.height = clustering.instancesCount() * element.height;
        }
        setMinimumSize(reqSize);
        setSize(reqSize);
        setPreferredSize(realSize);
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
            int k = 0;
            for (int i = 0; i < clustering.size(); i++) {
                clust = clustering.get(i);
                for (int j = 0; j < clust.size(); j++) {
                    score[k++] = silhouette.instanceScore(clust, clustering, i, j);
                }
            }
            //Dump.array(score, "silhouette score");
        }
    }

    public void setDendrogramData(DendrogramMapping dendroData) {
        this.dendroData = dendroData;
        resetCache();
    }

}
