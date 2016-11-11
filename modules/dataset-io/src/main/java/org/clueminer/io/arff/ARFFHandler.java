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
package org.clueminer.io.arff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.exception.ParserError;
import org.clueminer.io.AttrHolder;
import org.clueminer.io.LineIterator;
import org.clueminer.utils.DatasetLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides method to load data from ARFF formatted files.
 *
 * For a detailed description on the ARFF format, please see
 * http://weka.wikispaces.com/ARFF+(stable+version)
 *
 * @author Thomas Abeel
 * @author Tomas Barton
 * @param <E>
 *
 */
public class ARFFHandler<E extends Instance> implements DatasetLoader<E> {

    protected Matcher match;
    /**
     * matches eg. "
     *
     * @RELATION iris"
     */
    public static final Pattern RELATION = Pattern.compile("^@relation\\s+(.*)", Pattern.CASE_INSENSITIVE);
    public static final Pattern ATTR_TYPES = Pattern.compile("\\{(\\d+,)+(\\d+)\\}", Pattern.CASE_INSENSITIVE);
    //allow all characters except whitespace
    public static final Pattern SINGLE_WORD = Pattern.compile("(\\S+)(.*)", Pattern.CASE_INSENSITIVE);

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
    public static final Pattern attrDef = Pattern.compile("^@attribute(.*)", Pattern.CASE_INSENSITIVE);

    private static final Logger LOG = LoggerFactory.getLogger(ARFFHandler.class);

    /**
     * Most common values of separators in data (comma is the default)
     */
    private static final String[] SEPARATORS = new String[]{"\t", "\\s+", ","};

    private boolean skipColumnsWithNaNs = true;

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
    public boolean load(File file, Dataset dataset) throws FileNotFoundException, ParserError {
        return load(new LineIterator(file), dataset, -1, ",", new ArrayList<Integer>());
    }

    /**
     * Load a data set from an ARFF formatted file. Due to limitations in the
     * Java-ML design only numeric attributes can be read.
     *
     * @param file       - the file to read the data from
     * @param dataset
     * @param classIndex - the index of the class label
     * @return the data set represented in the provided file
     * @throws FileNotFoundException               - if the file can not be found
     * @throws org.clueminer.exception.ParserError
     */
    public boolean load(File file, Dataset dataset, int classIndex) throws FileNotFoundException, ParserError {
        return load(new LineIterator(file), dataset, classIndex, ",", new ArrayList<Integer>());
    }

    @Override
    public boolean load(Reader reader, Dataset output) throws FileNotFoundException, ParserError {
        return load(new LineIterator(reader), output, -1, ",", new ArrayList<Integer>());
    }

    public boolean load(File file, Dataset out, int classIndex, String separator, ArrayList<Integer> skippedIndexes) throws ParserError {
        return load(new LineIterator(file), out, classIndex, separator, skippedIndexes);
    }

