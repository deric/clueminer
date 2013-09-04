package org.clueminer.clustering.algorithm;

import com.google.common.primitives.Ints;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.clueminer.clustering.AssigmentsImpl;
import org.clueminer.clustering.HardAssignment;
import org.clueminer.clustering.api.Merge;
import org.clueminer.clustering.api.Assignment;
import org.clueminer.clustering.api.Assignments;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.hclust.DynamicTreeData;
import org.clueminer.math.Matrix;

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
    private DendroData treeData;
    private double cutoff = Double.NaN;
    
    /**
     * list of dendrogram levels - each Merge represents one dendrogram level
     */
    List<Merge> merges;

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clustering getClustering(Dataset<? extends Instance> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] getClusters(int terminalsNum) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCutoff(double cutoff) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int treeLevels() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double treeHeightAt(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int treeOrder(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getMaxTreeHeight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
