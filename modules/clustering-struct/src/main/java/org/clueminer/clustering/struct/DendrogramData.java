/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering.struct;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data used for rendering dendrogram, might contain rows and columns clustering
 * result. Data is stored without using matrix, which is inefficient for such
 * purpose.
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class DendrogramData<E extends Instance, C extends Cluster<E>> implements DendrogramMapping<E, C> {

    private Dataset<E> normData;
    private Dataset<E> origData;
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;
    private double mid = 0;
    private HierarchicalResult rowsResult;
    private HierarchicalResult colsResult;
    private static final Logger LOG = LoggerFactory.getLogger(DendrogramData.class);

    public DendrogramData() {

    }

    public DendrogramData(Dataset<E> dataset) {
        setDataset(dataset);
    }

    public DendrogramData(Dataset<E> dataset, HierarchicalResult rowResult, HierarchicalResult columnResult) {
        setDataset(dataset);
        checkParams(rowResult, columnResult);
        setMapping(rowResult, columnResult);
    }

    public DendrogramData(Dataset<E> dataset, HierarchicalResult rowResult) {
        setDataset(dataset);
        checkParams(rowResult, null);
        setMapping(rowResult, null);
    }

    private void checkParams(HierarchicalResult rowResult, HierarchicalResult columnResult) {
        if (normData.size() != rowResult.size()) {
            throw new RuntimeException("row result size does not match dimension of input matrix " + normData.size() + " vs. " + rowResult.size());
        }
        if (columnResult != null) {
            if (normData.attributeCount() != columnResult.size()) {
                throw new RuntimeException("column result size does not match dimension of input matrix " + columnResult.size() + " vs. " + normData.attributeCount());
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
    public final void setDataset(Dataset<E> dataset) {
        this.normData = dataset;
        Dataset<E> current = normData;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        origData = current;
        //in case of negative min, we add it again
        //@test [10-(-5)] /2 + (-5) = 7.5 - 5 = 2.5
        //@test 10 - 0 = 5 + 0 = 5
        //@test 1 - (-1) = 1-1 = 0
        min = normData.min();
        max = normData.max();
        mid = (max - min) / 2 + min;
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
        if (colsResult != null) {
            return colsResult.getMappedIndex(column);
        } else {
            return column; //same ordering as original data
        }
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
        if (normData != null) {
            return normData.size();
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
        if (normData != null) {
            return normData.attributeCount();
        } else {
            return 0;
        }
    }

    /**
     * @return the matrix
     */
    @Override
    public Matrix getMatrix() {
        throw new UnsupportedOperationException("not supported");
    }

    /**
     * @param matrix the matrix to set
     */
    @Override
    public final void setMatrix(Matrix matrix) {
        //this.matrix = matrix;
        throw new UnsupportedOperationException("not supported");
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
        if (normData != null) {
            return normData.get(i, j);
        } else {
            return Double.NaN;
        }
    }

    @Override
    public double getMappedValue(int rowIndex, int columnIndex) {
        return get(rowsResult.getMappedIndex(rowIndex), getColumnIndex(columnIndex));
    }

    @Override
    public double getOriginalValue(int rowIndex, int columnIndex) {
        if (origData != null) {
            return origData.get(rowsResult.getMappedIndex(rowIndex), getColumnIndex(columnIndex));
        } else {
            return Double.NaN;
        }
    }

    /**
     * @return the instances
     */
    @Override
    public Dataset<E> getDataset() {
        return normData;
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
    public void printMappedMatrix(int d) {
        if (!hasRowsClustering()) {
            LOG.error("missing row clustering");
            return;
        }
        if (!hasColumnsClustering()) {
            LOG.error("missing column clustering");
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
