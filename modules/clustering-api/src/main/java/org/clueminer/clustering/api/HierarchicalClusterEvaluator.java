package org.clueminer.clustering.api;

/**
 *
 * @author Tomas Barton
 */
public interface HierarchicalClusterEvaluator {

    public String getName();
    
    public double score(HierarchicalResult result);
}
