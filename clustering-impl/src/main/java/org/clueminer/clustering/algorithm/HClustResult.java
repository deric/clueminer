package org.clueminer.clustering.algorithm;

import com.google.common.primitives.Ints;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.cluster.BaseCluster;
import org.clueminer.cluster.ClusterList;
import org.clueminer.clustering.AssigmentsImpl;
import org.clueminer.clustering.HardAssignment;
import org.clueminer.clustering.api.Merge;
import org.clueminer.clustering.api.Assignment;
import org.clueminer.clustering.api.Assignments;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.hclust.DTreeNode;
import org.clueminer.hclust.DynamicTreeData;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Dump;

/**
 *
 * @author Tomas Barton
 */
public class HClustResult implements HierarchicalResult {

    private static final long serialVersionUID = -515379303769981129L;
    private Matrix proximity;
    private Matrix similarity;
    private Matrix inputData;
    private int[] mapping;
    private Assignments assignments;
    private int numClusters = -1;
    private DendroTreeData treeData;
    private double cutoff = Double.NaN;
    private Dataset<? extends Instance> dataset;
    private static final Logger logger = Logger.getLogger(HClustResult.class.getName());
    private DendroNode[] nodes;
    private int numNodes = 0;
    private Clustering clustering = null;

    /**
     * list of dendrogram levels - each Merge represents one dendrogram level
     */
    private List<Merge> merges;

    public HClustResult() {

    }

    public HClustResult(Dataset<? extends Instance> dataset) {
        this.dataset = dataset;
    }

    @Override
    public DendroTreeData getTreeData() {
        if (treeData == null) {
            constructTree();
        }
        logger.log(Level.INFO, "tree nodes: {0}", treeData.numNodes());
        return treeData;
    }

    @Override
    public Matrix getInputData() {
        return inputData;
    }

    @Override
    public void setInputData(Matrix inputData) {
        this.inputData = inputData;
    }

    @Override
    public Matrix getProximityMatrix() {
        return proximity;
    }

    @Override
    public void setProximityMatrix(Matrix m) {
        this.proximity = m;
    }

    @Override
    public Matrix getSimilarityMatrix() {
        return similarity;
    }

    @Override
    public void setSimilarityMatrix(Matrix m) {
        this.similarity = m;
    }

    @Override
    public void setMapping(int[] assignments) {
        this.mapping = assignments;
    }

    public Assignments getAssignments() {
        if (assignments == null) {
            assignments = toAssignments(getMapping(), getInputData(), getNumClusters());
        }
        return assignments;
    }

    /**
     * Converts an array containing each row's clustering assignment into an
     * array of {@link HardAssignment} instances.
     *
     * @param rowAssignments
     * @param matrix
     * @param numClusters
     * @return
     */
    public static Assignments toAssignments(int[] rowAssignments, Matrix matrix, int numClusters) {
        if (numClusters == -1) {
            for (int assignment : rowAssignments) {
                numClusters = Math.max(numClusters, assignment + 1);
            }
        }

        Assignment[] assignments = new Assignment[rowAssignments.length];
        for (int i = 0; i < rowAssignments.length; ++i) {
            assignments[i] = new HardAssignment(rowAssignments[i]);
        }
        return new AssigmentsImpl(numClusters, assignments, matrix);
    }

    @Override
    public int getNumClusters() {
        return numClusters;
    }

    @Override
    public void setNumClusters(int numClusters) {
        this.numClusters = numClusters;
    }

    @Override
    public Clustering getClustering() {
        if (clustering == null) {
            getClustering(dataset);
        }
        return clustering;
    }

    @Override
    public Clustering getClustering(Dataset<? extends Instance> parent) {
        setDataset(parent);

        Clustering result = new ClusterList(dataset.size());
        /**
         * TODO find cutoff
         */

        /*        //we need number of instances in dataset
         int[] clusters = treeData.getClusters(parent.size());
        Dump.array(clusters, "cluster assignments");

        Clustering result = new ClusterList(treeData.getNumberOfClusters());
        if (treeData.getNumberOfClusters() <= 0) {
            logger.log(Level.WARNING, "0 clusters according to treeData");
            return result;
        }
        if (clusters.length != parent.size()) {
            throw new RuntimeException("unexpected size of clustering result " + clusters.length + ", dataset size is " + parent.size());
        }

        //estimated capacity
        int perCluster = (int) (parent.size() / (float) treeData.getNumberOfClusters());
        int num, idx;
        Cluster<Instance> clust;
        //Dump.array(clusters, "clusters-assignment");
        //Dump.array(itemsMapping, "items-mapping");
        for (int i = 0; i < clusters.length; i++) {
            num = clusters[i] - 1; //numbering starts from 1
            //if clustering wasn't computed yet, we have to wait...
            if (num >= 0) {
                if (!result.hasAt(num)) {
                    clust = new BaseCluster<Instance>(perCluster);
                    clust.setName("Cluster " + (num + 1));
                    clust.setParent(parent);

                    Attribute[] attr = parent.copyAttributes();
                    for (int j = 0; j < attr.length; j++) {
                        clust.setAttribute(j, attr[j]);
                    }
                    result.put(num, clust);
                } else {
                    clust = result.get(num);
                }
                idx = itemsMapping[i];
                //logger.log(Level.WARNING, "adding {0} to cluster {1}", new Object[]{getInstance(idx).getName(), num});
                //logger.log(Level.WARNING, "{0} -> {1}: cluster {2}", new Object[]{i, idx, num});
                //mapping is tracked in cluster
                // values in cluster array doesn't need mapping!
                clust.add(dataset.get(i), idx);
            }
        }
        for (Object c : result) {
            logger.log(Level.INFO, "{0}", c.toString());
        }
        //proximity.printLower(5, 2);
        // similarity.print(4, 2);
         */
        return result;
    }

