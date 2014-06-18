package org.clueminer.importer.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.dataset.api.AttributeRole;
import org.clueminer.importer.Issue;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.ContainerLoader;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.io.importer.api.ParsingError;
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
public class CsvImporter extends AbstractImporter implements FileImporter, LongTask {

    private boolean hasHeader = true;
    private boolean skipHeader = false;
    private char separator = ',';
    private char quotechar = '"';
    private static final String name = "CSV";
    private boolean cancel = false;
    private static final int INITIAL_READ_SIZE = 128;
    private String pending;
    private boolean ignoreQuotations = false;
    private boolean strictQuotes = false;
    /**
     * header is typically on first line, unless we have some comments before
     * header - true when header was parser
     */
    private boolean parsedHeader = false;
    private char escape;
    private boolean inField = false;
    //white space in front of a quote in a field is ignored
    private boolean ignoreLeadingWhiteSpace = false;
    private int prevColCnt = -1;
    private int numInstances;
    private static final Logger logger = Logger.getLogger(CsvImporter.class.getName());
    private ContainerLoader loader;
    private final Pattern patternType = Pattern.compile("(double|float|int|integer|long|string)", Pattern.CASE_INSENSITIVE);
    private List<String> missing = new LinkedList<String>();

    public CsvImporter() {
        separator = ',';
    }

    @Override
    public String getName() {
        return name;
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
    public boolean execute(Container container, FileObject file) throws IOException {
        LineNumberReader lineReader = ImportUtils.getTextReader(file);
        container.setFile(file);
        return execute(container, lineReader);
    }

    @Override
    public boolean execute(Container container, Reader reader) throws IOException {
        LineNumberReader lineReader = ImportUtils.getTextReader(reader);
        return execute(container, lineReader);
    }

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

        for (AttributeDraft attr : loader.getAttributes()) {
            logger.log(Level.INFO, "attr: {0} type: {1}, role: {2}", new Object[]{attr.getName(), attr.getType(), attr.getRole()});
        }

        importData(lineReader);
        fireAnalysisFinished();

        return !cancel;
    }

    @Override
    public FileType[] getFileTypes() {
        FileType ft = new FileType(".csv", NbBundle.getMessage(getClass(), "fileType_CSV_Name"));
        return new FileType[]{ft};
    }

    @Override
    public boolean isMatchingImporter(FileObject fileObject) {
        return fileObject.getExt().equalsIgnoreCase("csv");
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
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

        /*it.setSkipBlanks(true);
         it.setCommentIdentifier("#");
         it.setSkipComments(true);*/
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
        } else {
            if (prevColCnt != columns.length) {
                prevColCnt = columns.length;
            }
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
            if (type.equals("double")) {
                res = Double.class;
            } else if (type.equals("float")) {
                res = Float.class;
            } else if (type.equals("int") || type.equals("integer")) {
                res = Integer.class;
            } else if (type.equals("long")) {
                res = Long.class;
            } else {
                res = String.class;
            }
            attr = getAttribute(attrIndex);
            // TODO: type has value "java.lang.Double" but we're passing "double"
            if (!attr.getType().equals(res)) {
                logger.log(Level.INFO, "type changed {0} from {2} to {1}", new Object[]{loader.getAttribute(attrIndex).getName(), type, attr.getType()});
                attr.setType(res);
                fireAttributeChanged(loader.getAttribute(attrIndex), "type");
            }
            return true;
        }
        logger.log(Level.WARNING, "column ''{0}'' doesn''t seem to contain reasonable type value", column);
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
     * Parse given input values as specified type
     *
     * @param attr
     * @param value
     * @param i
     * @param num
     * @param draft
     * @return
     */
    private Object parseValue(AttributeDraft attr, String value, int i, int num, InstanceDraft draft) {
        Object castedVal = null;

        //check for missing values
        if (missing.size() > 0) {
            for (String missingValue : missing) {
                if (missingValue.equals(value)) {
                    //TODO: should be returned by specific parser
                    return Double.NaN;
                }
            }
        }

        try {
            castedVal = attr.getParser().parse(value);
        } catch (ParsingError ex) {
            report.logIssue(new Issue(NbBundle.getMessage(CsvImporter.class,
                                                          "CsvImporter_invalidType", num, i + 1, ex.getMessage()), Issue.Level.WARNING));
        }
        return castedVal;
    }

    /**
     * @return true if something was left over from last call(s)
     */
    public boolean isPending() {
        return pending != null;
    }

