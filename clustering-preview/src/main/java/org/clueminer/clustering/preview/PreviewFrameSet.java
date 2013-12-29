package org.clueminer.clustering.preview;

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
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;

/**
 *
 * @author Tomas Barton
 */
public class PreviewFrameSet extends JPanel implements ClusteringListener, ClusterPreviewer {

    private static final long serialVersionUID = 4231956781752926611L;
    private int clusterNum = 0;
    private final JPanel parent;
    private Plotter[] plots;
    private Clustering<Cluster> clust;
    private Dimension dimChart;
    private static final Logger logger = Logger.getLogger(PreviewFrameSet.class.getName());

    public PreviewFrameSet(JPanel parent) {
        this.parent = parent;
        initComponents();
    }

    private void initComponents() {
        //   setLayout(new GridBagLayout());
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

        Instance inst;

        if (clusterNum > 0) {
            plots = new Plotter[clusterNum];

            int i = 0;
            int total = 0;
            for (Cluster<? extends Instance> d : clust) {
                //  c.gridy = i++;
                logger.log(Level.INFO, "{0}", new Object[]{d.toString()});
                Cluster<? extends Instance> dataset = d;
                if (dataset != null && dataset.size() > 0) {
                    inst = dataset.instance(0);
                    /**
                     * @TODO We can't support visualization of all possible
                     * kinds of data, this ability should be implemented
                     * elsewhere (dataset itself or a visualization
                     * controller...)
                     */
                    //logger.log(Level.INFO, "dataset is kind of {0}", dataset.getClass().toString());
                    //logger.log(Level.INFO, "instace is kind of {0}", inst.getClass().toString());
                    while (inst.getAncestor() != null) {
                        inst = inst.getAncestor();
                    }

                    Plotter plot = inst.getPlotter();
                    if (dataset.size() > 1) {
                        for (int k = 1; k < dataset.size(); k++) {
                            inst = dataset.instance(k);
                            while (inst.getAncestor() != null) {
                                inst = inst.getAncestor();
                            }
                            plot.addInstance(inst);
                            //logger.log(Level.INFO, "sample id {0}, name = {1}", new Object[]{inst.classValue(), inst.getName()});
                        }
                    }

                    if (dimChart == null) {
                        dimChart = new Dimension(this.getWidth(), 100);
                    }
                    plot.setMinimumSize(dimChart);
                    plot.setPreferredSize(dimChart);
                    plot.setTitle(d.getName());
                    plots[i++] = plot;
                    add((JComponent) plot);
                    total += d.size();
                }
            }
            logger.log(Level.INFO, "total num of instances: {0}", total);
        }
    }

    @Override
    public void clusteringChanged(Clustering clust) {
        System.out.println("PreviewFrameSet: clustering changed");
        this.clust = clust;
        redraw();
        parent.repaint();
    }

    @Override
    public void resultUpdate(HierarchicalResult hclust) {
        //new clustering result
    }

    @Override
    public void setClustering(Clustering<Cluster> clustering) {
        this.clust = clustering;
        redraw();
        parent.repaint();
    }

    /**
     * Updates sizes of charts, so that the information will be readable
     *
     * @param height
     */
    public void setChartHeight(int height) {
        if (plots != null) {
            Dimension dim = null;
            for (Plotter plot : plots) {
                dim = new Dimension(plot.getWidth(), height);
                plot.setPreferredSize(dim);
                plot.setMinimumSize(dim);
                plot.revalidate();
            }
            this.dimChart = dim;
            revalidate();
        }
    }
}
