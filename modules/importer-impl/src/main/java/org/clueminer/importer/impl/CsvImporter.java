package org.clueminer.importer.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.dataset.api.AttributeRole;
import org.clueminer.dataset.api.Instance;
import org.clueminer.importer.Issue;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.ContainerLoader;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.io.importer.api.Report;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.spi.FileImporter;
import org.clueminer.types.FileType;
import org.clueminer.utils.progress.Progress;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = FileImporter.class)
public class CsvImporter extends AbstractLineImporter implements FileImporter, LongTask {

    private boolean hasHeader = true;
    private boolean skipHeader = false;
    private static final String NAME = "CSV";
    /**
     * header is typically on first line, unless we have some comments before
     * header - true when header was parser
     */
    private boolean parsedHeader = false;
    private int prevColCnt = -1;
    private int numInstances;
    private static final Logger logger = Logger.getLogger(CsvImporter.class.getName());
    private ContainerLoader<Instance> loader;
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
            if (loader != null) {
                loader.resetAttributes();
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
    public boolean execute(Container container, LineNumberReader lineReader) throws IOException {
        this.container = container;
        if (container.getFile() != null) {
            logger.log(Level.INFO, "importing file {0}", container.getFile().getName());
        }
        this.loader = container.getLoader();
        loader.reset(); //remove all previous instances
        loader.setDataset(null);
        loader.setNumberOfLines(0);
        this.report = new Report();
        parsedHeader = false;
        logger.log(Level.INFO, "has header = {0}", hasHeader);
        logger.log(Level.INFO, "number of attributes = {0}", loader.getAttributeCount());

        for (AttributeDraft attr : loader.getAttrIter()) {
            logger.log(Level.INFO, "attr: {0} type: {1}, role: {2}", new Object[]{attr.getName(), attr.getType(), attr.getRole()});
        }

        importData(lineReader);
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
        numInstances = 0;
        //if it's not the first time we are trying to load the file,
        //number of lines will be known
        int numLines = loader.getNumberOfLines();
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

        logger.log(Level.INFO, "reader ready? {0}", reader.ready());
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
                logger.log(Level.WARNING, "exitting reading input because no data has been read. Got to line #{0}: {1}", new Object[]{count, line});
            }
            prev = count;
        }
        loader.setNumberOfLines(prev);
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
            logger.log(Level.INFO, "header: {0}", line);
            parseHeader(columns);
            parsedHeader = true;
        } else if (skipHeader) {
            logger.log(Level.INFO, "skipping: {0}", line);
            // just skip it
        } else {
            /**
             * Second line sometimes contains extra attributes specification,
             * like type, role etc.
             */

            if (loader.getAttributeCount() != columns.length) {
                logger.log(Level.INFO, "expected: {0} but got {1}", new Object[]{loader.getAttributeCount(), columns.length});
                loader.resetAttributes();
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
            addInstance(num, columns);
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

        final Matcher matcher = patternType.matcher(column);
        if (matcher.find()) {
            String type = matcher.group(1).toLowerCase();
            AttributeDraft attr;
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
            attr = getAttribute(attrIndex);
            // TODO: type has value "java.lang.Double" but we're passing "double"
            if (!attr.getType().equals(res)) {
                logger.log(Level.INFO, "type changed {0} from {2} to {1}", new Object[]{loader.getAttribute(attrIndex).getName(), type, attr.getType()});
                report.logIssue(new Issue(loader.getAttribute(attrIndex).getName() + "type changed from " + type + " to " + attr.getType(), Issue.Level.INFO));
                attr.setType(res);
                fireAttributeChanged(loader.getAttribute(attrIndex), "type");
            }
            return true;
        }
        logger.log(Level.INFO, "column ''{0}'' doesn't look like a type information", column);
        return false;
    }

    private void parseHeader(String[] columns) {
        int i = 0;
        String lower;
        AttributeDraft attrd;
        for (String attrName : columns) {
            if (loader.hasAttributeAtIndex(i)) {
                if (!loader.getAttribute(i).getName().equals(attrName) && loader.hasAttribute(attrName)) {
                    //this should be unique. TODO: really?
                    attrd = loader.createAttribute(i, attrName + "_" + i);
                } else {
                    //get or update attribute name
                    attrd = loader.createAttribute(i, attrName);
                }
            } else {
                //create new attribute
                if (loader.hasAttribute(attrName)) {
                    //duplicate attribute name
                    attrd = loader.createAttribute(i, attrName + "_" + i);
                } else {
                    attrd = loader.createAttribute(i, attrName);
                }
                logger.log(Level.INFO, "created missing attr {1}: {0}", new Object[]{attrName, i});
            }

            lower = attrName.toLowerCase();
            //sort of "smart" guesses based on attribute's name
            if (lower.startsWith("meta_")) {
                attrd.setRole(BasicAttrRole.META);
                logger.log(Level.INFO, "meta attr {0}", new Object[]{i});
            } else if (lower.startsWith("id")) {
                attrd.setRole(BasicAttrRole.ID);
            } else if (lower.startsWith("!")) {
                attrd.setRole(BasicAttrRole.CLASS);
            }
            i++;
        }
    }

    private AttributeDraft getAttribute(int i) {
        AttributeDraft attr;
        if (i < loader.getAttributeCount() && i > -1) {
            attr = loader.getAttribute(i);
        } else {
            logger.log(Level.INFO, "created dummy attr {0}", new Object[]{i});
            attr = loader.createAttribute(i, "attr_" + i);
            logger.log(Level.INFO, "attr name {0}, role = {1}", new Object[]{attr.getName(), attr.getRole().toString()});
        }

        return attr;
    }

    private void addInstance(int num, String[] columns) {
        InstanceDraft draft = new InstanceDraftImpl(loader, loader.getAttributeCount());
        int i = 0;
        AttributeRole role;
        AttributeDraft attr;
        for (String value : columns) {
            try {
                attr = getAttribute(i);
                role = attr.getRole();
                if (role == BasicAttrRole.ID) {
                    draft.setId(value);
                } else if (role == BasicAttrRole.INPUT) {
                    draft.setValue(i, parseValue(attr, value, i, num, draft));
                } else {
                    draft.setValue(i, value);
                }
            } catch (Exception e) {
                report.logIssue(new Issue("Invalid type (" + num + "): " + e.toString(), Issue.Level.WARNING));
                Exceptions.printStackTrace(e);
            }
            i++;
        }
        if (!loader.hasPrimaryKey()) {
            draft.setId(String.valueOf(numInstances));
        }
        loader.addInstance(draft, num);
        numInstances++;
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

    protected ContainerLoader getLoader() {
        return loader;
    }

    protected void setLoader(ContainerLoader loader) {
        this.loader = loader;
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

}
