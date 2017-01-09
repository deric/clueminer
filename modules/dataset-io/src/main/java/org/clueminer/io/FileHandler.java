/*
 * Copyright (C) 2011-2017 clueminer.org
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

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 * A class to load data sets from file and write them back.
 * <p>
 * The format of
 * the file should be as follows: One instance on each line, all values of an
 * entry on a single line, values should be separated by a tab character or
 * specified with the method and all entries should have the same number of
 * values.
 * <p>
 * Only class values are allowed to be non double. If a data set has
 * no labels, then all columns should be double values.
 *
 *
 * @author Thomas Abeel
 *
 */
public class FileHandler extends StreamHandler {

    /**
     * Utility method to load from a file without class set.
     *
     * @param f
     * @param dataset
     * @param separator
     * @return
     * @throws IOException
     */
    public static boolean loadDataset(File f, Dataset dataset, String separator) throws IOException {
        return loadDataset(f, dataset, -1, separator);
    }

    /**
     * This method will load the data stored in a file..
     * <p>
     * Only the column
     * with the class values is allowed to have different values than doubles.
     * <p>
     * Symbols that cannot be parsed to numbers will be converted to missing
     * values.
     *
     * @param f          the file to be loaded.
     * @param dataset
     * @param classIndex the index of the column that contains the class labels.
     *                   This index starts from zero for the first column and should not be
     *                   negative.
     * @return
     * @throws java.io.IOException
     */
    public static boolean loadDataset(File f, Dataset dataset, int classIndex) throws IOException {
        return loadDataset(f, dataset, classIndex, "\t");
    }

    public static boolean loadDataset(File f, Dataset dataset) throws IOException {
        return loadDataset(f, dataset, -1);

    }

    /**
     * Load the data from a file.
     * <p>
     * All columns should only contain double
     * values, except the class column which can only contain integer values.
     * <p>
     * Values that cannot be parsed to numbers will be entered as missing
     * values in the instances.
     * <p>
     * When the classIndex is outside the range of
     * available attributes, all instances will have the same class.
     *
     * @param f          the file to be loaded.
     * @param out        the dataset which will be used for storing the data
     * @param classIndex the index of the class value
     * @param separator  the symbol used to separate two fields, typically
     *                   ,(comma) ;(semi-colon) or \t (tab).
     * @return a data set containing the data from the file
     * @throws IOException
     */
    public static boolean loadDataset(File f, Dataset out, int classIndex, String separator) throws IOException {
        if (f.getName().endsWith("gz")) {
            return load(new InputStreamReader(new GZIPInputStream(new FileInputStream(f))), out, classIndex, separator);
        }
        if (f.getName().endsWith("zip")) {
            return load(new InputStreamReader(new ZipInputStream(new FileInputStream(f))), out, classIndex, separator);
        }
        return load(new InputStreamReader(new FileInputStream(f)), out, classIndex, separator);

    }

    public static boolean loadSparseDataset(File f, Dataset out, int classIndex) throws IOException {
        return loadSparseDataset(f, out, classIndex, "\t", ":");

    }

    public static boolean loadSparseDataset(File f, Dataset out, int classIndex, String attributeSeparator, String indexSep)
            throws IOException {
        if (f.getName().endsWith("gz")) {
            return loadSparse(new InputStreamReader(new GZIPInputStream(new FileInputStream(f))), out, classIndex, attributeSeparator, indexSep);
        }
        if (f.getName().endsWith("zip")) {
            return loadSparse(new InputStreamReader(new ZipInputStream(new FileInputStream(f))), out, classIndex, attributeSeparator, indexSep);
        }
        return loadSparse(new InputStreamReader(new FileInputStream(f)), out, classIndex, attributeSeparator, indexSep);
    }

    /**
     * Exports a data set to a file. Each instance is output separately with the
     * class label on position 0. The fields are delimited with a tab character.
     *
     * Note: data sets with mixed sparse and dense instances may not be loadable
     * with the load methods.
     *
     * @param data     data set
     * @param outFile  file to write data to
     * @param compress flag to indicate whether GZIP compression should be used.
     * @param sep
     * @throws IOException when something went wrong during the export
     */
    public static void exportDataset(Dataset<Instance> data, File outFile, boolean compress, String sep) throws IOException {
        PrintWriter out;
        if (compress) {
            out = new GZIPPrintWriter(outFile);
        } else {
            out = new PrintWriter(outFile);
        }
        for (Instance inst : data) {
            if (inst.classValue() != null) {
                out.print(inst.classValue() + sep);
            }
            out.println(inst.toString(sep));
        }

        out.close();

    }

    /**
     * Exports a data set to a file. Each instance is output separately with the
     * class label on position 0. The fields are delimited with a tab character.
     *
     * Note: data sets with mixed sparse and dense instances may not be loadable
     * with the load methods.
     *
     * By default compression of the output is turned off.
     *
     * @param data data set
     * @param file file to write data to
     * @throws IOException when something went wrong during the export
     */
    public static void exportDataset(Dataset data, File file) throws IOException {
        exportDataset(data, file, false);

    }

    public static void exportDataset(Dataset data, File file, boolean b) throws IOException {
        exportDataset(data, file, b, "\t");

    }
}
