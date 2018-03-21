/*
 * Copyright (C) 2011-2018 clueminer.org
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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class ClusterSetView<E extends Instance, C extends Cluster<E>> extends JPanel implements ClusteringListener<E, C>, ClusterPreviewer<E, C> {

    private static final long serialVersionUID = -8449113355905843012L;
    private final JPanel parent;
    private Plotter[] plots;
    private Clustering<E, C> clust;
    private int clusterNum = 0;
    private Dimension dimChart;
    private double ymax = Double.MIN_VALUE, ymin = Double.MAX_VALUE;
    private boolean useGlobalScale = false;
    private double xmax = 0.0;
    private static final Logger LOG = LoggerFactory.getLogger(ClusterSetView.class);
    private Props props;

    public ClusterSetView(JPanel parent) {
        this.parent = parent;
        this.props = new Props();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                //
                if (dimChart != null) {
                    if (dimChart.width != parent.getSize().width) {
                        dimChart.width = parent.getSize().width;
                        repaint();
                    }

                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                //
            }

            @Override
            public void componentShown(ComponentEvent e) {
                //
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                //
            }

        });
    }

    private void redraw() {
        if (clust == null) {
            return;
        }
        //remove all components
        this.removeAll();

        clusterNum = clust.size();
        LOG.trace("got {} clusters", clusterNum);

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

            for (Cluster<E> d : clust) {
                //  c.gridy = i++;
                Cluster<E> dataset = d;
                //each cluster might have different max
                xmax = 0.0;
                E inst;
                if (dataset != null && dataset.size() > 0) {
                    inst = dataset.instance(0);
                    LOG.trace("dataset: {}", new Object[]{dataset.toString()});
                    /**
                     * @TODO We can't support visualization of all possible
                     * kinds of data, this ability should be implemented
                     * elsewhere (dataset itself or a visualization
                     * controller...)
                     */
                    //logger.log(Level.INFO, "dataset is kind of {0}", dataset.getClass().toString());
                    //logger.log(Level.INFO, "instace is kind of {0}", inst.getClass().toString());

                    //we're trying to plot original data before any transformation
                    while (inst.getAncestor() != null) {
                        inst = (E) inst.getAncestor();
                    }

                    plot = inst.getPlotter(props);
                    if (dataset.size() > 1) {
                        for (int k = 1; k < dataset.size(); k++) {
                            inst = dataset.instance(k);
                            while (inst.getAncestor() != null) {
                                inst = (E) inst.getAncestor();
                            }
                            //logger.log(Level.INFO, "sample id {0}, name = {1}", new Object[]{inst.classValue(), inst.getName()});
                            plot.addInstance(inst, dataset.getName());

                        }
                    }
                    checkBounds(plot, inst);

                    if (plot != null) {
                        if (dimChart == null) {
                            dimChart = plot.getMinimumSize();
                            //override width according to component width
                            dimChart.width = this.getWidth();
                        }
                        plot.setMinimumSize(dimChart);
                        plot.setPreferredSize(dimChart);
                        plot.setTitle(d.getName() + " (" + d.size() + ")");
                        //LOG.debug("plot {}, min = {}, pref = {}", i, plot.getMinimumSize(), plot.getPreferredSize());
                        plots[i++] = plot;
                        add((JComponent) plot);
                    }
                    total += d.size();
                }
            }
            LOG.trace("total num of instances: {}", total);
        }
    }

    private void checkBounds(Plotter<E> plot, E metaInst) {
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

    public Dimension getChartDimension() {
        return dimChart;
    }

    public void setChartDimension(Dimension dim) {
        this.dimChart = dim;
        dimChart.width = this.getWidth();
        this.removeAll();
        redraw();
    }

}
