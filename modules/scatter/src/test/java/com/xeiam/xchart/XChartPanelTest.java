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
package com.xeiam.xchart;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.impl.TimeseriesDataset;
import org.clueminer.fixtures.TimeseriesFixture;
import org.clueminer.io.csv.CsvLoader;
import org.clueminer.plot.PlotMouseListener;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class XChartPanelTest<E extends Instance> extends JFrame {

    private static Logger LOG = LoggerFactory.getLogger(XChartPanelTest.class);
    private XChartPanel scatter;
    private Chart chart;
    private Collection<? extends Date> yAxis;
    private HashSet<Integer> instances = new HashSet<>(10);
    private static final TimeseriesFixture TF = new TimeseriesFixture();
    private static Timeseries<ContinuousInstance> data01;

    public XChartPanelTest() throws IOException {
        setLayout(new GridBagLayout());
        initComponents(800, 600);
        final Timeseries<ContinuousInstance> data = loadData01();
        yAxis = data.getTimePointsCollection();
        LOG.info("y axis {} points", yAxis.size());
        LOG.info("loaded {} ts attributes", data.attributeCount());
        for (ContinuousInstance inst : data) {
            addInstance(inst, inst.getName());
        }
    }

    public static Timeseries<ContinuousInstance> loadData01() throws IOException {
        if (data01 == null) {
            File f = TF.ts01();
            data01 = new TimeseriesDataset<>(105);
            CsvLoader loader = new CsvLoader();
            loader.setHasHeader(true);
            loader.setDefaultDataType("TIME");
            ArrayList<Integer> meta = new ArrayList<>(7);
            for (int i = 0; i < 7; i++) {
                meta.add(i);
            }
            loader.setSkipIndex(meta);
            loader.load(f, data01);
        }
        return data01;
    }

    // this function will be run from the EDT
    protected static void createAndShowGUI() throws Exception {
        XChartPanelTest sc = new XChartPanelTest();
        sc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sc.setSize(800, 600);
        sc.setVisible(true);
    }

    public void addInstance(ContinuousInstance instance, String clusterName) {
        //make sure we don't add same data twice
        if (!instances.contains(instance.getIndex())) {
            //this.addLinePlot(instance.getName(), instance.getColor(), dataset.getTimePointsArray(), instance.arrayCopy());
            String name = instance.getIndex() + " " + instance.getName();
            chart.addSeries(name, yAxis, new InstCollection(instance));
            instances.add(instance.getIndex());
        } else {
            LOG.info("skipping instance with ID {}, already plot contains {}", instance.getIndex(), instances.size());
        }
    }

    private void initComponents(int width, int height) {
        setLayout(new GridBagLayout());
        // Create Chart
        chart = new ChartBuilder().width(width).height(height).build();
        chart.getStyleManager().setLegendVisible(false);

        chart.getStyleManager().setXAxisLabelRotation(60);
        chart.getStyleManager().setDatePattern("MM-dd HH:mm");

        XChartPanel chartPanel = new XChartPanel(chart);
        PlotMouseListener ml = new PlotMouseListener(chart);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    long start = System.currentTimeMillis();
                    createAndShowGUI();
                    long end = (System.currentTimeMillis() - start);
                    LOG.info("clm-chart show = {} ms", end);
                } catch (Exception e) {
                    System.err.println(e);
                    Exceptions.printStackTrace(e);
                }
            }
        });
    }

    private class InstCollection<E extends Number> implements Collection<E> {

        private final Instance instance;

        public InstCollection(Instance inst) {
            this.instance = inst;
        }

        @Override
        public int size() {
            return instance.size();
        }

        @Override
        public boolean isEmpty() {
            return instance.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            for (int i = 0; i < instance.size(); i++) {
                if (o.equals(instance.get(i))) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Iterator<E> iterator() {
            return new CellValueIterator();
        }

        class CellValueIterator implements Iterator<E> {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public E next() {
                return (E) (Number) instance.get(index++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Cannot remove from dataset using the iterator.");

            }
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean add(E e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
