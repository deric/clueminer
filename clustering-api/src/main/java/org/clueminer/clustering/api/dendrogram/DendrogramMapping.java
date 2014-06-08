package org.clueminer.clustering.api.dendrogram;

import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
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

    /**
     * Set input data matrix for clustring
     *
     * @param input
     */
    public void setMatrix(Matrix input);

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

    public HierarchicalResult getRowsResult();

    /**
     * Set result of rows clustering
     *
     * @param rowsResult
     */
    public void setRowsResult(HierarchicalResult rowsResult);

    public HierarchicalResult getColsResult();

    public void setColsResult(HierarchicalResult colsResult);

    public Clustering getRowsClustering();

    public Clustering getColumnsClustering();

    /**
     * Get value at given position
     *
     * @param x row number, starts from 0
     * @param y column number, starts from 0
     * @return
     */
    public double get(int x, int y);

    public double getMappedValue(int rowIndex, int columnIndex);

    public double getMinValue();

    public double getMaxValue();

    public double getMidValue();

    public boolean isEmpty();

    /**
     * Print matrix with applied clustering mapping
     *
     * @param d number of decimal places
     */
    public void printMappedMatix(int d);
}
