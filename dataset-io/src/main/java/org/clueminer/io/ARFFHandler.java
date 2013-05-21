package org.clueminer.io;

import be.abeel.io.LineIterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.utils.DatasetLoader;

/**
 * Provides method to load data from ARFF formatted files.
 *
 * For a detailed description on the ARFF format, please see
 * http://weka.wikispaces.com/ARFF+(stable+version)
 *
 * @author Thomas Abeel
 * @author Tomas Barton
 *
 */
public class ARFFHandler implements DatasetLoader {

    /**
     * matches eg. "
     *
     * @RELATION iris"
     */
    private static Pattern relation = Pattern.compile("^@relation\\s+(\\w*)", Pattern.CASE_INSENSITIVE);
    /**
     * matches eg. "
     *
     * @ATTRIBUTE sepallength	REAL"
     */
    private static Pattern attribute = Pattern.compile("^@attribute\\s+['\"]?([\\w .\\\\/]*)['\"]?\\s+([\\w]*)", Pattern.CASE_INSENSITIVE);

    /**
     * Load a data set from an ARFF formatted file. Due to limitations in the
     * Java-ML design only numeric attributes can be read. This method does not
     * read class labels.
     *
     * @param file the file to read the data from
     *
     * @return the data set represented in the provided file
     * @throws FileNotFoundException if the file can not be found.
     */
    @Override
    public boolean load(File file, Dataset dataset) throws FileNotFoundException, UnsupportedAttributeType {
        return load(file, dataset, -1, ",", new ArrayList<Integer>());
    }

    /**
     * Load a data set from an ARFF formatted file. Due to limitations in the
     * Java-ML design only numeric attributes can be read.
     *
     * @param file - the file to read the data from
     * @param classIndex - the index of the class label
     * @return the data set represented in the provided file
     * @throws FileNotFoundException - if the file can not be found
     */
    public boolean load(File file, Dataset dataset, int classIndex) throws FileNotFoundException, UnsupportedAttributeType {
        return load(file, dataset, classIndex, ",", new ArrayList<Integer>());
    }

    /**
     *
     * @param file
     * @param classIndex - indexed from zero!
     * @param separator - for eliminating all white characters (" ",\n, \t) use
     * "\\s+"
     * @param skippedIndexes - indexes of columns that won't be imported
     * @return
     * @throws FileNotFoundException
     * @throws IllegalArgumentException when type is not convertible to Enum
     * IAttributeType
     */
    public boolean load(File file, Dataset out, int classIndex, String separator, ArrayList<Integer> skippedIndexes) throws FileNotFoundException, UnsupportedAttributeType {
        LineIterator it = new LineIterator(file);
        it.setSkipBlanks(true);
        it.setCommentIdentifier("%");
        it.setSkipComments(true);

        Matcher rmatch;
        Matcher amatch;

        //number of attributes
        int attrNum = 0;
        int headerLine = 0;
        /*
         * Indicates whether we are reading data
         */
        boolean dataMode = false;
        for (String line : it) {
            /*
             * When we passed the @data tag, we are reading data
             */
            if (dataMode) {
                String[] arr = line.split(separator);
                double[] values;
                int skipSize = skippedIndexes.size();
                int skip = 0;
                if (classIndex >= 0) {
                    skipSize++; //smaller array is enough
                }
                values = new double[arr.length - skipSize];

                String classValue = null;
                for (int i = 0; i < arr.length; i++) {
                    if (i == classIndex) {
                        classValue = arr[i];
                        skip++;
                    } else {
                        double val;
                        if (!skippedIndexes.contains(i)) {
                            try {
                                val = Double.parseDouble(arr[i]);
                            } catch (NumberFormatException e) {
                                val = Double.NaN;
                            }
                            values[i - skip] = val;
                        } else {
                            skip++;
                        }
                    }
                }
                out.add(out.builder().create(values, classValue));
            } else {
                if ((rmatch = relation.matcher(line)).matches()) {
                    out.setName(rmatch.group(1));
                } else if ((amatch = attribute.matcher(line)).matches()) {
                    if (!skippedIndexes.contains(headerLine)) {
                        //tries to convert string to enum, at top level we should catch the
                        //exception
                        out.setAttribute(attrNum, out.attributeBuilder().create(amatch.group(1), amatch.group(2).toUpperCase()));
                        attrNum++;
                    }
                    headerLine++;

                } else if (line.equalsIgnoreCase("@data")) {
                    dataMode = true;
                }
            }
        }
        return true;
    }
    
    protected boolean isValidAttributeDefinition(String line){
        Matcher amatch;
        if ((amatch = attribute.matcher(line)).matches()){
            return true;
        }
        return false;
    }
}
