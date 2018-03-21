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
package org.clueminer.importer.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.AttributeRole;
import org.clueminer.importer.Issue;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.io.importer.api.Report;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.spi.FileImporter;
import org.clueminer.types.FileType;
import org.clueminer.utils.progress.Progress;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Import CSV into data structures supported by the framework.
 *
 * @author Tomas Barton
 * @param <E> data row type
 */
@ServiceProvider(service = FileImporter.class)
public class CsvImporter<E extends InstanceDraft> extends AbstractLineImporter<E> implements FileImporter<E>, LongTask {

    private boolean hasHeader = true;
    private boolean skipHeader = false;
    private static final String NAME = "CSV";
    /**
     * header is typically on first line, unless we have some comments before
     * header - true when header was parser
     */
    private boolean parsedHeader = false;
    private int prevColCnt = -1;
    private static final Logger LOG = LoggerFactory.getLogger(CsvImporter.class);
    private final Pattern patternType = Pattern.compile("(double|float|int|integer|long|string)", Pattern.CASE_INSENSITIVE);

    public CsvImporter() {
        separator = ',';
    }

    @Override
    public String getName() {
        return NAME;
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        if (this.separator != separator) {
            this.separator = separator;
            //might change number of detected attributes, it's safer to remove
            //all of them
            if (container != null) {
                container.resetAttributes();
            }
        }
    }

    @Override
    public boolean isAccepting(Collection mimeTypes) {
        String mime = mimeTypes.toString();
        //this will match pretty much anything
        return mime.contains("text") || mime.contains("octet-stream");
    }

    @Override
    public boolean execute(Container<E> container, LineNumberReader lineReader) throws IOException {
        this.container = container;
        if (container.getFile() != null) {
            LOG.info("importing file {}", container.getFile().getName());
        }
        container.reset(); //remove all previous instances
        container.setDataset(null);
        container.setNumberOfLines(0);
        this.report = new Report();
        parsedHeader = false;
        LOG.info("has header = {}", hasHeader);
        LOG.info("number of attributes = {}", container.getAttributeCount());

        Container<E> c = container;
        for (AttributeDraft attr : c.getAttrIter()) {
            LOG.info("attr: {} type: {}, role: {}", attr.getName(), attr.getJavaType(), attr.getRole());
        }

        importData(lineReader);
        container.closeLoader();
        fireAnalysisFinished();

        return !cancel;
    }

    @Override
    public FileType[] getFileTypes() {
        FileType ft = new FileType(".csv", NbBundle.getMessage(getClass(), "fileType_CSV_Name"));
        FileType ft2 = new FileType(".data", NbBundle.getMessage(getClass(), "fileType_data_Name"));
        FileType ft3 = new FileType(".txt", NbBundle.getMessage(getClass(), "fileType_TXT_Name"));
        return new FileType[]{ft, ft2, ft3};
    }

    @Override
    public boolean isMatchingImporter(FileObject fileObject) {
        String ext = fileObject.getExt();
        return ext.equalsIgnoreCase("csv") || ext.equalsIgnoreCase("txt") || ext.equalsIgnoreCase("data");
    }

    protected void importData(LineNumberReader reader) throws IOException {
        //if it's not the first time we are trying to load the file,
        //number of lines will be known
        int numLines = container.getNumberOfLines();
        if (numLines > 0) {
            //if we know number of lines
            Progress.switchToDeterminate(progressTicket, numLines);
        } else {
            Progress.start(progressTicket);
        }

        /* it.setSkipBlanks(true);
         * it.setCommentIdentifier("#");
         * it.setSkipComments(true); */
        int count;
        int prev = -1;
        boolean reading = true;

        LOG.info("reader ready? {}", reader.ready());
        while (reader.ready() && reading) {
            String line = reader.readLine();
            count = reader.getLineNumber();
            //logger.log(Level.INFO, "line {0}: {1}", new Object[]{count, line});
            if (line != null && !line.isEmpty()) {
                lineRead(count, line);
            }
            //we should have read a next line, but we didn't
            if (count == prev) {
                reading = false;
                LOG.info("exitting reading input because no data has been read. "
                        + "Got to line #{}: {}", count, line);
            }
            prev = count;
        }
        container.setNumberOfLines(prev);
        //close the input
        reader.close();
        Progress.finish(progressTicket);
    }

    protected void lineRead(int num, String line) throws IOException {
        String[] columns = parseLine(line);
        if (prevColCnt != columns.length && prevColCnt > -1) {
            report.logIssue(new Issue(NbBundle.getMessage(CsvImporter.class, "CsvImporter_error_differentLineLength", num), Issue.Level.WARNING));
        } else if (prevColCnt != columns.length) {
            prevColCnt = columns.length;
        }
        //Dump.array(columns, "line " + num + " (" + columns.length + ")");
        if (hasHeader && !skipHeader && !parsedHeader) {
            LOG.info("header: {}", line);
            parseHeader(columns);
            parsedHeader = true;
        } else if (skipHeader) {
            LOG.info("skipping: {}", line);
            // just skip it
        } else {
            /**
             * Second line sometimes contains extra attributes specification,
             * like type, role etc.
             */

            if (container.getAttributeCount() != columns.length) {
                LOG.info("expected: {} but got {}", container.getAttributeCount(), columns.length);
                container.resetAttributes();
            }

            //LineNumberReader counts from 1, so this is 2nd line
            if (num == 2) {
                int i = 0;
                boolean matched = true;
                for (String column : columns) {
                    matched &= parseType(column, i++);
                    //logger.log(Level.WARNING, "col: {0} matched: {1}", new Object[]{column, matched});
                }
                //if all columns contain resonable value, we skip the line
                if (matched) {
                    return;
                }
            }
            container.createInstance(num, columns);
        }
    }

