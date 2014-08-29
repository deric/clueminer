package org.clueminer.dataset.api;

/**
 * 
 * @author Tomas Barton
 */
public interface ComparableDataset extends Dataset<Instance> {
    
    public Instance getMin();
    
    public Instance getMax();
}
