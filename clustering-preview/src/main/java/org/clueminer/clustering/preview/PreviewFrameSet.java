package org.clueminer.clustering.preview;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;

/**
 *
 * @author Tomas Barton
 */
public class PreviewFrameSet extends JPanel implements ClusteringListener {

    private static final long serialVersionUID = 4231956781752926611L;
    private int clusterNum = 0;
    private JPanel parent;
    private Plotter[] plots;
    private Clustering<Cluster> clust;

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
        System.out.println("got " + clusterNum + " clusters");

        Instance inst;

        if (clusterNum > 0) {
            plots = new Plotter[clusterNum];

            int i = 0;
            for (Cluster<? extends Instance> d : clust) {
                //  c.gridy = i++;

                Cluster<? extends Instance> dataset = d;
                System.out.println("cluster size = " + d.size());
                if (dataset != null && d.size() > 0) {
                    inst = dataset.instance(0);
                    /**
                     * @TODO We can't support visualization of all possible
                     * kinds of data, this ability should be implemented
                     * elsewhere (dataset itself or a visualization
                     * controller...)
                     */
                    System.out.println("dataset is kind of " + dataset.getClass().toString());
                    System.out.println("instace is kind of " + inst.getClass().toString());
                    System.out.println("ancestor is  " + inst.getAncestor());
                    Instance anc = inst.getAncestor();
                    Plotter plot;
                    if (anc != null) {
                        System.out.println("ancestor is " + anc.getClass().toString());
                        plot = anc.getPlotter();
                        if (dataset.size() > 1) {
                            for (int k = 1; k < dataset.size(); k++) {
                                plot.addInstance(dataset.instance(k).getAncestor());
                            }
                        }
                    } else {
                        plot = inst.getPlotter();
                        if (dataset.size() > 1) {
                            for (int k = 1; k < dataset.size(); k++) {
                                plot.addInstance(dataset.instance(k));
                            }
                        }
                    }
                    plots[i++] = plot;
                    add((JComponent) plot);


                    /* if (inst.getClass().isInstance(AbstractTimeInstance.class)) {
                     charts = new ArrayList<PreviewFrame>();
                     charts.ensureCapacity(50);
                     PreviewFrame f = new PreviewFrame();
                     f.setDataset((Timeseries) dataset);
                     charts.add(f);
                     add(f, c);
                     } else if (inst.getClass().isInstance(Instance.class)) {
                     Plot2DPanel plot = new Plot2DPanel();

                     double[] x = new double[dataset.size()];
                     double[] y = new double[dataset.size()];
                     // Dump.printMatrix(data.length,data[0].length,data,2,5);
                     int k = 5;
                     for (int j = 0; j < dataset.size(); j++) {
                     x[j] = dataset.getAttributeValue(k, j);
                     }

                     k = 0;
                     for (int j = 0; j < dataset.size(); j++) {
                     //Attribute ta =  dataset.getAttribute(j);
                     y[j] = dataset.getAttributeValue(k, j);

                     }
                     //Dump.array(x,"x");
                     //Dump.array(y,"y");

                     plot.addScatterPlot("Cluster " + i, x, y);
                     add(plot, c);
                     System.out.println("adding plot " + i);
                     } else {
                     throw new RuntimeException("unsupported object type, expected child of Dataset, got " + inst.getClass().toString());
                     }*/
                }
            }
        }
        //setPreferredSize(getPreferredSize());
        System.out.println("size " + this.getSize());
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
            Dimension dim;
            for (Plotter plot : plots) {
                dim = new Dimension(plot.getWidth(), height);
                plot.setPreferredSize(dim);
                plot.setMinimumSize(dim);
                plot.revalidate();
            }
            revalidate();
        }
    }
}
