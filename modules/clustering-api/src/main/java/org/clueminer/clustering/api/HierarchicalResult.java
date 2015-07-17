package org.clueminer.clustering.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.DataVector;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

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
    Matrix getProximityMatrix();

    void setProximityMatrix(Matrix m);

    /**
     * Cuts dendrogram tree into k clusters
     *
     * @return a set of clusters
     */
    Clustering getClustering();

    Clustering getClustering(Dataset<? extends Instance> dataset);

    void setClustering(Clustering clustering);

    /**
     * Array of integers with cluster assignments
     *
     * @param terminalsNum
     *
     * @return
     */
    int[] getClusters(int terminalsNum);

    /**
     * Set dendrogram tree cut-off, which determines number of clusters and
     * returns clustering with given cut-off
     *
     * @param cutoff
     * @return
     */
    Clustering updateCutoff(double cutoff);

    /**
     * Dendrogram tree cut-off
     *
     * @return cutoff
     */
    double getCutoff();

    /**
     * Sets cutoff to certain value without generating corresponding clustering
     *
     * @param cutoff
     */
    void setCutoff(double cutoff);

    /**
     * Cuts tree at given level
     *
     * @param level
     *
     * @return
     */
    double cutTreeByLevel(int level);

    /**
     * Find and sets optimal cutoff with default strategy
     *
     * @return
     */
    double findCutoff();

    /**
     * Find and sets optimal cutoff with given strategy
     *
     * @param strategy
     * @return
     */
    double findCutoff(CutoffStrategy strategy);

    /**
     *
     * @return return current number of clusters (computed according to current
     *         cutoff)
     */
    int getNumClusters();

    /**
     * Forces number of clusters, if -1 then is leaved undecided
     *
     * @param num
     */
    void setNumClusters(int num);

    /**
     * Scoring functions are used for evaluation of optimal number of clusters
     *
     * @param evaluator - name of scoring function
     *
     * @see ClusterEvaluator
     * @return Map<number of clusters, cutoff>
     */
    Map<Integer, Double> getScores(String evaluator);

    double getScore(String evaluator, int clustNum);

    /**
     * Stores clustering score for given evaluator and number of clusters
     *
     * @param evaluator
     * @param clustNum
     * @param sc
     */
    void setScores(String evaluator, int clustNum, double sc);

    boolean isScoreCached(String evaluator, int clustNum);

    /**
     *
     * @return original dataset used for clustering
     */
    Dataset<? extends Instance> getDataset();

    /**
     *
     * @return number of tree levels
     */
    int treeLevels();

    /**
     *
     * @return dendrogram tree structure
     */
    DendroTreeData getTreeData();

    void setTreeData(DendroTreeData treeData);

    /**
     *
     * @param idx
     *
     * @return height of dendrogram tree at given node index
     * @deprecated
     */
    double treeHeightAt(int idx);

    /**
     *
     * @param idx
     * @return
     * @deprecated
     */
    int treeOrder(int idx);

    /**
     *
     * @return maximum height of dendrogram tree
     */
    double getMaxTreeHeight();

    /**
     * Translate position of row/column which has been moved during clustering
     * process
     *
     * @param idx position in input matrix/dataset
     * @return row/column index in original dataset that maps from passed
     *         row/column index.
     */
    int getMappedIndex(int idx);

    void setMappedIndex(int pos, int idx);

    /**
     * @return indexes of items
     */
    int[] getMapping();

    /**
     * Return instance at given index - only for rows
     *
     * @param index
     * @deprecated in future only {@link DataVector} will be supported
     * @return
     */
    Instance getInstance(int index);

    /**
     * Either row or column vector
     *
     * @param index
     * @return
     */
    DataVector getVector(int index);

    /**
     * Sets order of items in dendrogram
     *
     * @param mapping
     */
    void setMapping(int[] mapping);

    void setInputData(Matrix inputData);

    Matrix getInputData();

    /**
     *
     * @return list of level where clustered instances are merged
     */
    List<Merge> getMerges();

    /**
     * Use e.g. DendrogramBuilder to generate merge list
     *
     * @param merges
     */
    void setMerges(List<Merge> merges);

    /**
     * Return ID of cluster to which was item at given position in input dataset
     * assigned. In case that assignment to clusters is unknown, all items
     * will be in one cluster (cluster 0)
     *
     * @param idx position in input dataset/matrix
     * @return cluster ID
     */
    int assignedCluster(int idx);

    /**
     * Sets original dataset which was used to obtain the result
     *
     * @param dataset input dataset
     */
    void setDataset(Dataset<? extends Instance> dataset);

    void setResultType(ResultType type);

    /**
     * Size of input data (dimension of similarity/proximity matrix) which
     * depends whether it is row/columns clustering
     *
     * @return size of input data
     */
    int size();

    /**
     * True when clusters has already been created.
     *
     * @return true when clusters are available
     */
    boolean hasClustering();

    /**
     * Parameters used for obtaining the clustering result
     *
     * @return
     */
    Props getParams();

    /**
     * Dendrogram mapping might contain both rows and columns clustering
     *
     * @return dendrogram mapping
     */
    DendrogramMapping getDendrogramMapping();

    public double getHeightByLevel(int level);

}