    /**
     * Detects line containing information about type of an attribute
     *
     * @param column
     * @param attrIndex
     * @return
     */
    protected boolean parseType(String column, int attrIndex) {
        AttributeDraft attr;
        final Matcher matcher = patternType.matcher(column);
        if (matcher.find()) {
            String type = matcher.group(1).toLowerCase();

            Class<?> res;
            switch (type) {
                case "double":
                    res = Double.class;
                    break;
                case "float":
                    res = Float.class;
                    break;
                case "int":
                case "integer":
                    res = Integer.class;
                    break;
                case "long":
                    res = Long.class;
                    break;
                default:
                    res = String.class;
                    break;
            }
            //attr = getAttribute(attrIndex);
            attr = container.getAttribute(attrIndex);
            // TODO: type has value "java.lang.Double" but we're passing "double"
            if (attr != null && !attr.getJavaType().equals(res)) {
                LOG.info("type changed {} from {} to {}", container.getAttribute(attrIndex).getName(), attr.getJavaType(), type);
                report.logIssue(new Issue(container.getAttribute(attrIndex).getName() + "type changed from " + type + " to " + attr.getJavaType(), Issue.Level.INFO));
                attr.setJavaType(res);
                fireAttributeChanged(container.getAttribute(attrIndex), "type");
            }
            return true;
        }
        attr = container.getAttribute(attrIndex);
        if (attr != null) {
            AttributeRole role = guessAttrType(attr.getName(), attr);
            LOG.info("column ''{}'' doesn't look like a type information. guessing type to: {}", column, role);
        }
        return false;
    }

    private void parseHeader(String[] columns) {
        int i = 0;
        String lower;
        AttributeDraft attrd;
        for (String attrName : columns) {
            if (container.hasAttributeAtIndex(i)) {
                attrd = container.getAttribute(i);
                if (!container.getAttribute(i).getName().equals(attrName) && container.hasAttribute(attrName)) {
                    //this should be unique. TODO: really?
                    LOG.info("creating attr: {}", attrName);
                    attrd = container.createAttribute(i, attrName + "_" + i);
                } else {
                    //get or update attribute name
                    attrd = container.getAttribute(i);
                }
            } else {
                //create new attribute
                if (container.hasAttribute(attrName)) {
                    //duplicate attribute name
                    attrd = container.createAttribute(i, attrName + "_" + i);
                } else {
                    attrd = container.createAttribute(i, attrName);
                }
                LOG.info("created missing attr {}: {}", i, attrName);
            }

            lower = attrName.toLowerCase();
            guessAttrType(lower, attrd);
            i++;
        }
    }

    /**
     * Sort of "smart" guesses based on attribute's name. Used when deterministic
     * approaches fails.
     *
     * @param name
     * @param attrd
     */
    private AttributeRole guessAttrType(String name, AttributeDraft attrd) {
        if (name.startsWith("meta_") || name.startsWith("name")) {
            attrd.setRole(BasicAttrRole.META);
            attrd.setType(BasicAttrType.STRING);
            LOG.info("meta attr {0}", attrd.getIndex());
        } else if (name.startsWith("id")) {
            attrd.setRole(BasicAttrRole.ID);
        } else if (name.startsWith("!")) {
            attrd.setRole(BasicAttrRole.CLASS);
        }
        return attrd.getRole();
    }

    /**
     * @return true if something was left over from last call(s)
     */
    public boolean isPending() {
        return pending != null;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public boolean isSkipHeader() {
        return skipHeader;
    }

    public void setSkipHeader(boolean skipHeader) {
        this.skipHeader = skipHeader;
    }

    public char getQuotechar() {
        return quotechar;
    }

    public void setQuotechar(char quotechar) {
        this.quotechar = quotechar;
    }

    /**
     * Conversion to FileObject might fail, so we have a backup BufferedReader
     *
     * @param file
     * @return
     * @throws IOException
     */
    private LineNumberReader getReader(File file) throws IOException {
        FileObject fileObject = FileUtil.toFileObject(file);
        if (fileObject == null) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            return new LineNumberReader(br);
        }
        return ImportUtils.getTextReader(fileObject);
    }

    /**
     * List of strings which are considered as missing values
     *
     * @return
     */
    public List<String> getMissing() {
        return missing;
    }

    public void setMissing(List<String> missing) {
        this.missing = missing;
    }

    public void setContainer(Container cont) {
        this.container = cont;
    }

}
