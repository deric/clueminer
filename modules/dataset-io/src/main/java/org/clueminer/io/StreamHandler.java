/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.io;

import java.io.Reader;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

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

        }
        for (Instance inst : out) {
            inst.setCapacity(maxAttributes);
        }
        return true;
    }

    /**
     *
     * @param in         input Reader
     * @param out        output Dataset
     * @param classIndex column number of class label in file
     * @param separator  data columns separator
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
            out.builder().create(values, classValue);

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
            if (i != classIndex) {
                dataset.attributeBuilder().create(attr, "NUMERICAL");
            }
            i++;

        }
    }
}
