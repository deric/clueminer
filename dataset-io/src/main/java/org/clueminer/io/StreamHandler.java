package org.clueminer.io;

import be.abeel.io.ColumnIterator;
import be.abeel.io.LineIterator;
import java.io.Reader;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.exception.UnsupportedAttributeType;
import org.openide.util.Exceptions;

/**
 * Simple dataset parser
 *
 * @author Tomas Barton
 */
public class StreamHandler {

    public static boolean loadSparse(Reader in, Dataset<Instance> out, int classIndex, String attSep, String indexSep) {

        ColumnIterator it = new ColumnIterator(in);
        it.setDelimiter(attSep);
        it.setSkipBlanks(true);
        it.setSkipComments(true);
        /*
         * to keep track of the maximum number of attributes
         */
        int maxAttributes = 0;
        for (String[] arr : it) {
            Instance inst = out.builder().create();

            for (int i = 0; i < arr.length; i++) {
                if (i == classIndex) {
                    inst.setClassValue(arr[i]);
                } else {
                    String[] tmp = arr[i].split(indexSep);
                    double val;
                    try {
                        val = Double.parseDouble(tmp[1]);
                    } catch (NumberFormatException e) {
                        val = Double.NaN;
                    }
                    inst.set(Integer.parseInt(tmp[0]), val);
                }
            }
            if (inst.size() > maxAttributes) {
                maxAttributes = inst.size();
            }
            out.add(inst);

        }
        for (Instance inst : out) {
            inst.setCapacity(maxAttributes);
        }
        return true;
    }

    /**
     *
     * @param in input Reader
     * @param out output Dataset
     * @param classIndex column number of class label in file
     * @param separator data columns separator
     * @return
     */
    public static boolean load(Reader in, Dataset<Instance> out, int classIndex, String separator) {

        LineIterator it = new LineIterator(in);
        it.setSkipBlanks(true);
        it.setSkipComments(true);
        boolean first = true;
        for (String line : it) {
            String[] arr = line.split(separator);
            if (first) {
                if (isHeader(arr)) {
                    createHeader(arr, out, classIndex);
                    //continue to next line
                    line = it.next();
                    arr = line.split(separator);
                }
                first = false;
            }
            double[] values;
            if (classIndex == -1) {
                values = new double[arr.length];
            } else {
                values = new double[arr.length - 1];
            }
            String classValue = null;
            for (int i = 0; i < arr.length; i++) {
                if (i == classIndex) {
                    classValue = arr[i];
                } else {
                    double val;
                    try {
                        val = Double.parseDouble(arr[i]);
                    } catch (NumberFormatException e) {
                        val = Double.NaN;
                    }
                    if (classIndex != -1 && i > classIndex) {
                        values[i - 1] = val;
                    } else {
                        values[i] = val;
                    }
                }
            }
            //creates an Instance of preferred type (dense, sparse, array based etc.)
            out.add(out.builder().create(values, classValue));

        }
        return true;
    }

    private static boolean isHeader(String row[]) {
        for (String attr : row) {
            try {
                Double.parseDouble(attr);                
                //if number is successfully parsed, it's not a header
                return false;
            } catch (NumberFormatException e) {
                // do nothing
            }
        }
        return true;
    }

    private static void createHeader(String row[], Dataset<Instance> dataset, int classIndex) {
        int i = 0;
        for (String attr : row) {
            try {
                if (i != classIndex) {
                    dataset.setAttribute(i, dataset.attributeBuilder().create(attr, "NUMERICAL"));
                }
                i++;
            } catch (UnsupportedAttributeType ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
