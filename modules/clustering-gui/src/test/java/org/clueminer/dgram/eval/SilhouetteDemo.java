/*
 * Copyright (C) 2011-2019 clueminer.org
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
package org.clueminer.dgram.eval;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.exec.ClusteringExecutorCached;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class SilhouetteDemo extends JFrame {

    private static final long serialVersionUID = 579590462477351303L;
    private static SilhouettePlot sPanel;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);
    private static final Logger LOG = LoggerFactory.getLogger(SilhouetteDemo.class);
    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);

    public SilhouetteDemo() throws IOException, CloneNotSupportedException {
        setLayout(new GridBagLayout());
        sPanel = new SilhouettePlot(true);
        //sPanel.setMinimumSize(new Dimension(800, 600));

        Props props = new Props();

        ClusteringExecutorCached exec = new ClusteringExecutorCached();
        DendrogramMapping map = exec.clusterAll(FakeDatasets.irisDataset(), props);

        sPanel.setClustering(map.getRowsResult(), map.getRowsClustering());

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = WEST_INSETS;

        this.getContentPane().add(sPanel, c);
        this.pack();
    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        SilhouetteDemo hmf = new SilhouetteDemo();
        hmf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        hmf.setSize(500, 500);
        hmf.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    createAndShowGUI();
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
            }
        });
    }
}
