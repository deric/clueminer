/*
 * Copyright (C) 2011-2015 clueminer.org
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

import be.abeel.io.LineIterator;
import java.io.File;
import java.util.regex.Matcher;
import static org.clueminer.io.ARFFHandler.relation;
import org.clueminer.utils.DataFileInfo;
import org.clueminer.utils.DatasetSniffer;
import org.openide.util.Exceptions;

/**
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
    public DataFileInfo scan(File file) {
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
            case "arff":
                return parseArff(file, df);
            default:
                return parseArff(file, df);
            //return parseCsv(file, df);
        }
    }

    private DataFileInfo parseArff(File file, DataFileInfo df) {
        LineIterator it = new LineIterator(file);
        it.setSkipBlanks(true);
        it.setCommentIdentifier("%");
        it.setSkipComments(true);

        ARFFHandler handler = new ARFFHandler();
        Matcher rmatch;

        int numAttr = 0;

        for (String line : it) {
            if ((rmatch = relation.matcher(line)).matches()) {
                df.name = rmatch.group(1);
            } else if (handler.isValidAttributeDefinition(line)) {
                AttrHolder ah;
                try {
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
                } catch (ParserError ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else if (line.equalsIgnoreCase("@data")) {
                return df;
            }
        }
        return df;
    }

    private DataFileInfo parseCsv(File file, DataFileInfo df) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
