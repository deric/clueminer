package org.clueminer.clustering.struct;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Cluster;
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
 * @param <E>
 * @param <C>
 */
public class DendroMatrixData<E extends Instance, C extends Cluster<E>> implements DendrogramMapping<E, C> {

    private Matrix matrix;
    private Dataset<E> dataset;
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;
    private double mid = 0;
    private HierarchicalResult rowsResult;
    private HierarchicalResult colsResult;
    private static final Logger log = Logger.getLogger(DendroMatrixData.class.getName());

    public DendroMatrixData() {

    }

    public DendroMatrixData(Dataset<E> dataset) {
        this.dataset = dataset;

    }

    public DendroMatrixData(Dataset<E> dataset, Matrix matrix, HierarchicalResult rowResult, HierarchicalResult columnResult) {
        this.dataset = dataset;
        // checkParams(matrix, rowResult, columnResult);
        this.setMatrix(matrix);
        setMapping(rowResult, columnResult);
    }

    public DendroMatrixData(Dataset<E> dataset, Matrix matrix, HierarchicalResult rowResult) {
        this.dataset = dataset;
        // checkParams(matrix, rowResult, null);
        this.setMatrix(matrix);
        setMapping(rowResult, null);
    }

    private void checkParams(Matrix matrix, HierarchicalResult rowResult, HierarchicalResult columnResult) {
        if (matrix == null) {
            throw new RuntimeException("input matrix can't be null");
        }
        if (matrix.rowsCount() != rowResult.size()) {
            throw new RuntimeException("row result size does not match dimension of input matrix " + rowResult.size() + " vs. " + matrix.rowsCount());
        }
        if (columnResult != null) {
            if (matrix.columnsCount() != columnResult.size()) {
                throw new RuntimeException("column result size does not match dimension of input matrix " + columnResult.size() + " vs. " + matrix.columnsCount());
            }
        }
    }

    private void setMapping(HierarchicalResult rowResult, HierarchicalResult columnResult) {
        this.rowsResult = rowResult;
        rowResult.setDendrogramMapping(this);
        if (columnResult != null) {
            this.colsResult = columnResult;
            colsResult.setDendrogramMapping(this);
        }
    }

    @Override
    public boolean isEmpty() {
        return getNumberOfColumns() == 0 && getNumberOfRows() == 0;
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
        if (dataset != null) {
            return dataset.size();
        } else {
            return 0;
        }
    }

    /**
     * Returns number of parameters (features)
     *
     * @return
     */
    @Override
    public int getNumberOfColumns() {
        if (dataset != null) {
            return dataset.attributeCount();
        } else {
            return 0;
        }
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
    @Override
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

    @Override
    public double getMinValue() {
        return min;
    }

    @Override
    public double getMaxValue() {
        return max;
    }

    @Override
    public double getMidValue() {
        return mid;
    }

    @Override
    public double get(int i, int j) {
        return matrix.get(i, j);
    }

    @Override
    public double getMappedValue(int rowIndex, int columnIndex) {
        return get(rowsResult.getMappedIndex(rowIndex), colsResult.getMappedIndex(columnIndex));
    }

    /**
     * @return the instances
     */
    @Override
    public Dataset<E> getDataset() {
        return dataset;
    }

    /**
     * @param instances the instances to set
     */
    @Override
    public void setDataset(Dataset<E> instances) {
        this.dataset = instances;
    }

    @Override
    public HierarchicalResult getRowsResult() {
        return rowsResult;
    }

    @Override
    public void setRowsResult(HierarchicalResult rowsResult) {
        this.rowsResult = rowsResult;
    }

    @Override
    public HierarchicalResult getColsResult() {
        return colsResult;
    }

    @Override
    public void setColsResult(HierarchicalResult colsResult) {
        this.colsResult = colsResult;
    }

    @Override
    public Clustering getRowsClustering() {
        return rowsResult.getClustering();
    }

    @Override
    public Clustering getColumnsClustering() {
        return colsResult.getClustering();
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

    @Override
    public void printMappedMatix(int d) {
        if (!hasRowsClustering()) {
            log.severe("missing row clustering");
            return;
        }
        if (!hasColumnsClustering()) {
            log.severe("missing column clustering");
            return;
        }
        DecimalFormat format = new DecimalFormat();
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        printMatrix(new PrintWriter(System.out, true), format, getNumberOfRows(), getNumberOfColumns(), d + 5);
    }

    public void printMatrix(PrintWriter output, NumberFormat format, int m, int n, int width) {
        output.println();  // start on new line.
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String s = format.format(getMappedValue(i, j)); // format the number
                int padding = Math.max(1, width - s.length()); // At _least_ 1 space
                for (int k = 0; k < padding; k++) {
                    output.print(' ');
                }
                output.print(s);
            }
            output.println();
        }
        output.println();   // end with blank line.
    }
}
