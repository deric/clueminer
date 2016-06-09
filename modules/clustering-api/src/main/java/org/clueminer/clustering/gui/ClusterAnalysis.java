/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.clustering.gui;

import java.io.Serializable;
import javax.swing.JPanel;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Exportable;
import org.clueminer.utils.Props;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 * TODO: convert to an interface
 *
 * @author Tomas Barton
 */
public abstract class ClusterAnalysis extends JPanel implements Serializable, TaskListener {

    private static final long serialVersionUID = -1915604725161703247L;

    public ClusterAnalysis() {
    }

    @Override
    public abstract String getName();

    public abstract ClusteringAlgorithm getAlgorithm();

    public abstract void setAlgorithm(ClusteringAlgorithm alg);

    /**
     * Original dataset
     *
     * @param dataset
     */
    public abstract void setDataset(Dataset<? extends Instance> dataset);

    /**
     * Check if dataset is loaded
     *
     * @return true when data available
     */
    public abstract boolean hasDataset();

    /**
     * Return dataset to be clustered
     *
     * @return dataset
     */
    public abstract Dataset<? extends Instance> getDataset();

    /**
     * Method is executed by external module which could modify the original
     * dataset (preprocessing, dimensionality reduction)
     *
     * @param map
     * @param dataset
     */
    public abstract void execute(Props map, Dataset<? extends Instance> dataset);

    public abstract void zoomIn();

    public abstract void zoomOut();

    public abstract Exportable getMainPanel();

    public abstract void addRowsTreeListener(TreeListener listener);

    /**
     * Method is executed when clustering task is finished
     *
     * @param task
     */
    @Override
    public abstract void taskFinished(Task task);

    public abstract HierarchicalResult getResult();
}
