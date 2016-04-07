package org.clueminer.io;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.utils.DatasetLoader;
import org.openide.util.Exceptions;

/**
 * Parses CSV into Dataset structure.
 *
 * @author Tomas Barton
 * @param <E>
 */
public class CsvLoader<E extends Instance> implements DatasetLoader<E> {

    private boolean hasHeader = true;
    private boolean skipHeader = false;
    private char separator = ',';
    private char quotechar = '"';
    private int classIndex = -1;
    private ArrayList<Integer> skipIndex = new ArrayList<>();
    private ArrayList<Integer> nameAttr = new ArrayList<>();
    private ArrayList<Integer> metaAttr = new ArrayList<>();
    private Dataset<E> dataset;
    private String nameJoinChar = " ";
    private String defaultDataType = "NUMERICAL";
    private static final Logger logger = Logger.getLogger(CsvLoader.class.getName());

    @Override
    public boolean load(File file, Dataset<E> output) throws FileNotFoundException {
        logger.log(Level.INFO, "loading file {0}", file.getName());
        setDataset(output);
        try {
            CSVReader reader = new CSVReader(new FileReader(file), separator, quotechar);
            return load(reader);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    private void checkDataset() {
        if (dataset == null) {
            throw new RuntimeException("dataset is null");
        }
    }

    @Override
    public boolean load(Reader reader, Dataset<E> output) throws FileNotFoundException {
        setDataset(output);
        try {
            CSVReader csvreader = new CSVReader(reader, separator, quotechar);
            return load(csvreader);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    public boolean load(File file) throws IOException {
        checkDataset();
        return load(new CSVReader(new FileReader(file)));
    }
    /**
     *
     * @param reader input file reader
     * @return
     * @throws java.io.IOException
     */
    public boolean load(CSVReader reader) throws IOException {
        Iterator<String[]> iter = reader.iterator();
        Instance inst;
        /* it.setSkipBlanks(true);
         * it.setCommentIdentifier("#");
         * it.setSkipComments(true); */
        checkDataset();
        InstanceBuilder builder = dataset.builder();

        if (hasHeader && !skipHeader) {
            parseHeader(iter.next());
            ///Dump.array(((CSVIterator) iter).showNext(), "next line: ");
        } else if (skipHeader) {
            iter.next(); // just skip it
        }

        int skip;
        int skipSize;
        int metaIndex;
        double[] values;
        double[] meta = null;
        String[] arr;
        StringBuilder name = null;
        int num = 0;
        int nameApp;
        int outSize;
        while (iter.hasNext()) {
            arr = iter.next();
            if (num == 0 && dataset.attributeCount() == 0) {
                //detect types from first line
                createAttributes(arr, false);
                logger.log(Level.WARNING, "automatic attribute detection. line size: {0}", arr.length);
            }
            if (nameAttr.size() > 0) {
                name = new StringBuilder();
            }
            skipSize = skipIndex.size();
            nameApp = 0;
            skip = 0;
            metaIndex = 0;
            if (classIndex >= 0) {
                skipSize++; //smaller array is enough
            }
            outSize = arr.length - skipSize - metaAttr.size();
            if (metaAttr.size() > 0) {
                meta = new double[metaAttr.size()];
            }
            String classValue = null;
            if (outSize > 0) {
                values = new double[outSize];
                for (int i = 0; i < arr.length; i++) {
                    if (i == classIndex) {
                        classValue = arr[i];
                        skip++;
                    } else {
                        double val;
                        if (metaAttr.contains(i)) {
                            try {
                                val = Double.parseDouble(arr[i]);
                            } catch (NumberFormatException e) {
                                val = Double.NaN;
                            }
                            meta[metaIndex++] = val;
                            skip++;
                        } else if (skipIndex.contains(i)) {
                            skip++;
                        } else if (!arr[i].isEmpty()) {
                            try {
                                val = Double.parseDouble(arr[i]);
                            } catch (NumberFormatException e) {
                                logger.log(Level.WARNING, "Number format exception, line {0}, attr {1}: {2}", new Object[]{num, i, e.getMessage()});
                                val = Double.NaN;
                            }
                            values[i - skip] = val;
                        }
                    }
                    if (!nameAttr.isEmpty() && nameAttr.contains(i)) {
                        name.append(arr[i]);
                        nameApp++;
                        if (nameAttr.size() != nameApp) {
                            name.append(nameJoinChar);
                        }
                    }
                }
                inst = builder.create(values, classValue);
                if (!nameAttr.isEmpty()) {
                    inst.setName(name.toString().trim());
                }
                if (metaAttr.size() > 0) {
                    inst.setMetaNum(meta);
                }
                num++;
            }
        }
        return true;
    }

    private void parseHeader(String[] header) throws IOException {
        //we expect first line to be a hasHeader
        createAttributes(header, false);
    }

    private void createAttributes(String[] line, boolean detectTypes) {
        for (int i = 0; i < line.length; i++) {
            if (i != classIndex && !skipIndex.contains(i)) {
                if (!detectTypes) {
                    if (metaAttr.contains(i)) {
                        dataset.attributeBuilder().create(line[i], defaultDataType, "META");
                    } else {
                        dataset.attributeBuilder().create(line[i], defaultDataType);
                    }
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

    public void setHasHeader(boolean header) {
        this.hasHeader = header;
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
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

    public Dataset<E> getDataset() {
        return dataset;
    }

    public void setDataset(Dataset<E> dataset) {
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
     * Value from column(s) will used as the name. We use C array numbering,
     * first attribute is marked as 0
     *
     * @param column column
     */
    public void addNameAttr(int column) {
        nameAttr.add(column);
    }

    public void addMetaAttr(int column) {
        metaAttr.add(column);
    }

    public void setMetaAttr(ArrayList<Integer> metaAttr) {
        this.metaAttr = metaAttr;
    }

    public ArrayList<Integer> getNameAttr() {
        return nameAttr;
    }

    public void setNameAttr(ArrayList<Integer> nameAttr) {
        this.nameAttr = nameAttr;
    }

    public String getNameJoinChar() {
        return nameJoinChar;
    }

    /**
     * In case that name is constructed from few columns, nameJoinChar is
     * used for joining them into one string
     *
     * @param nameJoinChar
     */
    public void setNameJoinChar(String nameJoinChar) {
        this.nameJoinChar = nameJoinChar;
    }

    public String getDefaultDataType() {
        return defaultDataType;
    }

    public void setDefaultDataType(String defaultDataType) {
        this.defaultDataType = defaultDataType;
    }

    public static String[] firstLine(File file, String separator) {
        String[] result = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                String line = br.readLine();
                result = line.split(separator);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                br.close();
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return result;
    }

    public char getQuotechar() {
        return quotechar;
    }

    public void setQuotechar(char quotechar) {
        this.quotechar = quotechar;
    }

}
