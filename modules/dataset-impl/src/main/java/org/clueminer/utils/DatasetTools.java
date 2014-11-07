package org.clueminer.utils;

import org.clueminer.math.matrix.JMatrix;
import java.util.Map;
import java.util.Random;
import org.apache.commons.math3.stat.StatUtils;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.dataset.row.SparseInstance;
import org.clueminer.math.Standardisation;

/**
 * This class provides utility methods on data sets.
 *
 * @see Dataset
 * @see DefaultDataset
 *
 *
 * @author Thomas Abeel
 *
 */
final public class DatasetTools {

    /**
     * All data will be merged together in the first supplied data set.
     *
     * @param datasets a number of data sets
     *
     */
    public static void merge(Dataset<Instance>... datasets) {
        Dataset<Instance> out = null;
        for (Dataset<Instance> data : datasets) {
            if (out == null) {
                out = data;
            } else {
                out.addAll(data);
            }
        }
    }

    /**
     * Create an instance that contains all the maximum values for the
     * attributes.
     *
     * @param data - data set to find minimum attribute values for
     * @return Instance representing the minimum values for each attribute
     */
    public static Instance maxAttributes(Dataset<? extends Instance> data) {
        SparseInstance max = new SparseInstance();
        Map<Integer, Attribute> attr = data.getAttributes();
        int index;
        for (Instance i : data) {
            for (int j = 0; j < attr.size(); j++) {
                index = attr.get(j).getIndex();
                double val = i.value(index);
                if (!max.containsKey(index)) {
                    max.set(index, val);
                } else if (max.value(index) < val) {
                    max.set(index, val);
                }

            }

        }
        return max;
    }

    /**
     * Create an instance that contains all the minimum values for the
     * attributes.
     *
     * @param data - data set to calculate minimum attribute values for
     * @return Instance representing all minimum attribute values
     */
    public static Instance minAttributes(Dataset<? extends Instance> data) {
        SparseInstance min = new SparseInstance();
        Map<Integer, Attribute> attr = data.getAttributes();
        int index;
        for (Instance i : data) {
            for (int j = 0; j < attr.size(); j++) {
                index = attr.get(j).getIndex();
                double val = i.value(index);
                if (!min.containsKey(index)) {
                    min.set(index, val);
                } else if (min.value(index) > val) {
                    min.set(index, val);
                }
            }
        }
        return min;
    }

    /**
     * Creates an instance that contains the standard deviation of the values
     * for each attribute.
     *
     * @param data data set to calculate attribute value standard deviations for
     * @param avg the average instance for the data set
     * @return Instance representing the standard deviation of the values for
     * each attribute
     */
    public static Instance standardDeviation(Dataset<Instance> data, Instance avg) {
        /*
         * Instance sum = new DoubleArrayDataRow(avg.attrCount()); for (Instance
         * i : data) { Instance diff = i.minus(avg); sum =
         * sum.set(diff.multiply(diff)); } sum = sum.divide(data.size()); return
         * sum.sqrt();
         */
        throw new UnsupportedOperationException("not implemented yet");

    }

    /**
     * Creates an instance that contains the average values for the attributes.
     *
     * @param data data set to calculate average attribute values for
     * @return Instance representing the average attribute values
     */
    public static Instance average(Dataset<Instance> data) {
        int attrCount = data.attributeCount();
        if (attrCount == 0) {
            throw new RuntimeException("number of attributes should not be 0");
        }
        double[] tmpOut = new double[attrCount];
        for (int i = 0; i < attrCount; i++) {
            double sum = 0;
            for (int j = 0; j < data.size(); j++) {
                sum += data.instance(j).value(i);
            }
            tmpOut[i] = sum / data.size();

        }
        return new DoubleArrayDataRow(tmpOut);
    }

    /**
     * Calculates the percentile hinge for a given percentile.
     *
     * @param data - data set to calculate percentile for
     * @param perc - percentile to calculate, Q1=25, Q2=median=50,Q3=75
     * @return
     */
    public static Instance percentile(Dataset<Instance> data, double perc) {
        double[] tmpOut = new double[data.attributeCount()];
        for (int i = 0; i < data.attributeCount(); i++) {
            double[] vals = new double[data.size()];
            for (int j = 0; j < data.size(); j++) {
                vals[j] = data.instance(j).value(i);
            }
            tmpOut[i] = StatUtils.percentile(vals, perc);

        }
        return new DoubleArrayDataRow(tmpOut);
    }

