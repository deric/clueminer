package org.clueminer.clustering.algorithm;

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
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
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
    private int[] assign;
    private int[] mapping;
    private Assignments assignments;
    private int numClusters = -1;
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
    public int[] getIntAssignments() {
        return assign;
    }

    @Override
    public void setIntAssignments(int[] assignments) {
        this.assign = assignments;
    }

    public Assignments getAssignments() {
        if (assignments == null) {
            assignments = toAssignments(getIntAssignments(), getInputData(), getNumClusters());
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMappedIndex(int pos, int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] getMapping() {
        if (mapping == null) {
           if(merges == null){
               throw new RuntimeException("expecting precomputed dendrogram merges");
           }
           mapping = new int[merges.size()];
           for(Merge m :merges){
               //m.
           }
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
    
    
}
