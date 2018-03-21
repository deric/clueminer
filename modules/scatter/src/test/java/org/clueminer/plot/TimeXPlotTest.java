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
package org.clueminer.plot;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.impl.TimeseriesDataset;
import org.clueminer.fixtures.TimeseriesFixture;
import org.clueminer.io.csv.CsvLoader;
import org.clueminer.kdtree.KDTree;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class TimeXPlotTest<E extends Instance> extends JFrame {

    private static Logger LOG = LoggerFactory.getLogger(TimeXPlotTest.class);
    private TimeXPlot plot;
    private Collection<? extends Date> yAxis;
    private HashSet<Integer> instances = new HashSet<>(10);
    private static final TimeseriesFixture TF = new TimeseriesFixture();
    private static Timeseries<ContinuousInstance> data01;
    private KDTree<E> kdTree;

    public TimeXPlotTest() throws IOException {
        setLayout(new GridBagLayout());
        initComponents(800, 600);
        final Timeseries<ContinuousInstance> data = loadData01();
        yAxis = data.getTimePointsCollection();
        LOG.info("y axis {} points", yAxis.size());
        LOG.info("loaded {} ts attributes", data.attributeCount());
        kdTree = new KDTree(2);
        for (ContinuousInstance inst : data) {
            plot.addInstance(inst);
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
        TimeXPlotTest sc = new TimeXPlotTest();
        sc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sc.setSize(800, 600);
        sc.setVisible(true);
    }

    private void initComponents(int width, int height) {
        setLayout(new GridBagLayout());
        // Create Chart
        plot = new TimeXPlot(width, height);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new java.awt.Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        add(plot, c);
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

}
