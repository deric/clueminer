package org.clueminer.io;

import be.abeel.io.LineIterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.clueminer.dataset.api.Dataset;
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

    private Matcher match;
    /**
     * matches eg. "
     *
     * @RELATION iris"
     */
    public static final Pattern relation = Pattern.compile("^@relation\\s+(\\w*)", Pattern.CASE_INSENSITIVE);
    public static final Pattern attrTypes = Pattern.compile("\\{(\\d+,)+(\\d+)\\}", Pattern.CASE_INSENSITIVE);

    /**
     * matches attribute definition which might simply contain attribute name
     * and its type, starting with "@attribute" annotation:
     *
     * "@attribute attr_name numeric"
     *
     * more complex definitions contain a set of allowed values (in curly
     * brackets {a, b, c}):
     *
     * "@attribute class {cp,im,pp,imU,om,omL,imL,imS}"
     *
     * or ranges in square brackets
     *
     * "@attribute Mitoses integer [1,10]"
     *
     */
    //normal regexp: ^@attribute\s+['\"]?([\w._\\/-]*)['\"]?\s+([\w]+)(\{[\w+|'[-\w+ ]',]+\}){0,1}(\s\[[\w\s,]+\]){0,1}
    public static final Pattern attribute = Pattern.compile(
            "^@attribute\\s+['\\\"]?([\\w._\\\\/-]*)['\\\"]?\\s+([\\w]+)?(\\{[\\w+|'[-\\w+ ]',]+\\}){0,1}(\\s\\[[\\w\\s,]+\\])?",
            Pattern.CASE_INSENSITIVE);

    /**
     * Load a data set from an ARFF formatted file. Due to limitations in the
     * Java-ML design only numeric attributes can be read. This method does not
     * read class labels.
     *
     * @param file    the file to read the data from
     * @param dataset
     *
     * @return the data set represented in the provided file
     * @throws FileNotFoundException if the file can not be found.
     */
    @Override
    public boolean load(File file, Dataset dataset) throws FileNotFoundException {
        return load(file, dataset, -1, ",", new ArrayList<Integer>());
    }

    /**
     * Load a data set from an ARFF formatted file. Due to limitations in the
     * Java-ML design only numeric attributes can be read.
     *
     * @param file       - the file to read the data from
     * @param dataset
     * @param classIndex - the index of the class label
     * @return the data set represented in the provided file
     * @throws FileNotFoundException - if the file can not be found
     */
    public boolean load(File file, Dataset dataset, int classIndex) throws FileNotFoundException {
        return load(file, dataset, classIndex, ",", new ArrayList<Integer>());
    }

    /**
     *
     * @param file
     * @param out
     * @param classIndex     - indexed from zero!
     * @param separator      - for eliminating all white characters (" ",\n, \t)
     *                       use
     *                       "\\s+"
     * @param skippedIndexes - indexes of columns that won't be imported
     * @return
     * @throws IllegalArgumentException when type is not convertible to Enum
     *                                  IAttributeType
     */
    public boolean load(File file, Dataset out, int classIndex, String separator, ArrayList<Integer> skippedIndexes) {
        LineIterator it = new LineIterator(file);
        it.setSkipBlanks(true);
        it.setCommentIdentifier("%");
        it.setSkipComments(true);

        Matcher rmatch;
        Matcher amatch;

        int headerLine = 0;
        int numAttr = 0;
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
                out.builder().create(values, classValue);
            } else {
                if ((rmatch = relation.matcher(line)).matches()) {
                    out.setName(rmatch.group(1));
                } else if ((amatch = attribute.matcher(line)).matches()) {
                    //System.out.println(line);
                    if (headerLine != classIndex && !skippedIndexes.contains(headerLine)) {
                        //tries to convert string to enum, at top level we should catch the
                        //exception
                        System.out.println(headerLine + ": " + line + " attr num=" + numAttr);
                        String attrName = amatch.group(1).toLowerCase().trim();
                        switch (attrName) {
                            case "class":
                            case "type":
                                classIndex = numAttr;
                                break;
                            default:
                                out.attributeBuilder().create(attrName, convertType(amatch.group(2).toUpperCase()));
                        }
                        numAttr++;
                    }
                    headerLine++;

                } else if (line.equalsIgnoreCase("@data")) {
                    dataMode = true;
                }
            }
        }
        return true;
    }

    protected String convertType(String type) {
        if ((match = attrTypes.matcher(type)).matches()) {
            return "REAL";
        }
        return type;
    }

    public AttrHolder parseAttribute(String line) throws ParserError {
        return attrParse(line);
    }

    protected boolean isValidAttributeDefinition(String line) {
        // return attribute.matcher(line).matches();
        try {
            AttrHolder h = attrParse(line);
            System.out.println("attr = " + h.toString());
        } catch (ParserError e) {
            return false;
        }
        return true;
    }

    private AttrHolder attrParse(String line) throws ParserError {
        if (line.startsWith("@attribute ")) {
            line = consume(line, "@attribute ");
            AttrHolder attr = new AttrHolder();
            attrDef(line, attr);
            return attr;
        }
        if (line.startsWith("@ATTRIBUTE ")) {
            line = consume(line, "@ATTRIBUTE ");
            AttrHolder attr = new AttrHolder();
            attrDef(line, attr);
            return attr;
        }

        throw new ParserError("attribute definition must start with '@attribute'");
    }

    protected String consume(String food, String meal) throws ParserError {
        if (!food.startsWith(meal)) {
            throw new ParserError("expected '" + meal + "' but got '" + food.substring(meal.length(), food.length()) + "'");
        }
        return food.substring(meal.length(), food.length());
    }

    /**
     * Read attribute definition and store it into attr variable
     *
     * @param line
     * @param attr
     * @throws ParserError
     */
    private void attrDef(String line, AttrHolder attr) throws ParserError {
        //remove whitespace
        line = removeWhitespace(line);
        if (line.startsWith("'")) {
            line = attrName("'", line, attr);
        } else if (line.startsWith("\"")) {
            line = attrName("\"", line, attr);
        } else {
            //suppose next string is the attribute's name
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < line.length() && line.charAt(i) != ' ') {
                sb.append(line.charAt(i++));
            }
            line = line.substring(i, line.length());
            attr.setName(sb.toString());
        }
        line = line.trim();
        line = attrType(line, attr);
        line = attrAllowed(line, attr);
        line = attrRange(line, attr);
        if (!line.isEmpty()) {
            throw new ParserError("expected empty string, but got '" + line + "'");
        }
    }

    /**
     * Reads attribute name from quotes, e.g.: 'MAX.LENGTH ASPECT RATIO'
     * Any characters are allowed inside quotes, also whitespace.
     *
     * @param exp
     * @param line
     * @param attr
     * @return
     * @throws ParserError
     */
    private String attrName(String exp, String line, AttrHolder attr) throws ParserError {
        line = consume(line, exp);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        char needle = exp.charAt(0);
        while (i < line.length() && line.charAt(i) != needle) {
            sb.append(line.charAt(i++));
        }
        line = line.substring(i, line.length());
        attr.setName(sb.toString());
        return consume(line, exp);
    }

    private String attrAllowed(String line, AttrHolder attr) throws ParserError {
        if (line.startsWith("{")) {
            line = consume(line, "{");
            StringBuilder sb = new StringBuilder();
            int i = 0;
            char needle = '}';
            while (i < line.length() && line.charAt(i) != needle) {
                sb.append(line.charAt(i++));
            }
            line = line.substring(i, line.length());
            attr.setAllowed(sb.toString());
            return consume(line, "}");
        }
        return line;
    }

    private String attrRange(String line, AttrHolder attr) throws ParserError {
        if (line.startsWith("[")) {
            line = consume(line, "[");
            StringBuilder sb = new StringBuilder();
            int i = 0;
            char needle = ']';
            while (i < line.length() && line.charAt(i) != needle) {
                sb.append(line.charAt(i++));
            }
            line = line.substring(i, line.length());
            attr.setRange(sb.toString());
            return consume(line, "]");
        }
        return line;
    }

    /**
     * We can not use just trim, because we want to remove also tabs
     *
     * @param line
     * @return
     */
    private String removeWhitespace(String line) {
        return line.replaceAll("^\\s+", "");
    }

    private String attrType(String line, AttrHolder attr) {
        if (line.matches("^[A-Za-z]")) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < line.length() && line.charAt(i) != ' ') {
                sb.append(line.charAt(i++));
            }
            line = line.substring(i, line.length());
            attr.setType(sb.toString());
            line = line.trim();
        }
        return line;
    }
}
