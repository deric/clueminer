package org.clueminer.importer.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.importer.ImportController;
import org.clueminer.importer.Issue;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.io.importer.api.ParsingError;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.spi.FileImporter;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * Importer which reads file line by line.
 *
 * @author Tomas Barton
 */
public abstract class AbstractLineImporter extends BaseImporter implements FileImporter, LongTask {

    private static final Logger logger = Logger.getLogger(AbstractLineImporter.class.getName());
    protected static final int INITIAL_READ_SIZE = 128;
    protected String pending;
    protected boolean ignoreQuotations = false;
    protected boolean strictQuotes = false;
    protected char escape;
    protected boolean inField = false;
    //white space in front of a quote in a field is ignored
    protected boolean ignoreLeadingWhiteSpace = false;
    protected char separator = ',';
    protected char quotechar = '"';
    protected List<String> missing = new LinkedList<>();
    protected boolean replaceMissingValues = true;

    public AbstractLineImporter() {
        super();
    }

    @Override
    public void reload(final FileObject file, final Reader reader) {
        logger.info("reload called");
        LongTask task = null;
        if (this instanceof LongTask) {
            task = (LongTask) this;
        }
        final FileImporter importer = this;
        final ImportController controller = Lookup.getDefault().lookup(ImportController.class);
        String taskName = NbBundle.getMessage(AbstractLineImporter.class, "AbstractImporter.taskName");
        if (!executor.isRunning()) {
            executor.execute(task, new Runnable() {
                @Override
                public void run() {
                    try {
                        logger.log(Level.INFO, "reloading file");
                        controller.importFile(file, reader, importer, true);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }, taskName, errorHandler);
        } else {
            logger.log(Level.INFO, "executor is still running");
        }
    }

    @Override
    public void reload(File file) {

    }

    @Override
    public boolean execute(Container container, Reader reader) throws IOException {
        LineNumberReader lineReader = ImportUtils.getTextReader(reader);
        return execute(container, lineReader);
    }

    public boolean execute(Container container, File file) throws IOException {
        LineNumberReader lineReader = ImportUtils.getTextReader(new BufferedReader(new FileReader(file)));
        if (file != null) {
            container.setFile(FileUtil.toFileObject(file));
        }
        return execute(container, lineReader);
    }

    @Override
    public boolean execute(Container container, FileObject file) throws IOException {
        LineNumberReader lineReader = ImportUtils.getTextReader(file);
        container.setFile(file);
        return execute(container, lineReader);
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    public abstract boolean execute(Container container, LineNumberReader reader) throws IOException;

    protected String[] parseLine(String line) throws IOException {
        List<String> tokensOnThisLine = new ArrayList<>();
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
            } else if (!strictQuotes || (inQuotes && !ignoreQuotations)) {
                sb.append(c);
                inField = true;
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
     * Parse given input values as specified type
     *
     * @param attr
     * @param value
     * @param i
     * @param num
     * @param draft
     * @return
     */
    protected Object parseValue(AttributeDraft attr, String value, int i, int num, InstanceDraft draft) {
        Object castedVal = null;

        //check for missing values
        if (replaceMissingValues && missing.size() > 0) {
            if (value == null) {
                return Double.NaN;
            }
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

    public boolean isReplaceMissingValues() {
        return replaceMissingValues;
    }

    public void setReplaceMissingValues(boolean replaceMissingValues) {
        this.replaceMissingValues = replaceMissingValues;
    }

}