    /**
     * Creates an Instance from the class labels over all Instances in a data
     * set.
     *
     * The indices of the class labels are used because the class labels can be
     * any Object.
     *
     * @param data data set to create class label instance for
     * @return instance with class label indices as values.
     */
    public static Instance createInstanceFromClass(Dataset<Instance> data) {
        Instance out = new DoubleArrayDataRow(data.size());
        for (Instance inst : data) {
            out.put((double) data.classIndex(inst.classValue()));
        }
        return out;
    }

    /**
     * Creates an Instance from the values of one particular attribute over all
     * Instances in a data set.
     *
     * @param data
     * @param i
     * @return
     */
    public static Instance createInstanceFromAttribute(Dataset<Instance> data, int i) {
        Instance out = new DoubleArrayDataRow(data.size());
        for (Instance inst : data) {
            out.put(inst.value(i));
        }
        return out;
    }

    /**
     * Finds the double[][] array where the first index reflects the attributes
     * and the second index the minimum (index 0) and maximum (index 1) of the
     * attributes.
     *
     * @param data data set to compute this array for
     *
     * @return a two-dimensional array with the minimum and maximum values per
     * attribute
     */
    private static double[][] getMinMax(Dataset<? extends Instance> data) {
        final int noAttributes = data.attributeCount();
        final int noInstances = data.size();
        final int MIN_INDEX = 0;
        final int MAX_INDEX = 1;
        // second index contains min and max -> therefore size = 2
        double[][] ret = new double[noAttributes][2];

        // reset min and max indices to Double.MAX_VALUE and Double.MIN_VALUE
        // resp;
        for (int a = 0; a < noAttributes; a++) {
            ret[a][MIN_INDEX] = Double.MAX_VALUE;
            ret[a][MAX_INDEX] = Double.MIN_VALUE;
        }

        // find min and max for each attribute
        double attrVal;
        Instance instance;
        for (int i = 0; i < noInstances; i++) { // for each instance
            instance = data.instance(i);
            for (int a = 0; a < noAttributes; a++) { // for each attribute
                attrVal = instance.value(a);
                if (attrVal < ret[a][MIN_INDEX]) {
                    ret[a][MIN_INDEX] = attrVal;
                }
                if (attrVal > ret[a][MAX_INDEX]) {
                    ret[a][MAX_INDEX] = attrVal;
                }

            }
        }
        return ret;
    }

    /**
     * Creates a random instance using the
     * {@link DatasetTools#getMinMax(Dataset)} method. The random instance is
     * created by choosing a random value for each attribute (which is between
     * the min and max value of the attribute).
     *
     * @param data - the data set
     * @return a random instance
     */
    public static double[] getRandomInstance(Dataset<? extends Instance> data, Random r) {
        final int MIN_INDEX = 0;
        final int MAX_INDEX = 1;
        double[][] minMax = getMinMax(data);
        final int noAttributes = minMax.length;
        double[] ret = new double[noAttributes];
        for (int a = 0; a < noAttributes; a++) {
            ret[a] = minMax[a][MIN_INDEX] + (minMax[a][MAX_INDEX] - minMax[a][MIN_INDEX]) * r.nextDouble();
        }
        return ret;
    }

    public static JMatrix createMatrix(Dataset<Instance> dataset, Standardisation std) {
        if (dataset.isEmpty()) {
            throw new NullPointerException("no data to process");
        }
        int m = dataset.size();
        int n = dataset.attributeCount();

        if ((m > 0) && (n > 0)) {
            double[][] res = dataset.arrayCopy();
            float[][] out = new float[m][n];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    out[i][j] = (float) res[i][j];
                }
            }

            return new JMatrix(std.optimize(res, m, n), m, n);
        }
        throw new ArrayIndexOutOfBoundsException("empty dataset - dimensions " + m + " x " + n);
    }

}
