package org.clueminer.io;

import be.abeel.io.LineIterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.utils.DatasetLoader;

/**
 *
 * @author Tomas Barton
 */
public class CsvLoader implements DatasetLoader {

    @Override
    public boolean load(File file, Dataset output) throws FileNotFoundException, UnsupportedAttributeType {
        return load(file, output, 2, ",", new ArrayList<Integer>());
    }
    
    /**
     * 
     * @param file  input CSV file
     * @param out   dataset for storing data
     * @param classIndex    index starts from zero
     * @param separator     CSV separator character
     * @param skippedIndexes    columns indexes which won't be loaded
     * @return
     * @throws FileNotFoundException
     * @throws UnsupportedAttributeType 
     */
    public boolean load(File file, Dataset out, int classIndex, String separator, ArrayList<Integer> skippedIndexes) throws FileNotFoundException, UnsupportedAttributeType {
        LineIterator it = new LineIterator(file);
        it.setSkipBlanks(true);
        it.setCommentIdentifier("#");
        it.setSkipComments(true);


       
        //we expect first line to be a header
        String first = it.next();
        String[] header = first.split(separator);

        int j=0;
        for (int i = 0; i < header.length; i++) {
            if (i != classIndex && !skippedIndexes.contains(i)) {
                out.setAttribute(j++, out.attributeBuilder().create(header[i], "NUMERICAL"));
            }
        }

        for (String line : it) {
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

        }
        return true;
    }
}
