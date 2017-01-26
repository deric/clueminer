/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.plot;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.XChartPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.DataType;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.impl.InstCollection;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.neighbor.KDTree;
import smile.neighbor.Neighbor;

/**
 * Plot for rendering timeseries data.
 *
 * Low number position service are returned first.
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = Plotter.class, position = 10)
public class TimeXPlot<E extends ContinuousInstance> extends JPanel implements Plotter<E> {

    private Chart chart;
    private Collection<? extends Date> yAxis;
    private final HashSet<Integer> instances = new HashSet<>(10);
    private XChartPanel chartPanel;
    private static final Logger LOG = LoggerFactory.getLogger(TimeXPlot.class);
    private ReentrantLock lock;
    private KDTree<E> tree;
    private double cursorRadius = -1;
    private Timeseries<E> dataset;

    public TimeXPlot() {
        initComponents(400, 400);
    }

    public TimeXPlot(int width, int height) {
        initComponents(width, height);
    }

    private void initComponents(int width, int height) {
        lock = new ReentrantLock();
        setLayout(new GridBagLayout());
        ToolTipManager.sharedInstance().registerComponent(this);
        // Create Chart
        chart = new ChartBuilder().width(width).height(height).build();
        chart.getStyleManager().setLegendVisible(false);

        chart.getStyleManager().setXAxisLabelRotation(60);
        chart.getStyleManager().setDatePattern("MM-dd HH:mm");

        chartPanel = new XChartPanel(chart);
        PlotMouseListener ml = new PlotMouseListener(chart, this);
        chartPanel.addMouseListener(ml);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new java.awt.Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        add(chartPanel, c);
    }

    @Override
    public Insets getInsets() {
        return chartPanel.getInsets();
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        chartPanel.setPreferredSize(preferredSize);
    }

    @Override
    public void setMinimumSize(Dimension minimumSize) {
        super.setPreferredSize(minimumSize);
        chartPanel.setMinimumSize(minimumSize);
    }

    @Override
    public Dimension getMinimumSize() {
        return chartPanel.getMinimumSize();
    }

    @Override
    public boolean isSupported(DataType type) {
        return type == DataType.TIMESERIES;
    }

    @Override
    public void prepare(DataType type) {
        //
    }

    @Override
    public void addInstance(E instance) {
        String name = instance.getIndex() + " " + instance.getName();
        addInstance(instance, name);
    }

    @Override
    public void addInstance(E instance, String clusterName) {
        ContinuousInstance inst = (ContinuousInstance) instance;
        dataset = (Timeseries) inst.getParent();
        if (yAxis == null) {
            yAxis = dataset.getTimePointsCollection();
        }
        //make sure we don't add same data twice
        if (!instances.contains(instance.getIndex())) {
            StringBuilder sb = new StringBuilder();
            sb.append(inst.getIndex()).append(" - ").append(clusterName);
            Series s = chart.addSeries(sb.toString(), yAxis, new InstCollection(instance));
            instances.add(instance.getIndex());
        }
    }

    @Override
    public void clearAll() {
        instances.clear();
    }

    @Override
    public void setTitle(String title) {
        chart.setChartTitle(title);
    }

    @Override
    public void setXBounds(double min, double max) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setYBounds(double min, double max) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private double computeCursorRadius() {
        double radius;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dim = toolkit.getBestCursorSize(12, 30);

        Rectangle rect = new Rectangle(0, 0, dim.width, dim.height);
        Rectangle.Double rectTrans = chart.translateSelection(rect);
        radius = 4 * Math.sqrt(Math.pow(rectTrans.width, 2) + Math.pow(rectTrans.height, 2));
        LOG.debug("cursor radius {}", radius);

        return radius;
    }

    /**
     * Initialize tree used for finding points on grid
     */
    private void initializeTree() {
        if (lock.tryLock()) {
            try {
                PlotSearcher<E> worker = new PlotSearcher<>(dataset, instances);
                worker.execute();
                tree = worker.get();
            } catch (InterruptedException | ExecutionException ex) {
                LOG.debug("background worker interrupted {}", ex);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public Collection<E> instanceAt(double[] coord, int maxK) {
        List<Neighbor<double[], E>> neighbors = new LinkedList<>();
        int size = maxK < neighbors.size() ? maxK : neighbors.size();
        if (cursorRadius < 0) {
            cursorRadius = computeCursorRadius();
        }
        if (tree == null) {
            initializeTree();
        } else {
            tree.range(coord, cursorRadius, neighbors);
        }

        HashMap<String, E> ret = new HashMap<>(size);
        Neighbor<double[], E> nn;
        for (int i = 0; i < size; i++) {
            nn = neighbors.get(i);
            if (!ret.containsKey(nn.value.getName())) {
                ret.put(nn.value.getName(), nn.value);
            }
            LOG.debug("key: {}", nn.key);
            LOG.debug("found {}", nn.value.getFullName());
        }

        return ret.values();
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        String tip = null;
//        Point p = event.getPoint();

        // No tip from the renderer get our own tip
        if (tip == null) {
            tip = getToolTipText();
        }

        return tip;
    }

    private void displayToolTip(String text, MouseEvent event) {
        final ToolTipManager ttm = ToolTipManager.sharedInstance();
        final int oldDelay = ttm.getInitialDelay();
        final String oldText = this.getToolTipText(event);
        this.setToolTipText(text);
        ttm.setInitialDelay(0);
        ttm.setDismissDelay(1000);

        MouseEvent evt = new MouseEvent(this, 0, 0, 0,
                event.getX(), event.getY(), // X-Y of the mouse for the tool tip
                0, false);

        ttm.mouseMoved(evt);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ttm.setInitialDelay(oldDelay);
                setToolTipText(oldText);
            }
        }, ttm.getDismissDelay());
    }

    @Override
    public void focus(Collection<E> instance, MouseEvent e) {
        if (instance != null) {
            //this.setToolTipText("tooltip " + instance.getName());
            //LOG.info("focused {}: {}", instance.getName(), e);
            displayToolTip("tooltip " + instance.iterator().next(), e);
        }
    }

}
