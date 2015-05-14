package org.clueminer.attrstats;

import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.gui.ClusterPreviewer;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class BoxPlotSet extends JPanel implements ClusteringListener, ClusterPreviewer {

    private static final long serialVersionUID = 4231956781752926611L;
    private int clusterNum = 0;
    private JPanel parent;
    private DistPlot[] plots;
    private Clustering<Cluster> clust;
    private Dimension dimChart;
    private static final Logger logger = Logger.getLogger(BoxPlotSet.class.getName());
    private int attributeIndex;

    public BoxPlotSet() {
        initComponents();
    }

    public BoxPlotSet(JPanel parent) {
        this.parent = parent;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    private void redraw() {
        if (clust == null) {
            return;
        }
        if (clust.size() != clusterNum) {
        }
        //remove all components
        this.removeAll();

        clusterNum = clust.size();
        logger.log(Level.INFO, "got {0} clusters", clusterNum);

        if (clusterNum > 0) {
            plots = new DistPlot[clusterNum];

            int i = 0;
            int total = 0;
            for (Cluster<? extends Instance> cluster : clust) {
                //  c.gridy = i++;
                //logger.log(Level.INFO, "{0}", new Object[]{cluster.toString()});
                Cluster<? extends Instance> dataset = cluster;
                if (dataset != null && dataset.size() > 0) {

                    DistPlot plot = new DistPlot();
                    plot.setDataset(dataset);
                    plot.setAttributeIndex(attributeIndex);

                    if (dimChart == null) {
                        dimChart = new Dimension(this.getWidth(), 100);
                    }
                    plot.setMinimumSize(dimChart);
                    plot.setPreferredSize(dimChart);
                    //plot.setTitle(d.getName());
                    plots[i++] = plot;
                    add((JComponent) plot);
                    total += cluster.size();
                }
            }
            logger.log(Level.INFO, "total num of instances: {0}", total);
        }
    }

    @Override
    public void clusteringChanged(Clustering clust) {
        System.out.println("BoxPlotSet: clustering changed");
        this.clust = clust;
        redraw();
        if (parent != null) {
            parent.repaint();
        }
    }

    @Override
    public void clusteringStarted(Dataset<? extends Instance> dataset, Props params) {
        //nothing to do
    }

    @Override
    public void resultUpdate(HierarchicalResult hclust) {
        //new clustering result
    }

    @Override
    public void setClustering(Clustering<Cluster> clustering) {
        this.clust = clustering;
        redraw();
        if (parent != null) {
            parent.repaint();
        }
    }

    /**
     * Updates sizes of charts, so that the information will be readable
     *
     * @param height
     */
    public void setChartHeight(int height) {
        if (plots != null) {
            Dimension dim = null;
            for (DistPlot plot : plots) {
                dim = new Dimension(plot.getWidth(), height);
                plot.setPreferredSize(dim);
                plot.setMinimumSize(dim);
                plot.revalidate();
            }
            this.dimChart = dim;
            revalidate();
        }
    }

    public void setParent(JPanel p) {
        this.parent = p;
    }

    public void setAttributeIndex(int index) {
        this.attributeIndex = index;
        repaint();
    }
}
