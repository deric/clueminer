package org.clueminer.dendrogram;

import org.clueminer.cluster.HierachicalClusteringResult;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;

/**
 * Data used for rendering dendrogram, might contain rows and columns clustering
 * result.
 *
 * @author Tomas Barton
 */
public class DendrogramData implements DendrogramMapping {

    private Matrix matrix;
    private Dataset<? extends Instance> instances;
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;
    private double mid = 0;
    private HierarchicalResult rowsResult;
    private HierarchicalResult colsResult;

    public DendrogramData(Dataset<? extends Instance> dataset) {
        this.instances = dataset;

    }

    public DendrogramData(Dataset<? extends Instance> dataset, Matrix matrix, HierarchicalResult rowResult, HierarchicalResult columnResult) {
        this.instances = dataset;
        this.setMatrix(matrix);
        this.rowsResult = rowResult;
        this.colsResult = columnResult;
    }

    public DendrogramData(Dataset<? extends Instance> dataset, Matrix matrix, HierarchicalResult rowResult) {
        this.instances = dataset;
        this.setMatrix(matrix);
        this.rowsResult = (HierachicalClusteringResult) rowResult;
    }

    public boolean isEmpty() {
        if (getNumberOfColumns() == 0 && getNumberOfRows() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Returns column index
     *
     * @param column
     * @return
     */
    @Override
    public int getColumnIndex(int column) {
        return colsResult.getMappedIndex(column);
    }

    /**
     *
     * @param row
     * @return
     */
    @Override
    public int getRowIndex(int row) {
        return rowsResult.getMappedIndex(row);
    }

    /**
     * Returns number of instances
     *
     * @return
     */
    @Override
    public int getNumberOfRows() {
        return matrix.rowsCount();
    }

    /**
     * Returns number of parameters (features)
     *
     * @return
     */
    @Override
    public int getNumberOfColumns() {
        return matrix.columnsCount();
    }

    /**
     * @return the matrix
     */
    @Override
    public Matrix getMatrix() {
        return matrix;
    }

    /**
     * @param matrix the matrix to set
     */
    public final void setMatrix(Matrix matrix) {
        this.matrix = matrix;
        findMinMax(matrix);
    }

    private void findMinMax(Matrix matrix) {
        double value;
        for (int i = 0; i < matrix.rowsCount(); i++) {
            for (int j = 0; j < matrix.columnsCount(); j++) {
                value = matrix.get(i, j);
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
            }
        }
        //in case of negative min, we add it again
        //@test [10-(-5)] /2 + (-5) = 7.5 - 5 = 2.5
        //@test 10 - 0 = 5 + 0 = 5
        //@test 1 - (-1) = 1-1 = 0
        mid = (max - min) / 2 + min;
    }

    public double getMinValue() {
        return min;
    }

    public double getMaxValue() {
        return max;
    }

    public double getMidValue() {
        return mid;
    }

    public double get(int i, int j) {
        return matrix.get(i, j);
    }

    public double getMappedValue(int rowIndex, int columnIndex) {
        return get(rowsResult.getMappedIndex(rowIndex), colsResult.getMappedIndex(columnIndex));
    }

    /**
     * @return the instances
     */
    @Override
    public Dataset<? extends Instance> getInstances() {
        return instances;
    }

    /**
     * @param instances the instances to set
     */
    public void setInstances(Dataset<? extends Instance> instances) {
        this.instances = instances;
    }

    @Override
    public HierarchicalResult getRowsResult() {
        return rowsResult;
    }

    public void setRowsResult(HierarchicalResult rowsResult) {
        this.rowsResult = (HierachicalClusteringResult) rowsResult;
    }

    @Override
    public HierarchicalResult getColsResult() {
        return colsResult;
    }

    public void setColsResult(HierarchicalResult colsResult) {
        this.colsResult = (HierachicalClusteringResult) colsResult;
    }

    @Override
    public Clustering getRowsClustering() {
        return rowsResult.getClustering(instances);
    }

    @Override
    public Clustering getColumnsClustering() {
        return colsResult.getClustering(instances);
    }

    /**
     * Cuts dendrogram tree at specific level
     *
     * @param level
     */
    public void setRowsTreeCutoffByLevel(int level) {
        rowsResult.cutTreeByLevel(level);
    }

    public void setColumnsTreeCutoffByLevel(int level) {
        colsResult.cutTreeByLevel(level);
    }

    @Override
    public boolean hasRowsClustering() {
        return rowsResult != null && getNumberOfRows() > 0;
    }

    @Override
    public boolean hasColumnsClustering() {
        return colsResult != null && getNumberOfColumns() > 0;
    }
}
