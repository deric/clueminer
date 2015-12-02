/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.visualize;

import java.awt.Dimension;
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
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class ClusterSetView<E extends Instance, C extends Cluster<E>> extends JPanel implements ClusteringListener<E, C>, ClusterPreviewer<E, C> {

    private final JPanel parent;
    private Plotter[] plots;
    private Clustering<E, C> clust;
    private int clusterNum = 0;
    private Dimension dimChart;
    private double ymax = Double.MIN_VALUE, ymin = Double.MAX_VALUE;
    private boolean useGlobalScale = false;
    private double xmax = 0.0;
    private static final Logger logger = Logger.getLogger(ClusterSetView.class.getName());

    public ClusterSetView(JPanel parent) {
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
            //remove all components
            this.removeAll();
        }

        clusterNum = clust.size();
        logger.log(Level.INFO, "got {0} clusters", clusterNum);

        Instance inst;
        Timeseries<ContinuousInstance> ts;

        if (clusterNum > 0) {
            plots = new Plotter[clusterNum];

            int i = 0;
            int total = 0;
            Plotter plot;

            if (clust.size() > 0) {
                Dataset<? extends Instance> cluster = clust.get(0);
                while (cluster.hasParent()) {
                    cluster = cluster.getParent();
                }
                if (cluster instanceof Timeseries) {
                    ts = (Timeseries<ContinuousInstance>) cluster;
                    ymax = ts.getMax();
                    ymin = ts.getMin();
                }
            }

            for (Cluster<? extends Instance> d : clust) {
                //  c.gridy = i++;
                //logger.log(Level.INFO, "{0}", new Object[]{d.toString()});
                System.out.println(d.getName() + " [" + d.size() + "]");
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
                    checkBounds(plot, inst);

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

    private void checkBounds(Plotter plot, Instance metaInst) {
        if (metaInst instanceof ContinuousInstance) {
            ContinuousInstance tsInst = (ContinuousInstance) metaInst;
            Timeseries<ContinuousInstance> ts = (Timeseries<ContinuousInstance>) ((ContinuousInstance) metaInst).getParent();
            double pos = ((TimePointAttribute) ts.getAttribute(ts.attributeCount() - 1)).getPosition();
            if (pos > xmax) {
                xmax = pos;
                plot.setXBounds(0, xmax);
            }
            //logger.log(Level.INFO, "x max is {0}", xmax);
            //logger.log(Level.INFO, "x max time is {0}", ((TimePointAttribute) ts.getAttribute(ts.attributeCount() - 1)).getTimestamp());
            if (useGlobalScale) {
                if (tsInst.getMin() < ymin) {
                    ymin = tsInst.getMin();
                }
                if (ts.getMax() > ymax) {
                    ymax = ts.getMax();
                }
                plot.setYBounds(ymin, ymax);
            }
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
    public void clusteringStarted(Dataset<E> dataset, Props params) {
        //nothing to do
    }

    @Override
    public void resultUpdate(HierarchicalResult hclust) {
        //new clustering result
    }

    @Override
    public void setClustering(Clustering<E, C> clustering) {
        this.clust = clustering;
        redraw();
        if (parent != null) {
            parent.repaint();
        }
    }

}
