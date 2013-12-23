package org.clueminer.io;

import be.abeel.io.LineIterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.utils.DatasetLoader;

/**
 *
 * @author Tomas Barton
 */
public class CsvLoader implements DatasetLoader {

    private boolean hasHeader = true;
    private boolean skipHeader = false;
    private String separator = ",";
    private int classIndex = -1;
    private ArrayList<Integer> skipIndex = new ArrayList<Integer>();
    private ArrayList<Integer> nameAttr = new ArrayList<Integer>();
    private Dataset<Instance> dataset;

    @Override
    public boolean load(File file, Dataset output) throws FileNotFoundException {
        setDataset(output);
        return load(file);
    }

    private void checkDataset() {
        if (dataset == null) {
            throw new RuntimeException("dataset is null");
        }
    }

    /**
     *
     * @param file input CSV file
     * @return
     */
    public boolean load(File file) {
        LineIterator it = new LineIterator(file);
        Instance inst;
        StringBuilder name = new StringBuilder();
        it.setSkipBlanks(true);
        it.setCommentIdentifier("#");
        it.setSkipComments(true);
        checkDataset();
        InstanceBuilder builder = dataset.builder();

        if (hasHeader && !skipHeader) {
            parseHeader(it);
        } else if (skipHeader) {
            it.next(); // just skip it
        }

        int skip;
        int skipSize;
        double[] values;
        String[] arr;
        int num = 0;
        for (String line : it) {
            arr = line.split(separator);
            if (num == 0 && dataset.attributeCount() == 0) {
                //detect types from first line
                createAttributes(arr, false);
            }
            skipSize = skipIndex.size();
            skip = 0;
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
                    if (!skipIndex.contains(i)) {
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
                if (!nameAttr.isEmpty() && nameAttr.contains(i)) {
                    name.append(arr[i]).append(" ");
                }
            }
            inst = builder.create(values, classValue);
            inst.setName(name.toString().trim());
            dataset.add(inst);
            num++;
        }
        return true;
    }

    private void parseHeader(LineIterator it) {
        //we expect first line to be a hasHeader
        String first = it.next();
        String[] header = first.split(separator);
        createAttributes(header, false);
    }

    private void createAttributes(String[] line, boolean detectTypes) {
        int j = 0;
        for (int i = 0; i < line.length; i++) {
            if (i != classIndex && !skipIndex.contains(i)) {
                if (!detectTypes) {
                    dataset.setAttribute(j++, dataset.attributeBuilder().create(line[i], "NUMERICAL"));
                } else {
                    //TODO: try to parse double from string
                    throw new UnsupportedOperationException("not implemented yet");
                }
            }
        }
    }

    public boolean hasHeader() {
        return hasHeader;
    }

    public void setHeader(boolean header) {
        this.hasHeader = header;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public int getClassIndex() {
        return classIndex;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }

    public ArrayList<Integer> getSkipIndex() {
        return skipIndex;
    }

    public void setSkipIndex(ArrayList<Integer> skipIndex) {
        this.skipIndex = skipIndex;
    }

    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    public void setDataset(Dataset<Instance> dataset) {
        this.dataset = dataset;
    }

    /**
     * Skip loading column on given index
     *
     * @param i which index to skip
     */
    public void skip(int i) {
        skipIndex.add(i);
    }

    public boolean isSkipHeader() {
        return skipHeader;
    }

    public void setSkipHeader(boolean skipHeader) {
        this.skipHeader = skipHeader;
    }

    /**
     * Value from column(s) will used as the name
     *
     * @param column column
     */
    public void addNameAttr(int column) {
        nameAttr.add(column);
    }

    public ArrayList<Integer> getNameAttr() {
        return nameAttr;
    }

    public void setNameAttr(ArrayList<Integer> nameAttr) {
        this.nameAttr = nameAttr;
    }

}
