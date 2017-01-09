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
package org.clueminer.chart.plots;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.chart.ui.DrawablePanel;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class ScatterTest2 extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(ScatterTest2.class);
    private ScatterPlot2 scatter;

    public ScatterTest2() {
        setLayout(new GridBagLayout());
        initComponents();

        final Dataset<? extends Instance> data = FakeDatasets.schoolData();
        scatter.setDataset(data);

    }

    // this function will be run from the EDT
    protected static void createAndShowGUI() throws Exception {
        ScatterTest2 sc = new ScatterTest2();
        sc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sc.setSize(800, 600);
        sc.setVisible(true);
    }

    private void initComponents() {
        long start = System.currentTimeMillis();
        scatter = new ScatterPlot2(800, 600);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(0, 0, 0, 0);
        DrawablePanel panel = new DrawablePanel(scatter);
        long create = (System.currentTimeMillis() - start);
        LOG.info("clm-chart create = {} ms", create);
        add(panel, c);
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
