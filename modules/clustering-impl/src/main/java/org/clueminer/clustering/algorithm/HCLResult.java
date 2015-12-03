package org.clueminer.clustering.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.Merge;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.factory.CutoffStrategyFactory;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.colors.ColorBrewer;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.hclust.TreeDataImpl;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton use {@link HClustResult} instead
 * @param <E>
 */
@Deprecated
public class HCLResult<E extends Instance, C extends Cluster<E>> implements HierarchicalResult<E, C> {

    private static final long serialVersionUID = 2779535800981843584L;
    private Matrix proximity;
    private TreeDataImpl treeData;
    private final Map<String, Map<Integer, Double>> scores = new HashMap<>();
    private CutoffStrategy cutoffStrategy;
    private int[] itemsMapping;
    private Matrix inputData;
    private Clustering clustering = null;
    private DendrogramMapping dendroMapping;
    private static final Logger logger = Logger.getLogger(HCLResult.class.getName());
    /**
     * original dataset
     */
    private Dataset<E> dataset;

    public HCLResult(Dataset<E> dataset) {
        this.dataset = dataset;
        cutoffStrategy = CutoffStrategyFactory.getInstance().getProvider("naive cutoff");
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
    public TreeDataImpl getTreeData() {
        return treeData;
    }

    public void setTreeData(TreeDataImpl tree) {
        this.treeData = tree;
        createDefaultMapping();
    }

    @Override
    public void setTreeData(DendroTreeData treeData) {
        this.treeData = (TreeDataImpl) treeData;
        createDefaultMapping();
    }

    public void createDefaultMapping() {
        itemsMapping = treeData.createTreeOrder();
    }

    @Override
    public int[] getMapping() {
        return itemsMapping;
    }

    @Override
    public int getNumClusters() {
        return treeData.getNumberOfClusters();
    }

    @Override
    public Clustering getClustering() {
        if (clustering == null) {
            updateClustering();
        }
        return clustering;
    }

    public Clustering updateClustering() {
        clustering = getClustering(dataset);
        /**
         * TODO: fire result?
         */
        return clustering;
    }

    /**
     * @TODO adjust also for columns clustering
     *
     * @param parent
     *
     * @return
     */
    @Override
    public Clustering getClustering(Dataset<E> parent) {
        setDataset(parent);

        //we need number of instances in dataset
        int[] clusters = treeData.getClusters(parent.size());
        ClusterList result = new ClusterList(treeData.getNumberOfClusters());
        logger.log(Level.INFO, "created clustering result with capacity {0}", new Object[]{result.getCapacity()});
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
        Cluster<E> clust;
        ColorGenerator cg = new ColorBrewer();
        //Dump.array(clusters, "clusters-assignment");
        //Dump.array(itemsMapping, "items-mapping");
        for (int i = 0; i < clusters.length; i++) {
            num = clusters[i] - 1; //numbering starts from 1
            //if clustering wasn't computed yet, cluster number == -1
            if (num >= 0) {
                if (!result.hasAt(num)) {
                    clust = new BaseCluster<>(perCluster);
                    clust.setColor(cg.next());
                    clust.setName("cluster " + (num + 1));
                    clust.setClusterId(num);
                    clust.setParent(parent);
                    clust.setAttributes(parent.getAttributes());
                    //logger.log(Level.INFO, "created cluster {0} with capacity {1}", new Object[]{num, perCluster});
                    result.put(num, clust);
                } else {
                    clust = result.get(num);
                }
                idx = itemsMapping[i];
                //logger.log(Level.WARNING, "adding {0} to cluster {1}", new Object[]{getVector(idx).getName(), num});
                //mapping is tracked in cluster
                // values in cluster array doesn't need mapping!
                /**
                 * TODO the orig idx is probably very useless
                 */
                //clust.add(dataset.get(i), idx);
                clust.add(dataset.get(i));
                //logger.log(Level.WARNING, "{0} -> {1}: clust num: {4} |{2}| = {3}, {5}", new Object[]{i, idx, clust.getName(), clust.size(), num, dataset.get(i).classValue()});
            }
        }
        /* for (Object c : result) {
         logger.log(Level.INFO, "{0}", c.toString());
         }*/
        //proximity.printLower(5, 2);
        // similarity.print(4, 2);
        result.lookupAdd(dataset);
        return result;
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
            Map<Integer, Double> hm = new HashMap<>();
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
    public Clustering updateCutoff(double cutoff) {
        treeData.setCutoff(cutoff);
        //maximum number of clusters is number of instances
        treeData.formClusters(proximity.rowsCount());

        return updateClustering();
    }

    @Override
    public double getCutoff() {
        return treeData.getCutoff();
    }

    @Override
    public double findCutoff() {
        double cut = cutoffStrategy.findCutoff(this, getParams());
        updateCutoff(cut);
        System.out.println(treeData.toString());
        return cut;
    }

    @Override
    public double findCutoff(CutoffStrategy strategy) {
        double cut = strategy.findCutoff(this, getParams());
        updateCutoff(cut);
        return cut;
    }

    @Override
    public double cutTreeByLevel(int level) {
        double cut = treeData.treeCutByLevel(level);
        updateCutoff(cut);
        return cut;
    }

    @Override
    public Dataset<E> getDataset() {
        return dataset;
    }

    @Override
    public void setDataset(Dataset<E> dataset) {
        this.dataset = dataset;
    }

    @Override
    public int treeLevels() {
        return treeData.treeLevels();
    }

    @Override
    public double treeHeightAt(int idx) {
        return treeData.getHeight(idx);
    }

    @Override
    public int treeOrder(int idx) {
        return treeData.getOrder(idx);
    }

    @Override
    public int[] getClusters(int terminalsNum) {
        return treeData.getClusters(terminalsNum);
    }

    @Override
    public double getMaxTreeHeight() {
        return treeData.getMaxHeight();
    }

    @Override
    public int getMappedIndex(int idx) {
        return itemsMapping[idx];
    }

    @Override
    public void setMappedIndex(int pos, int idx) {
        itemsMapping[pos] = idx;
    }

    /**
     * Mapping between dendrogram order and input matrix
     *
     * @param mapping
     */
    @Override
    public void setMapping(int[] mapping) {
        this.itemsMapping = mapping;
    }

    @Override
    public void setNumClusters(int num) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public List<Merge> getMerges() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMerges(List<Merge> merges) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Return Instance at given position in dendrogram
     *
     * @param index either row or column index in dendrogram
     * @return
     */
    @Override
    public Instance getVector(int index) {
        return dataset.get(this.getMappedIndex(index));
    }

    @Override
    public int assignedCluster(int idx) {
        if (clustering == null) {
            return 0;
        }
        int assig = clustering.assignedCluster(idx);
        if (assig != -1) {
            return assig;
        }
        //this shouldn't happen :)
        return 0;
    }

    public CutoffStrategy getCutoffStrategy() {
        return cutoffStrategy;
    }

    /**
     * Strategy for cutting dendrogram tree
     *
     * @param cutoffStrategy
     */
    public void setCutoffStrategy(CutoffStrategy cutoffStrategy) {
        this.cutoffStrategy = cutoffStrategy;
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

    @Override
    public boolean hasClustering() {
        return (clustering != null);
    }

    @Override
    public Props getParams() {
        return new Props();
    }

    @Override
    public E getInstance(int index) {
        return dataset.get(this.getMappedIndex(index));
    }

    @Override
    public DendrogramMapping getDendrogramMapping() {
        return dendroMapping;
    }

    @Override
    public void setCutoff(double cutoff) {
        //TODO: estimate cutoff
    }

    @Override
    public void setClustering(Clustering clustering) {
        //
    }

    @Override
    public void setResultType(ClusteringType type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getHeightByLevel(int level) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setNoise(List<Instance> noise) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDendrogramMapping(DendrogramMapping dendroMap) {
        this.dendroMapping = dendroMap;
    }

    @Override
    public DendroNode findTreeBelow(DendroNode node, double x, double y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
