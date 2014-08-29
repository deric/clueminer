package org.clueminer.clustering.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;

/**
 *
 * @author Tomas Barton
 */
public interface HierarchicalResult extends Serializable {

    /**
     * Proximity (a.k.a. similarity matrix) is a NxN matrix of distances
     * (similarities) between items. When the distance metric is symmetrical we
     * are fine with lower/upper diagonal matrix (distance to self is usually 0,
     * so even the diagonal could be left out)
     *
     * @return
     */
    public Matrix getProximityMatrix();

    public void setProximityMatrix(Matrix m);

    /**
     * Cuts dendrogram tree into k clusters
     *
     * @return a set of clusters
     */
    public Clustering getClustering();

    public Clustering getClustering(Dataset<? extends Instance> dataset);

    /**
     * Array of integers with cluster assignments
     *
     * @param terminalsNum
     *
     * @return
     */
    public int[] getClusters(int terminalsNum);

    /**
     * Set dendrogram tree cut-off, which determines number of clusters and
     * returns clustering with given cut-off
     *
     * @param cutoff
     * @return
     */
    public Clustering updateCutoff(double cutoff);

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
     *
     * @return
     */
    public double findCutoff();

    /**
     * Find and sets optimal cutoff with given strategy
     *
     * @param strategy
     * @return
     */
    public double findCutoff(CutoffStrategy strategy);

    /**
     *
     * @return return current number of clusters (computed according to current
     *         cutoff)
     */
    public int getNumClusters();

    /**
     * Forces number of clusters, if -1 then is leaved undecided
     *
     * @param num
     */
    public void setNumClusters(int num);

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
    public Dataset<? extends Instance> getDataset();

    /**
     *
     * @return number of tree levels
     */
    public int treeLevels();

    /**
     *
     * @return dendrogram tree structure
     */
    public DendroTreeData getTreeData();

    public void setTreeData(DendroTreeData treeData);

    /**
     *
     * @param idx
     *
     * @return height of dendrogram tree at given node index
     * @deprecated
     */
    public double treeHeightAt(int idx);

    /**
     *
     * @param idx
     * @return
     * @deprecated
     */
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
     * @param idx position in input matrix/dataset
     * @return row/column index in original dataset that maps from passed
     *         row/column index.
     */
    public int getMappedIndex(int idx);

    public void setMappedIndex(int pos, int idx);

    /**
     * @return indexes of items
     */
    public int[] getMapping();

    /**
     * Return instance at given index -- that means either row or column
     *
     * @param index
     * @return
     */
    public Instance getInstance(int index);

    /**
     * Sets order of items in dendrogram
     *
     * @param mapping
     */
    public void setMapping(int[] mapping);

    public void setInputData(Matrix inputData);

    public Matrix getInputData();

    /**
     *
     * @return list of level where clustered instances are merged
     */
    public List<Merge> getMerges();

    /**
     * Use e.g. DendrogramBuilder to generate merge list
     *
     * @param merges
     */
    public void setMerges(List<Merge> merges);

    /**
     * Return ID of cluster to which was item at given position in input dataset
     * assigned. In case that assignment to clusters is unknown, all items
     * will be in one cluster (cluster 0)
     *
     * @param idx position in input dataset/matrix
     * @return cluster ID
     */
    public int assignedCluster(int idx);

    /**
     * Sets original dataset which was used to obtain the result
     *
     * @param dataset input dataset
     */
    public void setDataset(Dataset<? extends Instance> dataset);

    /**
     * Size of input data (dimension of similarity/proximity matrix) which
     * depends whether it is row/columns clustering
     *
     * @return size of input data
     */
    public int size();

}
