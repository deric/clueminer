package org.clueminer.clustering.api.dendrogram;

import org.clueminer.dataset.Dataset;
import org.clueminer.instance.Instance;
import org.clueminer.math.Matrix;

/**
 *
 * @author Tomas Barton
 */
public interface DendrogramMapping {
    
     public int getColumnIndex(int column);
     
     public int getRowIndex(int row);
     
     public int getNumberOfRows();
     
     public int getNumberOfColumns();
     
     public Matrix getMatrix();
     
     public Dataset<Instance> getInstances();
}
