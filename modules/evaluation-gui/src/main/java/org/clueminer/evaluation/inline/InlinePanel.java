/*
 * Copyright (C) 2015 clueminer.org
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
package org.clueminer.evaluation.inline;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;

/**
 *
 * @author deric
 */
public class InlinePanel extends JPanel {

    private static final long serialVersionUID = -4474047225035728427L;

    private ScorePlot plot;
    private SortingToolbar toolbar;

    public static String NONE = "(none)";

    public InlinePanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        //setBackground(Color.WHITE);
        //setSize(new Dimension(800, 600));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;
        //component in last row should be streatched to fill space at the bottom
        c.weighty = 0.1;
        c.insets = new java.awt.Insets(5, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 10);

        //left list
        plot = new ScorePlot();
        toolbar = new SortingToolbar(plot);
        add(toolbar, c);

        c.gridy = 1;
        c.gridwidth = 2;
        c.gridx = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(0, 0, 0, 0);
        add(plot, c);

        revalidate();
        validate();
        repaint();
    }

    public void setClusterings(Collection<? extends Clustering> clusterings) {
        if (clusterings != null && clusterings.size() > 1) {
            plot.setClusterings((Collection<Clustering>) clusterings);
        }
    }

    public Collection<? extends Clustering> getClusterings() {
        return plot.getClusterings();
    }

    public void setGolden(Clustering<? extends Cluster> clust) {
        plot.goldenStd = clust;
    }

    public void setEvaluatorX(ClusterEvaluation ex) {
        toolbar.setEvaluatorX(ex);
    }

    public void setEvaluatorY(ClusterEvaluation ey) {
        toolbar.setEvaluatorY(ey);
    }

}