    @Override
    public int[] getClusters(int terminalsNum) {
        int[] clusters = new int[getDataset().size()];
        /**
         * TODO: fill with assignments
         */
        return clusters;
    }

    @Override
    public void setCutoff(double cutoff) {
        this.cutoff = cutoff;
    }

    @Override
    public double getCutoff() {
        return cutoff;
    }

    @Override
    public double cutTreeByLevel(int level) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double findCutoff() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double findCutoff(CutoffStrategy strategy) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Integer, Double> getScores(String evaluator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getScore(String evaluator, int clustNum) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setScores(String evaluator, int clustNum, double sc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isScoreCached(String evaluator, int clustNum) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    @Override
    public int treeLevels() {
        if (treeData != null) {
            return treeData.treeLevels();
        }
        return 0;
    }

    @Override
    public double treeHeightAt(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int treeOrder(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * TODO: this is hardly correct
     *
     * @return
     */
    @Override
    public double getMaxTreeHeight() {
        if (treeData == null) {
            logger.log(Level.INFO, "constructing tree");
            constructTree();
        }
        return treeData.getRoot().getHeight();
    }

    @Override
    public int getMappedIndex(int idx) {
        if (mapping == null) {
            throw new RuntimeException("Empty mapping");
        }
        return mapping[idx];
    }

    @Override
    public void setMappedIndex(int pos, int idx) {
        if (mapping == null) {
            throw new RuntimeException("Empty mapping");
        }
        mapping[pos] = idx;
    }

    @Override
    public int[] getMapping() {
        if (mapping == null) {
            throw new RuntimeException("Empty mapping");
        }

        if (merges != null) {
            //we need a guarantee of ordered items
            LinkedHashSet<Integer> samples = new LinkedHashSet<Integer>();
            for (Merge m : getMerges()) {
                samples.add(m.mergedCluster()); //this should be unique
                if (!samples.contains(m.remainingCluster())) {
                    //linked sample (on higher levels cluster is marked with lowest number in the cluster)
                    samples.add(m.remainingCluster());
                }
            }
            //convert List<Integer> to int[]
            mapping = Ints.toArray(samples);
        } else {
            throw new RuntimeException("empty merges!");
        }

        return mapping;
    }

    @Override
    public List<Merge> getMerges() {
        return merges;
    }

    @Override
    public void setMerges(List<Merge> merges) {
        this.merges = merges;
    }

    @Override
    public Instance getInstance(int index) {
        if (dataset != null) {
            return dataset.instance(index);
        } else {
            throw new RuntimeException("dataset is null");
        }
    }

    @Override
    public int assignedCluster(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDataset(Dataset<? extends Instance> dataset) {
        this.dataset = dataset;
    }

    private void constructTree() {
        if (merges == null) {
            throw new RuntimeException("merges empty!");
        }
        logger.log(Level.INFO, "constructing tree, merge size:{0}", merges.size());
        Dump.array(mapping, "mapping");
        treeData = new DynamicTreeData();

        nodes = new DendroNode[merges.size() + 1];

        DendroNode current = null;
        DendroNode prev = null;
        //for (Merge m : getMerges()) {

        Merge m;
        for (int i = merges.size() - 1; i >= 0; i--) {
            m = merges.get(i);
            current = new DTreeNode();
            //bottom level
            if (prev == null) {
                prev = getNode(m.remainingCluster());
            }
            current.setLeft(prev);
            current.setRight(getNode(m.mergedCluster()));
            current.setHeight(m.similarity());
            prev = current;
            //  System.out.println("merge: " + m.mergedCluster() + " remain: " + m.remainingCluster() + " similarity = " + m.similarity());
        }
        numNodes = 0;
        //number leaves, so that we can compute it's position
        numberLeaves(current);
        updatePositions(current);

//        BTreePrinter.printNode(prev);
        treeData.setRoot(current);
        logger.log(Level.INFO, "max tree height: {0}", current.getHeight());
    }

    /**
     * Number leaves from 0 to {numNodes}
     *
     * @param node
     * @return
     */
    public int numberLeaves(DendroNode node) {
        int ll = 0;
        int lr = 0;
        int level;
        if (!node.isLeaf()) {
            if (node.hasLeft()) {
                ll = numberLeaves(node.getLeft());
            }
            if (node.hasRight()) {
                lr = numberLeaves(node.getRight());
            }
        } else {
            node.setPosition(numNodes++);
        }
        level = Math.max(ll, lr);
        node.setLevel(level);
        return level;
    }

    private double updatePositions(DendroNode node) {
        if (node.isLeaf()) {
            return node.getPosition();
        }

        double position = (updatePositions(node.getLeft()) + updatePositions(node.getRight())) / 2.0;
        node.setPosition(position);
        return position;
    }

    private DendroNode getNode(int idx) {
        if (nodes[idx] == null) {
            nodes[idx] = new DTreeNode();
            nodes[idx].setId(idx);
        }
        return nodes[idx];
    }
}
