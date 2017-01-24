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
package org.clueminer.clustering.selection;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.utils.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 */
public class SelectionFrame extends JPanel {

    private Dataset<? extends Instance> dataset;
    private Dimension dimChart;
    private static final Logger LOG = LoggerFactory.getLogger(SelectionFrame.class);
    private final int minWidth = 100;
    private final Props props;

    public SelectionFrame() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.props = new Props();
    }

    private void redraw() {
        //remove all components
        this.removeAll();

        Instance inst;

        if (dataset != null && dataset.size() > 0) {
            LOG.info("dataset size {}", dataset.size());
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

            Plotter plot = inst.getPlotter(props);
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

            if (dimChart == null) {
                dimChart = new Dimension(minWidth, 100);
            }
            plot.setMinimumSize(dimChart);
            plot.setPreferredSize(dimChart);
            this.setPreferredSize(dimChart);
            String title = dataset.getName();
            if (title != null) {
                plot.setTitle(dataset.getName());
            }
            add((JComponent) plot, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            revalidate();
            LOG.debug("preview size {}", dimChart);
            super.repaint();
        }

    }

    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    public void setDataset(Dataset<? extends Instance> dataset) {
        this.dataset = dataset;
        redraw();
    }

}