    protected String[] parseLine(String line) throws IOException {
        List<String> tokensOnThisLine = new ArrayList<String>();
        StringBuilder sb = new StringBuilder(INITIAL_READ_SIZE);
        boolean inQuotes = false;
        if (pending != null) {
            sb.append(pending);
            pending = null;
            inQuotes = !this.ignoreQuotations;//true;
        }
        for (int i = 0; i < line.length(); i++) {

            char c = line.charAt(i);
            if (c == this.escape) {
                if (isNextCharacterEscapable(line, (inQuotes && !ignoreQuotations) || inField, i)) {
                    sb.append(line.charAt(i + 1));
                    i++;
                }
            } else if (c == quotechar) {
                if (isNextCharacterEscapedQuote(line, (inQuotes && !ignoreQuotations) || inField, i)) {
                    sb.append(line.charAt(i + 1));
                    i++;
                } else {
                    inQuotes = !inQuotes;
                    // the tricky case of an embedded quote in the middle: a,bc"d"ef,g
                    if (!strictQuotes) {
                        if (i > 2 //not on the beginning of the line
                                && line.charAt(i - 1) != this.separator //not at the beginning of an escape sequence
                                && line.length() > (i + 1)
                                && line.charAt(i + 1) != this.separator //not at the	end of an escape sequence
                                ) {

                            if (ignoreLeadingWhiteSpace && sb.length() > 0 && isAllWhiteSpace(sb)) {
                                sb = new StringBuilder(INITIAL_READ_SIZE);  //discard white space leading up to quote
                            } else {
                                sb.append(c);
                            }
                        }
                    }
                }
                inField = !inField;
            } else if (c == separator && !(inQuotes && !ignoreQuotations)) {
                tokensOnThisLine.add(sb.toString());
                sb = new StringBuilder(INITIAL_READ_SIZE); // start work on next token
                inField = false;
            } else {
                if (!strictQuotes || (inQuotes && !ignoreQuotations)) {
                    sb.append(c);
                    inField = true;
                }
            }
        }
        tokensOnThisLine.add(sb.toString());

        return tokensOnThisLine.toArray(new String[tokensOnThisLine.size()]);
    }

    /**
     * precondition: the current character is a quote or an escape
     *
     * @param nextLine the current line
     * @param inQuotes true if the current context is quoted
     * @param i        current index in line
     * @return true if the following character is a quote
     */
    private boolean isNextCharacterEscapedQuote(String nextLine, boolean inQuotes, int i) {
        return inQuotes // we are in quotes, therefore there can be escaped quotes in here.
                && nextLine.length() > (i + 1) // there is indeed another character to check.
                && nextLine.charAt(i + 1) == quotechar;
    }

    /**
     * precondition: the current character is an escape
     *
     * @param nextLine the current line
     * @param inQuotes true if the current context is quoted
     * @param i        current index in line
     * @return true if the following character is a quote
     */
    protected boolean isNextCharacterEscapable(String nextLine, boolean inQuotes, int i) {
        return inQuotes // we are in quotes, therefore there can be escaped quotes in here.
                && nextLine.length() > (i + 1) // there is indeed another character to check.
                && (nextLine.charAt(i + 1) == quotechar || nextLine.charAt(i + 1) == this.escape);
    }

    /**
     * precondition: sb.length() > 0
     *
     * @param sb A sequence of characters to examine
     * @return true if every character in the sequence is whitespace
     */
    protected boolean isAllWhiteSpace(CharSequence sb) {
        boolean result = true;
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);

            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return result;
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

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isIgnoreQuotations() {
        return ignoreQuotations;
    }

    public void setIgnoreQuotations(boolean ignoreQuotations) {
        this.ignoreQuotations = ignoreQuotations;
    }

    public boolean isStrictQuotes() {
        return strictQuotes;
    }

    public void setStrictQuotes(boolean strictQuotes) {
        this.strictQuotes = strictQuotes;
    }

    public char getEscape() {
        return escape;
    }

    public void setEscape(char escape) {
        this.escape = escape;
    }

    public boolean isIgnoreLeadingWhiteSpace() {
        return ignoreLeadingWhiteSpace;
    }

    public void setIgnoreLeadingWhiteSpace(boolean ignoreLeadingWhiteSpace) {
        this.ignoreLeadingWhiteSpace = ignoreLeadingWhiteSpace;
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
