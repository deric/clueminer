package org.clueminer.dgram.eval;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.eval.Silhouette;
import org.clueminer.gui.BPanel;
import org.clueminer.std.StdScale;

/**
 *
 * @author Tomas Barton
 */
public class SilhouettePlot extends BPanel implements DendrogramDataListener, ClusteringListener {

    private static final long serialVersionUID = 4887302917255522954L;
    private Clustering<? extends Cluster> clustering;
    private Dimension element = new Dimension(5, 5);
    private Silhouette silhouette;
    private StdScale scale;
    private double[] score;
    private HierarchicalResult hierarchicalResult;

    public SilhouettePlot(boolean fit) {
        super();
        silhouette = new Silhouette();
        scale = new StdScale();
        fitToSpace = fit;
    }

    @Override
    public void render(Graphics2D g) {
        if (hasData()) {
            //Dump.array(score, "sil score");
            FontMetrics fm = g.getFontMetrics();
            Cluster clust = null;
            // float y;
            int x = 0, k = 0, prev = -1;
            double value, s;
            // String str;
            if (hierarchicalResult != null) {
                for (int i = 0; i < hierarchicalResult.getDataset().size(); i++) {
                    s = score[i];
                    if (Double.isNaN(s)) {
                        s = -1.0;
                    }
                    value = scale.scaleToRange(s, -1.0, 1.0, 0.0, plotMax());

                    k = clustering.assignedCluster(hierarchicalResult.getMappedIndex(i));
                    if (k != prev) {
                        if (clustering.hasAt(k - 1)) {
                            clust = clustering.get(k - 1);
                        }
                    }
                    if (clust != null) {
                        g.setColor(clust.getColor());
                    } else {
                        g.setColor(Color.GRAY);
                    }

                    g.fillRect(x, i * element.height, (int) value, element.height);
                    /*
                     g.setColor(Color.BLACK);
                     y = (i * element.height + element.height / 2f + fm.getDescent() / 2f);
                     str = String.format("%.2f", s);
                     if (str != null) {
                     g.drawString(str, (float) (x + value + 10), y);
                     }*/

                    prev = k;
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

    /**
     *
     * @param w
     * @param h
     */
    public void setElementSize(int w, int h) {
        element.width = w;
        element.height = h;
        resetCache();
    }

    @Override
    public void sizeUpdated(Dimension size) {
        if (hasData()) {
            //we dont care about width
            realSize.width = size.width;
            realSize.height = size.height;
            if (fitToSpace) {
                double perLine = Math.ceil(size.height / (double) clustering.instancesCount());
                if (perLine < 1) {
                    perLine = 1;// 1px line height
                    realSize.height = clustering.instancesCount();
                }
                element.height = (int) perLine;
            }
            resetCache();
        }
    }

    @Override
    public boolean hasData() {
        return clustering != null && clustering.instancesCount() > 0 && hierarchicalResult != null;
    }

    @Override
    public void recalculate() {
        if (hasData()) {
            realSize.width = reqSize.width;
            realSize.height = clustering.instancesCount() * element.height;
        } else {
            realSize.width = reqSize.width;
            realSize.height = reqSize.height;
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
        DendrogramMapping d = data.getLookup().lookup(DendrogramMapping.class);
        if (d != null) {
            hierarchicalResult = d.getRowsResult();
        }
        updateScore();
        if (reqSize.width == 0) {
            reqSize.width = 100;
        }
        resetCache();
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramMapping mapping) {
        hierarchicalResult = mapping.getRowsResult();
        setClustering(mapping.getRowsClustering());
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        //TODO check if horizontal orientation
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        if (height > 0) {
            element.height = height;
            if (reqSize.width == 0) {
                reqSize.width = 100;
            }
            if (hasData()) {
                reqSize.height = clustering.instancesCount() * element.height;
            }
            resetCache();
        }
    }

    /**
     *
     */
    private void updateScore() {
        if (hasData()) {
            score = new double[hierarchicalResult.getDataset().size()];
            Cluster clust;
            int k;
            double value;
            int instId;
            for (int i = 0; i < score.length; i++) {
                instId = hierarchicalResult.getMappedIndex(i);
                k = clustering.assignedCluster(instId);
                //if k == -1 (not assigned to any cluster yet) there's no point to count the score
                if (clustering.hasAt(k - 1)) {
                    clust = clustering.get(k - 1);
                    value = silhouette.instanceScore(clust, clustering, k - 1, hierarchicalResult.getDataset().get(instId));
                    if (hierarchicalResult != null) {
                        score[instId] = value;
                    } else {
                        score[i] = value;
                    }
                }
            }
            //Dump.array(score, "silhouette score");
        }
    }

    public void setDendrogramData(DendrogramMapping dendroData) {
        hierarchicalResult = dendroData.getRowsResult();
        setClustering(dendroData.getRowsClustering());
        resetCache();
        repaint();
    }

    @Override
    public void clusteringChanged(Clustering clust) {
        setClustering(clust);
    }

    @Override
    public void resultUpdate(HierarchicalResult hclust) {
        if (hclust != null) {
            hierarchicalResult = hclust;
            setClustering(hclust.getClustering());
        }
    }

}
