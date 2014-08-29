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
