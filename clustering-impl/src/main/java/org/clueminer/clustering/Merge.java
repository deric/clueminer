package org.clueminer.clustering;

/**
 * A status object that represents the result of agglomeratively merging two
 * clusters.  This class provides the information on which clusters were merged,
 * what the id of the remaining cluster is, and the similarity of the two
 * clusters at the point at which they were merged.
 *
 */
public class Merge implements java.io.Serializable {
    
    private static final long serialVersionUID = 7366397676154738636L;
    private final int remainingCluster;
    private final int mergedCluster;
    
    private final double similarity;

    public Merge(int remainingCluster, int mergedCluster, double similarity) {
        this.remainingCluster = remainingCluster;
        this.mergedCluster = mergedCluster;
        this.similarity = similarity;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Merge) {
            Merge m = (Merge)o;
            return m.remainingCluster == remainingCluster
                && m.mergedCluster == mergedCluster
                && m.similarity == similarity;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return remainingCluster ^ mergedCluster;
    }

    /**
     * Returns the ID of the cluster that was merged into another cluster.  
     */
    public int mergedCluster() {
        return mergedCluster;
    }

    /**
     * Returns the ID of the clusters into which another cluster was merged,
     * i.e. all the data points in the merged cluster would now have this ID.
     */
    public int remainingCluster() {
        return remainingCluster;
    }

    /**
     * Returns the similarity of the two clusters at the time of their merging.
     */
    public double similarity() {
        return similarity;
    }

    @Override
    public String toString() {
        return "(" + mergedCluster + " -> " + remainingCluster + ": "
            + similarity + ")";
    }
}
