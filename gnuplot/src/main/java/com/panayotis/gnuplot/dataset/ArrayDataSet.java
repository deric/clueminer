package com.panayotis.gnuplot.dataset;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

/**
 * Store data sets in a static primitive 2D array
 *
 * @author teras
 */
public class ArrayDataSet implements DataSet, Serializable {

    private String[][] val;
    private String[] labels;

    /**
     * Creates a new instance of ArrayDataSet from a double precision 2D array
     *
     * @param values the 2D array in double precision to retrieve data from
     */
    public ArrayDataSet(double[][] values) {
        int length = values.length;
        int dimension = values[0].length;
        int i, j;

        if (length == 0) {
            throw new ArrayStoreException("Array has zero points");
        }
        val = new String[length][dimension];
        for (i = 0; i < length; i++) {
            if (values[i].length != dimension) {
                throw new ArrayStoreException("Array has not consistent size, was " + dimension + ", found " + values[i].length);
            }
            for (j = 0; j < dimension; j++) {
                val[i][j] = Double.toString(values[i][j]);
            }
        }
    }

    /**
     * Creates a new instance of ArrayDataSet from a float precision 2D array
     *
     * @param values the 2D array in float precision to retrieve data from
     */
    public ArrayDataSet(float[][] values) {
        int length = values.length;
        int dimension = values[0].length;
        int i, j;

        if (length == 0) {
            throw new ArrayStoreException("Array has zero points");
        }
        val = new String[length][dimension];
        for (i = 0; i < length; i++) {
            if (values[i].length != dimension) {
                throw new ArrayStoreException("Array has not consistent size, was " + dimension + ", found " + values[i].length);
            }
            for (j = 0; j < dimension; j++) {
                val[i][j] = Float.toString(values[i][j]);
            }
        }
    }

    /**
     * Creates a new instance of ArrayDataSet from a int precision 2D array
     *
     * @param values the 2D array in int precision to retrieve data from
     */
    public ArrayDataSet(int[][] values) {
        int length = values.length;
        int dimension = values[0].length;
        int i, j;

        if (length == 0) {
            throw new ArrayStoreException("Array has zero points");
        }
        val = new String[length][dimension];
        for (i = 0; i < length; i++) {
            if (values[i].length != dimension) {
                throw new ArrayStoreException("Array has not consistent size, was " + dimension + ", found " + values[i].length);
            }
            for (j = 0; j < dimension; j++) {
                val[i][j] = Integer.toString(values[i][j]);
            }
        }
    }

    /**
     * Creates a new instance of ArrayDataSet from a long precision 2D array
     *
     * @param values the 2D array in long precision to retrieve data from
     */
    public ArrayDataSet(long[][] values) {
        int length = values.length;
        int dimension = values[0].length;
        int i, j;

        if (length == 0) {
            throw new ArrayStoreException("Array has zero points");
        }
        val = new String[length][dimension];
        for (i = 0; i < length; i++) {
            if (values[i].length != dimension) {
                throw new ArrayStoreException("Array has not consistent size, was " + dimension + ", found " + values[i].length);
            }
            for (j = 0; j < dimension; j++) {
                val[i][j] = Long.toString(values[i][j]);
            }
        }
    }

    /**
     * Creates a new instance of ArrayDataSet from a String 2D array. No check
     * on the data format is performed, can store any kind of value. Do not use
     * this method.
     *
     * @deprecated
     * @param values the 2D array in String format to retrieve data from
     */
    public ArrayDataSet(String[][] values) {
        int length = values.length;
        int dimension = values[0].length;
        int i, j;

        if (length == 0) {
            throw new ArrayStoreException("Array has zero points");
        }
        val = new String[length][dimension];
        for (i = 0; i < length; i++) {
            if (values[i].length != dimension) {
                throw new ArrayStoreException("Array has not consistent size, was " + dimension + ", found " + values[i].length);
            }
            System.arraycopy(values, 0, val, 0, values.length);
        }
    }

    /**
     * Retrieve how many points this data set has.
     *
     * @return the number of points
     */
    @Override
    public int size() {
        return val.length;
    }

    /**
     * Retrieve how many dimensions this dataset refers to.
     *
     * @return the number of dimensions
     * @see DataSet#getDimensions()
     */
    @Override
    public int getDimensions() {
        if (val[0] == null) {
            return -1;
        }
        return val[0].length;
    }

    public void setLabels(String[] labels) throws Exception {
        if (size() != labels.length) {
            throw new Exception("invalid lenght of array, expected " + size() + " but got " + labels.length);
        }
        this.labels = labels;
    }

    /**
     * Retrieve data information from a point.
     *
     * @param point The point number
     * @param dimension The point dimension (or "column") to request data from
     * @return the point data for this dimension
     * @see DataSet#getPointValue(int,int)
     */
    @Override
    public String getPointValue(int point, int dimension) {
        return val[point][dimension];
    }

    @Override
    public void save(String path) throws IOException {
        FileWriter fstream = new FileWriter(path);
        BufferedWriter out = new BufferedWriter(fstream);
        StringBuilder sb = new StringBuilder();
        int dim = getDimensions();
        boolean includeLabels = labels.length == 0 ? false : true;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < dim; j++) {
                sb.append(getPointValue(i, j)).append("\t");
            }
            if (includeLabels) {
                sb.append(labels[i]); //append class label
            }
            sb.append("\n");
        }
        out.write(sb.toString());
        out.close();
    }
}
