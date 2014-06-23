package org.clueminer.clustering.algorithm;

import com.google.common.primitives.Ints;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.struct.ClusterList;
//import org.clueminer.cluster.ClusterList;
import org.clueminer.clustering.AssigmentsImpl;
import org.clueminer.clustering.HardAssignment;
import org.clueminer.clustering.api.Merge;
import org.clueminer.clustering.api.Assignment;
import org.clueminer.clustering.api.Assignments;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroLeaf;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.hclust.DTreeNode;
import org.clueminer.hclust.DynamicTreeData;
import org.clueminer.hclust.HillClimbCutoff;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Dump;

/**
 *
 * @author Tomas Barton
 */
public class HClustResult implements HierarchicalResult {

    private static final long serialVersionUID = -515379303769981129L;
    private Matrix proximity;
    private Matrix inputData;
    private int[] mapping;
    private Assignments assignments;
    private int numClusters = -1;
    private DendroTreeData treeData;
    private double cutoff = Double.NaN;
    private Dataset<? extends Instance> dataset;
    private static final Logger logger = Logger.getLogger(HClustResult.class.getName());
    private int numNodes = 0;
    private Clustering clustering = null;
    private CutoffStrategy cutoffStrategy = new HillClimbCutoff(InternalEvaluatorFactory.getInstance().getDefault());
    private final Map<String, Map<Integer, Double>> scores = new HashMap<String, Map<Integer, Double>>();

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
        if (m.rowsCount() != m.columnsCount()) {
            throw new RuntimeException("expected square matrix, got " + m.rowsCount() + " x " + m.columnsCount());
        }
        this.proximity = m;
    }

    @Override
    public void setMapping(int[] assignments) {
        treeData.setMapping(assignments);
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

        int estClusters = (int) Math.sqrt(dataset.size());
        Clustering result = new ClusterList(estClusters);

        //estimated capacity
        int perCluster = (int) (parent.size() / (float) estClusters);

        //proximity.printLower(5, 2);
        // similarity.print(4, 2);
        return result;
    }

    @Override
    public int[] getClusters(int terminalsNum) {
        return mapping;
    }

    @Override
    public Clustering updateCutoff(double cutoff) {
        this.cutoff = cutoff;
        int[] assign = new int[dataset.size()];
        int estClusters = (int) Math.sqrt(dataset.size());
        Clustering clusters = new ClusterList(estClusters);
        DendroNode root = treeData.getRoot();
        if (root != null) {
            checkCutoff(root, cutoff, clusters, assign);
            if (clusters.size() > 0) {
                mapping = assign;
            }
        }
        logger.info(clusters.toString());
        //add input dataset to clustering lookup
        clusters.lookupAdd(dataset);
        clusters.lookupAdd(this);
        return clusters;
    }

    private void checkCutoff(DendroNode node, double cutoff, Clustering clusters, int[] assign) {
        if (node.isLeaf()) {
            return;
        }
        if (node.getHeight() <= cutoff) {
            //both branches goes to the same cluster
            Cluster clust = clusters.createCluster();
            subtreeToCluster(node, clust, assign);
        } else {
            checkCutoff(node.getLeft(), cutoff, clusters, assign);
            checkCutoff(node.getRight(), cutoff, clusters, assign);
        }
    }

    private void subtreeToCluster(DendroNode node, Cluster c, int[] assign) {
        if (node.isLeaf()) {
            c.add(((DendroLeaf) node).getData());
            assign[node.getId()] = c.getClusterId();
        } else {
            subtreeToCluster(node.getLeft(), c, assign);
            subtreeToCluster(node.getRight(), c, assign);
        }
    }

    @Override
    public double getCutoff() {
        return cutoff;
    }

    @Override
    public double cutTreeByLevel(int level) {
        DendroNode node = treeData.getRoot();
        double cut = findLevel(node, level);
        updateCutoff(cut);
        return cut;
    }

    private double findLevel(DendroNode node, int level) {
        if (node.level() == level) {
            return (node.getParent().getHeight() + node.getHeight()) / 2.0;
        } else {
            double ret = findLevel(node.getLeft(), level);
            if (ret > -1) {
                return ret;
            }
            ret = findLevel(node.getRight(), level);
            if (ret > -1) {
                return ret;
            }
        }
        return -1;
    }

    @Override
    public double findCutoff() {
        double cut = cutoffStrategy.findCutoff(this);
        updateCutoff(cut);
        System.out.println(treeData.toString());
        return cut;
    }

    @Override
    public double findCutoff(CutoffStrategy strategy) {
        double cut = strategy.findCutoff(this);
        updateCutoff(cut);
        return cut;
    }

    @Override
    public Map<Integer, Double> getScores(String evaluator) {
        if (scores.containsKey(evaluator)) {
            return scores.get(evaluator);
        }
        return null;
    }

    @Override
    public double getScore(String evaluator, int clustNum) {
        return this.scores.get(evaluator).get(clustNum);
    }

    @Override
    public void setScores(String evaluator, int clustNum, double sc) {
        if (this.scores.containsKey(evaluator)) {
            this.scores.get(evaluator).put(clustNum, sc);
        } else {
            Map<Integer, Double> hm = new HashMap<Integer, Double>();
            hm.put(clustNum, sc);
            this.scores.put(evaluator, hm);
        }
    }

    @Override
    public boolean isScoreCached(String evaluator, int clustNum) {
        if (this.scores.containsKey(evaluator)) {
            if (this.scores.get(evaluator).containsKey(clustNum)) {
                return true;
            }
        }
        return false;
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
        if (treeData == null) {
            throw new RuntimeException("Empty tree data");
        }
        return treeData.getMappedId(idx);
    }

    @Override
    public void setMappedIndex(int pos, int idx) {
        // if (treeData == null) {
        throw new RuntimeException("not available yet");
        //}
        //treeData.set
    }

    private void updateMapping() {
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
    }

    @Override
    public int[] getMapping() {
        /* if (mapping == null && merges != null) {
         updateMapping();
         }

         return mapping;*/
        if (treeData != null) {
            return treeData.getMapping();
        }
        return null;
    }

    @Override
    public List<Merge> getMerges() {
        return merges;
    }

    @Override
    public void setMerges(List<Merge> merges) {
        this.merges = merges;
        updateMapping();
        constructTree();
    }

    /**
     * During clustering we usually change order of rows, this should translate
     * indexes of rows to instance indexes
     *
     * @param index row index, starting from 0
     * @return
     */
    @Override
    public Instance getInstance(int index) {
        if (dataset != null) {
            return dataset.instance(getMappedIndex(index));
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
        treeData = new DynamicTreeData();

        DendroNode[] nodes = new DendroNode[merges.size() + 1];

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

        treeData.setRoot(current);
        treeData.setLeaves(nodes);
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

    /**
     * Recursive tree nodes positions update
     *
     * @TODO move this methods to tree itself
     * @param node
     * @return
     */
    private double updatePositions(DendroNode node) {
        if (node.isLeaf()) {
            return node.getPosition();
        }

        double position = (updatePositions(node.getLeft()) + updatePositions(node.getRight())) / 2.0;
        node.setPosition(position);
        return position;
    }

    private DendroNode getNode(int idx) {
        DendroNode node = treeData.getLeaf(idx);
        if (node == null) {
            node = new DTreeNode();
            node.setId(idx);
        }
        return node;
    }

    @Override
    public void setTreeData(DendroTreeData treeData) {
        this.treeData = treeData;
        updatePositions(treeData.getRoot());
    }

    /**
     * It's a square matrix, doesn't matter which dimension we'll return
     *
     * @return
     */
    @Override
    public int size() {
        return proximity.rowsCount();
    }

}
