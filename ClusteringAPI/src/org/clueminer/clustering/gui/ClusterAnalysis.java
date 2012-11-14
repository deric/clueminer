package org.clueminer.clustering.gui;

import java.io.Serializable;
import javax.swing.JPanel;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.dataset.Dataset;
import org.clueminer.instance.Instance;
import org.clueminer.utils.AlgorithmParameters;
import org.clueminer.utils.Exportable;

/**
 *
 * @author Tomas Barton
 */
public abstract class ClusterAnalysis extends JPanel implements Serializable {

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
    public abstract void setDataset(Dataset<Instance> dataset);

    /**
     * Approximated (simplified) dataset by some models
     *
     * @param approx
     */
    public abstract void setApproximated(Dataset<Instance> approx);

    public abstract void execute(AlgorithmParameters map);

    public abstract void zoomIn();

    public abstract void zoomOut();

    public abstract Exportable getMainPanel();

    public abstract ClusteringProperties getClusteringProperties();

    public abstract void addRowsTreeListener(TreeListener listener);
}
