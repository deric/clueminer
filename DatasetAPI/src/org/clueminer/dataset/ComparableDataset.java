package org.clueminer.dataset;

import org.clueminer.instance.Instance;

/**
 * 
 * @author Tomas Barton
 */
public interface ComparableDataset extends Dataset<Instance> {
    
    public Instance getMin();
    
    public Instance getMax();
}
