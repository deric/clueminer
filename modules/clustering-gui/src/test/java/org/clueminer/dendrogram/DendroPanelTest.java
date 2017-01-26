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
package org.clueminer.dendrogram;

import com.google.common.collect.ImmutableMap;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.aggl.linkage.SingleLinkage;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Executor;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.report.MemInfo;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class DendroPanelTest extends JFrame {

    private HacDendroPanel frame;
    private static final Logger LOG = LoggerFactory.getLogger(DendroView.class);

    public DendroPanelTest() {
        load();
    }

    private void load() {
        setLayout(new GridBagLayout());
        LOG.debug("initializing components");
        initComponents();
        LOG.debug("loading data");
        final Dataset<? extends Instance> data = FakeDatasets.irisDataset();

        Executor exec = new ClusteringExecutorCached();

        Props prop = new Props();
        prop.put(AlgParams.LINKAGE, SingleLinkage.name);
        MemInfo mem = new MemInfo();
        Clustering clust = exec.clusterRows(data, prop);
        mem.report();
        frame.setDataset(data);
    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        DendroView hmf = new DendroView();
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

    private void initComponents() {
        ImmutableMap<String, Dataset<? extends Instance>> map = new ImmutableMap.Builder<String, Dataset<? extends Instance>>()
                .put("school", FakeDatasets.schoolData())
                .put("iris", FakeDatasets.irisDataset())
                .put("US arrests", FakeDatasets.usArrestData())
                .build();

        frame = new HacDendroPanel(map);
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
        add((Component) frame, c);

    }

}
