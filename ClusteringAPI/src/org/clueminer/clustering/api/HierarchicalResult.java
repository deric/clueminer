package org.clueminer.clustering.api;

import java.io.Serializable;
import java.util.Map;
import org.clueminer.dataset.Dataset;
import org.clueminer.instance.Instance;
import org.clueminer.math.Matrix;

/**
 *
 * @author Tomas Barton
 */
public interface HierarchicalResult extends Serializable {

    public Matrix getProximityMatrix();

    public void setProximityMatrix(Matrix m);

    public Matrix getSimilarityMatrix();

    public void setSimilarityMatrix(Matrix m);

    /**
     * Cuts dendrogram tree into k clusters
     *
     * @return a set of clusters
     */
    public Clustering getClustering();

    public Clustering getClustering(Dataset<Instance> dataset);

    /**
     * Array of integers with cluster assignments
     *
     * @param terminalsNum
     *
     * @return
     */
    public int[] getClusters(int terminalsNum);

    /**
     * Set dendrogram tree cut-off, which determines number of clusters
     *
     * @param cutoff
     */
    public void setCutoff(double cutoff);

    /**
     * Dendrogram tree cut-off
     *
     * @return cutoff
     */
    public double getCutoff();

    /**
     * Cuts tree at given level
     *
     * @param level
     *
     * @return
     */
    public double cutTreeByLevel(int level);

    /**
     * Find and sets optimal cutoff with default strategy
     */
    public double findCutoff();

    /**
     * Find and sets optimal cutoff with given strategy
     *
     * @param strategy
     */
    public double findCutoff(CutoffStrategy strategy);

    /**
     *
     * @return return current number of clusters (computed accoring to current
     *         cutoff)
     */
    public int getNumberOfClusters();

    /**
     * Scoring functions are used for evaluation of optimal number of clusters
     *
     * @param evaluator - name of scoring function
     *
     * @see ClusterEvaluator
     * @return Map<number of clusters, cutoff>
     */
    public Map<Integer, Double> getScores(String evaluator);
    
    public double getScore(String evaluator, int clustNum);

    /**
     * Stores clustering score for given evaluator and number of clusters
     *
     * @param evaluator
     * @param clustNum
     * @param sc
     */
    public void setScores(String evaluator, int clustNum, double sc);
    
    public boolean isScoreCached(String evaluator, int clustNum);

    /**
     *
     * @return original dataset used for clustering
     */
    public Dataset<Instance> getDataset();

    /**
     *
     * @return number of tree levels
     */
    public int treeLevels();

    /**
     *
     * @param idx
     *
     * @return height of dendrogram tree at given node index
     */
    public double treeHeightAt(int idx);

    public int treeOrder(int idx);

    /**
     *
     * @return maximum height of dendrogram tree
     */
    public double getMaxTreeHeight();

    /**
     * Translate position of row/column which has been moved during clustering
     * process
     *
     * @return row/column index in original dataset that maps from passed row/column index.
     */
    public int getMappedIndex(int idx);
    
    public void setMappedIndex(int pos, int idx);
}
