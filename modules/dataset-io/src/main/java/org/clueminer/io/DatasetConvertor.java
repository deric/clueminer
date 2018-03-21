/*
 * Copyright (C) 2011-2018 clueminer.org
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

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class DatasetConvertor {

    /**
     *
     * @param dataset
     * @param header    - keyword to denote a start of header
     * @param body      - keyword to denote a start of data section
     * @param separator
     */
    public static String convert(Dataset dataset, String separator, String header, String body) {
        StringBuilder sb = new StringBuilder();
        if (header != null) {
            String name = dataset.getName();
            sb.append(header).append(" ");
            if (name == null) {
                name = "untitled";
            }
            sb.append(quote(name)).append("\n");
            sb.append("\n");
        }
        for (Object s : dataset.getClasses()) {
            sb.append("@attribute ").append(s.toString()).append("\t NUMERIC").append("\n");
        }
        sb.append("@attribute ").append("class\t{");
        for (Object s : dataset.getClasses()) {
            sb.append(s.toString()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1); //remove last comma
        sb.append("}\n");
        sb.append("\n").append(body).append("\n");
        Instance inst;
        for (int i = 0; i < dataset.size(); i++) {
            inst = (Instance) dataset.instance(i);
            sb.append(inst.toString(separator));
            Object clazz = inst.classValue();
            if (clazz != null) {
                sb.append(clazz);
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Returns the dataset as a string in ARFF format. Strings are quoted if
     * they contain whitespace characters, or if they are a question mark.
     */
    public static String toARFF(Dataset dataset) {
        return convert(dataset, ",", "@relation", "@data");
    }

    /**
     * Quotes a string if it contains special characters.
     *
     * The following rules are applied:
     *
     * A character is backquoted version of it is one of <tt>" ' % \ \n \r
     * \t</tt>.
     *
     * A string is enclosed within single quotes if a character has been
     * backquoted using the previous rule above or contains <tt>{ }</tt> or is
     * exactly equal to the strings <tt>, ? space or ""</tt> (empty string).
     *
     * A quoted question mark distinguishes it from the missing value which is
     * represented as an unquoted question mark in arff files.
     *
     * @param string the string to be quoted
     * @return the string (possibly quoted)
     * @see	#unquote(String)
     */
    public static String quote(String string) {
        boolean quote = false;

        // backquote the following characters
        if ((string.indexOf('\n') != -1) || (string.indexOf('\r') != -1)
                || (string.indexOf('\'') != -1) || (string.indexOf('"') != -1)
                || (string.indexOf('\\') != -1)
                || (string.indexOf('\t') != -1) || (string.indexOf('%') != -1)) {
            string = backQuoteChars(string);
            quote = true;
        }

        // Enclose the string in 's if the string contains a recently added
        // backquote or contains one of the following characters.
        if ((quote == true)
                || (string.indexOf('{') != -1) || (string.indexOf('}') != -1)
                || (string.indexOf(',') != -1) || (string.equals("?"))
                || (string.indexOf(' ') != -1) || (string.equals(""))) {
            string = ("'".concat(string)).concat("'");
        }

        return string;
    }

    /**
     * Converts carriage returns and new lines in a string into \r and \n.
     * Backquotes the following characters: ` " \ \t and %
     *
     * @param string the string
     * @return the converted string
     * @see	#unbackQuoteChars(String)
     */
    public static String backQuoteChars(String string) {

        int index;
        StringBuffer newStringBuffer;

        // replace each of the following characters with the backquoted version
        char charsFind[] = {'\\', '\'', '\t', '\n', '\r', '"', '%'};
        String charsReplace[] = {"\\\\", "\\'", "\\t", "\\n", "\\r", "\\\"", "\\%"};
        for (int i = 0; i < charsFind.length; i++) {
            if (string.indexOf(charsFind[i]) != -1) {
                newStringBuffer = new StringBuffer();
                while ((index = string.indexOf(charsFind[i])) != -1) {
                    if (index > 0) {
                        newStringBuffer.append(string.substring(0, index));
                    }
                    newStringBuffer.append(charsReplace[i]);
                    if ((index + 1) < string.length()) {
                        string = string.substring(index + 1);
                    } else {
                        string = "";
                    }
                }
                newStringBuffer.append(string);
                string = newStringBuffer.toString();
            }
        }

        return string;
    }
}
