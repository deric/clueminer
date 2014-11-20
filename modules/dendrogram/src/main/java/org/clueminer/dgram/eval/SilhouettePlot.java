package org.clueminer.dgram.eval;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.Silhouette;
import org.clueminer.gui.BPanel;
import org.clueminer.std.StdScale;
import org.clueminer.utils.Dump;
import org.imgscalr.Scalr;

/**
 *
 * @author Tomas Barton
 */
public class SilhouettePlot extends BPanel implements DendrogramDataListener, ClusteringListener {

    private static final long serialVersionUID = 4887302917255522954L;
    private Clustering<? extends Cluster> clustering;
    private Dimension element = new Dimension(5, 5);
    private final Silhouette silhouette;
    private final StdScale scale;
    private double[] score;
    private HierarchicalResult hierarchicalResult;

    public SilhouettePlot(boolean fit) {
        super();
        silhouette = new Silhouette();
        scale = new StdScale();
        this.fitToSpace = fit;
        this.preserveAlpha = true;
    }

    @Override
    public void render(Graphics2D g) {
        if (hasData()) {
            //Dump.array(score, "sil score");
            //FontMetrics fm = g.getFontMetrics();
            Cluster clust = null;
            // float y;
            int x = 0, k, prev = -1;
            double value, s;
            Dataset<? extends Instance> dataset;
            Instance inst;
            // String str;
            if (hierarchicalResult != null) {
                dataset = hierarchicalResult.getDataset();
                Dump.array(hierarchicalResult.getMapping(), "sil mapping");
                System.out.println("clusters size: " + clustering.size());
                System.out.println("hres clusters size: " + hierarchicalResult.getClustering().size());
                System.out.println("equals = " + clustering.equals(hierarchicalResult.getClustering()));
                for (int i = 0; i < dataset.size(); i++) {
                    s = score[i];
                    if (Double.isNaN(s)) {
                        s = -1.0;
                    }
                    value = scale.scaleToRange(s, -1.0, 1.0, 0.0, plotMax());
                    inst = dataset.get(hierarchicalResult.getMappedIndex(i));
                    //System.out.println(i + " -> " + hierarchicalResult.getMappedIndex(i) + " : " + inst.getIndex() + " " + inst.classValue());
                    k = clustering.assignedCluster(hierarchicalResult.getMappedIndex(i));
                    if (k != prev) {
                        if (clustering.hasAt(k)) {
                            clust = clustering.get(k);
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

    public void setClustering(Clustering<? extends Cluster> data) {
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
                //System.out.println(i + " -> " + instId + " = " + k + " - " + hierarchicalResult.getDataset().get(instId).getName());
                //if k == -1 (not assigned to any cluster yet) there's no point to count the score
                if (clustering.hasAt(k)) {
                    clust = clustering.get(k);
                    value = silhouette.instanceScore(clust, clustering, k, hierarchicalResult.getDataset().get(instId));
                    if (hierarchicalResult != null) {
                        score[instId] = value;
                    } else {
                        throw new RuntimeException("missing hierarchical result");
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

    /**
     * Generate image of given size
     *
     * @param width
     * @param height
     * @return
     */
    public Image generate(int width, int height) {
        double fHeight = height / (double) clustering.instancesCount();
        reqSize.width = width;
        reqSize.height = height;
        realSize.width = width;
        realSize.height = height;

        //element size can't be smaller than 1px
        element.height = (int) Math.ceil(fHeight);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        render(g);
        if (image.getHeight() != height || image.getWidth() != width) {
            image = Scalr.resize(image, Scalr.Method.SPEED,
                    Scalr.Mode.AUTOMATIC,
                    width, height, Scalr.OP_ANTIALIAS);
        }

        return image;
    }

}
