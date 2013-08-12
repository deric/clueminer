package org.clueminer.clustering.api.dendrogram;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
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
     
     public Dataset<? extends Instance> getInstances();
     
     /**
      * 
      * @return true when rows clustering is available
      */
     public boolean hasRowsClustering();
     
     /**
      * 
      * @return true when columns clustering is available
      */
     public boolean hasColumnsClustering();
}