    /**
     *
     * @param it             line iterator
     * @param out
     * @param classIndex     - indexed from zero!
     * @param separator      - for eliminating all white characters (" ",\n, \t)
     *                       use "\\s+"
     * @param skippedIndexes - indexes of columns that won't be imported
     * @return
     * @throws org.clueminer.exception.ParserError
     * @throws IllegalArgumentException            when type is not convertible to Enum
     *                                             IAttributeType
     */
    public boolean load(LineIterator it, Dataset out, int classIndex, String separator, ArrayList<Integer> skippedIndexes) throws ParserError {
        it.setSkipBlanks(true);
        it.setCommentIdentifier("%");
        it.setSkipComments(true);

        Matcher rmatch;

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
                //magic for separator detection
                if (arr.length == 1) {
                    LOG.info("failed to split line (using separator ''{}''): {}", separator, line);
                    for (String s : SEPARATORS) {
                        arr = line.split(s);
                        if (arr.length > 1) {
                            LOG.info("setting separator to ''{}''", s);
                            separator = s;
                            break;
                        }
                    }
                    if (arr.length == 1) {
                        throw new RuntimeException("failed to find multiple columns in data row");
                    }
                }
                double[] values;
                int skipSize = skippedIndexes.size();
                int skip = 0;
                if (classIndex >= 0) {
                    skipSize++; //smaller array is enough
                }
                values = new double[arr.length - skipSize];

                String classValue = null;
                int idx;
                for (int i = 0; i < arr.length; i++) {
                    if (i == classIndex) {
                        classValue = arr[i];
                        skip++;
                    } else {
                        double val;
                        idx = i - skip;
                        if (!skippedIndexes.contains(i)) {
                            try {
                                val = Double.parseDouble(arr[i]);
                                setValue(values, idx, val, line);
                            } catch (NumberFormatException e) {
                                if (skipColumnsWithNaNs) {
                                    LOG.info("skipping column {} because can't be parsed as number {}: {}", i, arr[i], e.getMessage());
                                    skippedIndexes.add(i);
                                    skipSize++;
                                    skip++;
                                    double[] tmp = new double[arr.length - skipSize];
                                    System.arraycopy(values, 0, tmp, 0, tmp.length);
                                    values = tmp;
                                    if (out.attributeCount() >= i) {
                                        out.removeAttribute(i);
                                    }
                                } else {
                                    val = Double.NaN;
                                    setValue(values, idx, val, line);
                                }
                            }
                        } else {
                            skip++;
                        }
                    }
                }
                out.builder().create(values, classValue);
            } else if ((rmatch = RELATION.matcher(line)).matches()) {
                //replace single quotes, if contains any
                out.setName(rmatch.group(1).replaceAll("\'", ""));
            } else if (isValidAttributeDefinition(line)) {
                if (headerLine != classIndex && !skippedIndexes.contains(headerLine)) {
                    AttrHolder ah;

                    //System.out.println(headerLine + ": " + line + " attr num=" + numAttr);
                    ah = parseAttribute(line);
                    String attrName = ah.getName().toLowerCase();
                    switch (attrName) {
                        case "class":
                        case "type":
                            classIndex = numAttr;
                            break;
                        default:
                            //TODO: use range and set of valid value for further parsing
                            out.attributeBuilder().create(ah.getName(), ah.getType());
                    }
                    numAttr++;
                }
                headerLine++;

            } else if (line.equalsIgnoreCase("@data")) {
                dataMode = true;
            }
        }
        return true;
    }

    private void setValue(double[] values, int idx, double value, String line) {
        if (idx < 0 || idx >= values.length) {
            throw new RuntimeException("expected " + values.length + " columns, on line: '" + line + "', got " + idx);
        }
        values[idx] = value;
    }

    protected String convertType(String type) {
        if ((match = ATTR_TYPES.matcher(type)).matches()) {
            return "REAL";
        }
        return type;
    }

    public AttrHolder parseAttribute(String line) throws ParserError {
        return attrParse(line);
    }

    public boolean isValidAttributeDefinition(String line) {
        if (!attrDef.matcher(line).matches()) {
            return false;
        }
        try {
            attrParse(line);
        } catch (ParserError e) {
            LOG.warn(e.getMessage());
            return false;
        }
        return true;
    }

    protected AttrHolder attrParse(String line) throws ParserError {
        AttrHolder attr;
        if (attrDef.matcher(line).matches()) {
            //remove @attribute
            line = line.substring(10, line.length());
            attr = new AttrHolder();
            attrDef(line, attr);
        } else {
            throw new ParserError("attribute definition must start with '@attribute'");
        }
        return attr;
    }

    /**
     * Removes string subpart 'meal' from a 'food' string
     *
     * @param food    the whole thing
     * @param starter a starting meal
     * @return
     * @throws ParserError
     */
    protected String consume(String food, String starter) throws ParserError {
        if (!food.startsWith(starter)) {
            throw new ParserError("expected '" + starter + "' but got '" + food.substring(starter.length(), food.length()) + "'");
        }
        return food.substring(starter.length(), food.length());
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
            Matcher m;
            String name;
            if ((m = SINGLE_WORD.matcher(line)).matches()) {
                name = m.group(1);
                attr.setName(name);
                line = consume(line, name);
            } else {
                throw new ParserError("type name error, got '" + line + "'");
            }
            line = removeWhitespace(line);
        }
        line = removeWhitespace(line);
        line = attrType(line, attr);
        line = attrAllowed(line, attr);
        line = attrRange(line, attr);
        if (!line.isEmpty()) {
            throw new ParserError("expected empty string, but got '" + line + "'");
        }
    }

    /**
     * Reads attribute name from quotes, e.g.: 'MAX.LENGTH ASPECT RATIO' Any
     * characters are allowed inside quotes, also whitespace.
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

    private String attrType(String line, AttrHolder attr) throws ParserError {
        if (line.matches("^[A-Za-z](.*)")) {
            Matcher m;
            String type;
            if ((m = SINGLE_WORD.matcher(line)).matches()) {
                type = m.group(1);
                attr.setType(type);
                line = consume(line, type);
            } else {
                throw new ParserError("type name error, got '" + line + "'");
            }
            line = removeWhitespace(line);
        }
        return line;
    }
}
