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

import org.clueminer.io.arff.ARFFHandler;
import java.io.File;
import java.util.LinkedList;
import java.util.regex.Matcher;
import org.clueminer.exception.ParserError;
import org.clueminer.utils.DataFileInfo;
import org.clueminer.utils.DatasetSniffer;
import static org.clueminer.io.arff.ARFFHandler.RELATION;

/**
 * A set of heuristics to detect most common file types, right now it's not very
 * precise and can't be relied on.
 *
 * @author deric
 */
public class DataSniffer implements DatasetSniffer {

    /**
     * So far we test file types just based on extensions, this could be
     * extended by MIME analysis, though it is also not very reliable
     *
     * @param file
     * @return
     */
    @Override
    public DataFileInfo scan(File file) throws ParserError {
        String name = file.getName();
        int pos = name.indexOf(".");
        DataFileInfo df = new DataFileInfo();
        String ext = "";
        if (pos > 0) {
            ext = name.substring(pos + 1).toLowerCase();
            df.name = name.substring(0, pos);
        } else {
            df.name = name;
        }
        switch (ext) {
            default:
                return parseArff(file, df);
        }
    }

    private DataFileInfo parseArff(File file, DataFileInfo df) throws ParserError {
        LineIterator it = new LineIterator(file);
        it.setSkipBlanks(true);
        it.setCommentIdentifier("%");
        it.setSkipComments(true);

        ARFFHandler handler = new ARFFHandler();
        Matcher rmatch;
        boolean relationFound = false;

        int numAttr = 0;
        int i = 0;
        for (String line : it) {
            if ((rmatch = RELATION.matcher(line)).matches()) {
                df.name = rmatch.group(1);
                relationFound = true;
            } else if (handler.isValidAttributeDefinition(line)) {
                AttrHolder ah;

                ah = handler.parseAttribute(line);
                String attrName = ah.getName().toLowerCase();
                switch (attrName) {
                    case "class":
                    case "type":
                        df.classIndex = numAttr;
                        break;
                    default:
                        df.numAttributes++;
                }

            } else if (line.equalsIgnoreCase("@data")) {
                df.separator = ",";
                return df;
            }
            //if no ARFF relation found within first few lines, try parsing as CSV
            if (!relationFound && i > 15) {
                return parseCsv(file, df);
            }
            i++;
        }
        return df;
    }

    private DataFileInfo parseCsv(File file, DataFileInfo df) {
        String first = null;
        LineIterator it = new LineIterator(file);
        it.setSkipBlanks(true);

        LinkedList<String> possibleSeparators = new LinkedList<>();
        possibleSeparators.add(",");
        possibleSeparators.add(";");
        possibleSeparators.add("\t");

        int i = 0;
        String sep;
        for (String line : it) {
            if (i == 0) {
                first = line;
            }
            if (possibleSeparators.size() == 1) {
                return detectCols(line, possibleSeparators.get(0), df);
            }
            for (int j = 0; j < possibleSeparators.size(); j++) {
                sep = possibleSeparators.get(j);
                if (!line.contains(sep)) {
                    possibleSeparators.remove(j);
                    j--;
                }
            }

            if (i > 10) {
                return detectCols(first, possibleSeparators.get(0), df);
            }
            i++;
        }

        return df;
    }

    private DataFileInfo detectCols(String line, String separator, DataFileInfo df) {
        String[] cols = line.split(separator);
        int i = 0;
        for (String str : cols) {
            try {
                Double.parseDouble(str);
                df.numAttributes++;
            } catch (NumberFormatException e) {
                //we suppose that class information will be String (just a guess)
                df.classIndex = i;
            }
            i++;
        }
        df.separator = separator;
        df.type = "csv";

        return df;
    }

}
