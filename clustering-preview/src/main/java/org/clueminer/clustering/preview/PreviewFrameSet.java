package org.clueminer.clustering.preview;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.gui.ClusterPreviewer;
import org.clueminer.dataset.api.AbstractTimeInstance;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusterPreviewer.class)
public class PreviewFrameSet extends JPanel implements ClusteringListener, ClusterPreviewer {

    private static final long serialVersionUID = 4231956781752926611L;
    private int clusterNum = 0;
    private JPanel parent;
    private Plotter[] plots;
    private Clustering<Cluster> clust;
    private Dimension dimChart;
    private double ymax = Double.MIN_VALUE, ymin = Double.MAX_VALUE;
    private double xmax = 0.0;
    private static final Logger logger = Logger.getLogger(PreviewFrameSet.class.getName());
    private HashMap<Integer, Instance> metaMap;

    public PreviewFrameSet() {
        initComponents();
    }

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
        TimeseriesDataset<ContinuousInstance> ts;

        if (clusterNum > 0) {
            plots = new Plotter[clusterNum];

            int i = 0;
            int total = 0;
            Plotter plot = null;
            Instance metaInst;

            if (clust.size() > 0) {
                Dataset<? extends Instance> cluster = clust.get(0);
                while (cluster.hasParent()) {
                    cluster = cluster.getParent();
                }
                if (cluster instanceof TimeseriesDataset) {
                    ts = (TimeseriesDataset<ContinuousInstance>) cluster;
                    ymax = ts.getMax();
                    ymin = ts.getMin();
                }
            }

            for (Cluster<? extends Instance> d : clust) {
                //  c.gridy = i++;
                //logger.log(Level.INFO, "{0}", new Object[]{d.toString()});
                Cluster<? extends Instance> dataset = d;
                //each cluster might have different max
                xmax = 0.0;
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
                    if (metaMap == null) {
                        while (inst.getAncestor() != null) {
                            inst = inst.getAncestor();
                        }

                        plot = inst.getPlotter();
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
                    } else {
                        int id = Integer.valueOf((String) inst.classValue());

                        if (metaMap.containsKey(id)) {
                            metaInst = metaMap.get(id);
                            plot = metaInst.getPlotter();
                            checkXmax(plot, metaInst);
                        } else {
                            logger.log(Level.WARNING, "failed to find {0}", inst.classValue());
                        }

                        for (int k = 1; k < dataset.size(); k++) {
                            id = Integer.valueOf((String) dataset.instance(k).classValue());
                            if (metaMap.containsKey(id)) {
                                metaInst = metaMap.get(id);
                                plot.addInstance(metaInst);
                                checkXmax(plot, metaInst);
                            } else {
                                logger.log(Level.WARNING, "failed to find {0}", inst.classValue());
                            }
                        }
                    }

                    if (dimChart == null) {
                        dimChart = new Dimension(this.getWidth(), 100);
                    }
                    if (plot != null) {
                        plot.setMinimumSize(dimChart);
                        plot.setPreferredSize(dimChart);
                        plot.setTitle(d.getName() + " (" + d.size() + ")");
                        plots[i++] = plot;
                        add((JComponent) plot);
                    }
                    total += d.size();
                }
            }
            logger.log(Level.INFO, "total num of instances: {0}", total);
        }
    }

    private void checkXmax(Plotter plot, Instance metaInst) {
        TimeseriesDataset<ContinuousInstance> ts = (TimeseriesDataset<ContinuousInstance>) ((AbstractTimeInstance) metaInst).getParent();
        double pos = ((TimePointAttribute) ts.getAttribute(ts.attributeCount() - 1)).getPosition();
        if (pos > xmax) {
            xmax = pos;
            plot.setXBounds(0, xmax);
        }
        if (!Double.isNaN(ymax)) {
            plot.setYBounds(ymin, ymax);
        }
    }

    @Override
    public void clusteringChanged(Clustering clust) {
        this.clust = clust;
        redraw();
        if (parent != null) {
            parent.repaint();
        }
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
            for (Plotter plot : plots) {
                if (plot != null) {
                    dim = new Dimension(plot.getWidth(), height);
                    plot.setPreferredSize(dim);
                    plot.setMinimumSize(dim);
                    plot.revalidate();
                }
            }
            this.dimChart = dim;
            revalidate();
        }
    }

    public void setParent(JPanel p) {
        this.parent = p;
    }

    public HashMap<Integer, Instance> getMetaMap() {
        return metaMap;
    }

    public void setMetaMap(HashMap<Integer, Instance> metaMap) {
        this.metaMap = metaMap;
    }

    public double getYmax() {
        return ymax;
    }

    public void setYmax(double ymax) {
        this.ymax = ymax;
    }

    public double getYmin() {
        return ymin;
    }

    public void setYmin(double ymin) {
        this.ymin = ymin;
    }

    public double getXmax() {
        return xmax;
    }

    public void setXmax(double xmax) {
        this.xmax = xmax;
    }

}
