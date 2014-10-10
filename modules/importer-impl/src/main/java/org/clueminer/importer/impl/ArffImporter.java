package org.clueminer.importer.impl;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
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
import org.clueminer.io.importer.api.Report;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.spi.FileImporter;
import org.clueminer.types.FileType;
import org.clueminer.utils.progress.Progress;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Attribute-Relation File Format (ARFF) importer
 *
 * @see http://weka.wikispaces.com/ARFF
 * @see http://www.cs.waikato.ac.nz/ml/weka/arff.html
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = FileImporter.class)
public class ArffImporter extends AbstractImporter implements FileImporter, LongTask {

    public static final String name = "ARFF";
    private static final Logger logger = Logger.getLogger(ArffImporter.class.getName());
    /**
     * matches eg. "
     *
     * @RELATION iris"
     */
    private static final Pattern relation = Pattern.compile("^@relation\\s+(\\w*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern attrTypes = Pattern.compile("\\{(\\d+,)+(\\d+)\\}", Pattern.CASE_INSENSITIVE);
    /**
     * matches eg. "
     *
     * @ATTRIBUTE sepallength	REAL"
     */
    private static final Pattern attribute = Pattern.compile("^@attribute\\s+['\"]?([\\w ._\\\\/-]*)['\"]?\\s+([\\w]*|\\{[(\\w+),]+\\})", Pattern.CASE_INSENSITIVE);

    private static final Pattern klassAttr = Pattern.compile("^@attribute ['\"]?class['\"]?\\s+\\{(.*)\\}", Pattern.CASE_INSENSITIVE);
    private ArrayList<Integer> skippedIndexes = new ArrayList<>();
    private int numInstances;

    /**
     * if line starts with following string, it will be ignored
     */
    private String comment = "%";

    private Matcher rmatch;
    private Matcher amatch;

    @Override

    public String getName() {
        return name;
    }

    /**
     * Will match pretty much anything
     *
     * @param mimeTypes
     * @return
     */
    @Override
    public boolean isAccepting(Collection mimeTypes) {
        String mime = mimeTypes.toString();
        return mime.contains("text") || mime.contains("octet-stream");
    }

    @Override
    public FileType[] getFileTypes() {
        FileType ft = new FileType(".arff", NbBundle.getMessage(getClass(), "fileType_ARFF_Name"));
        return new FileType[]{ft};
    }

    @Override
    public boolean isMatchingImporter(FileObject fileObject) {
        if (fileObject != null) {
            return fileObject.getExt().equalsIgnoreCase("arff");
        }
        return false;
    }

    @Override
    public boolean execute(Container container, LineNumberReader reader) throws IOException {
        this.container = container;
        if (container.getFile() != null) {
            logger.log(Level.INFO, "importing file {0}", container.getFile().getName());
        }
        ContainerLoader loader = container.getLoader();
        loader.reset(); //remove all previous instances
        loader.setDataset(null);
        loader.setNumberOfLines(0);
        this.report = new Report();
        logger.log(Level.INFO, "number of attributes = {0}", loader.getAttributeCount());

        for (AttributeDraft attr : loader.getAttributes()) {
            logger.log(Level.INFO, "attr: {0} type: {1}, role: {2}", new Object[]{attr.getName(), attr.getType(), attr.getRole()});
        }
        parseHeader(loader, reader);
        importData(loader, reader);
        fireAnalysisFinished();

        return !cancel;
    }

    /**
     * Should detect relation name, number of attributes their types etc.
     *
     * @param loader
     * @param reader
     * @throws IOException
     */
    protected void parseHeader(ContainerLoader loader, LineNumberReader reader) throws IOException {
        //number of attributes
        int attrNum = 0;
        int headerLine = 0;
        String line;
        AttributeDraft attrd;
        while (reader.ready()) {
            line = reader.readLine();
            if ((rmatch = relation.matcher(line)).matches()) {
                loader.setName(rmatch.group(1));
            } else if ((amatch = attribute.matcher(line)).matches()) {
                //System.out.println(line);
                if (!skippedIndexes.contains(headerLine)) {
                    //tries to convert string to enum, at top level we should catch the
                    //exception
                    //System.out.println(headerLine + ": " + line + " attr num= " + attrNum);
                    attrd = loader.createAttribute(attrNum, amatch.group(1));
                    //convertType(amatch.group(2).toUpperCase()))
                    attrd.setRole(BasicAttrRole.INPUT);
                    attrNum++;
                }
                headerLine++;
            } else if (isClassDefinition(line)) {
                //at which index we have the class attribute
                attrd = loader.createAttribute(attrNum, amatch.group(1));
                //convertType(amatch.group(2).toUpperCase()))
                attrd.setRole(BasicAttrRole.CLASS);
                attrNum++;
            } else if (line.equalsIgnoreCase("@data")) {
                return;
            }
        }
    }

    /**
     * amatch variable is later used
     *
     * @param line
     * @return
     */
    public boolean isClassDefinition(String line) {
        amatch = klassAttr.matcher(line);
        return amatch.matches();
    }

    protected String convertType(String type) {
        if ((attrTypes.matcher(type)).matches()) {
            return "REAL";
        }
        return type;
    }

    protected void importData(ContainerLoader loader, LineNumberReader reader) throws IOException {
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

        int count;
        int prev = -1;
        boolean reading = true;

        while (reader.ready() && reading) {
            String line = reader.readLine();
            count = reader.getLineNumber();
            //logger.log(Level.INFO, "line {0}: {1}", new Object[]{count, line});
            if (line != null && !line.isEmpty()) {
                lineRead(loader, count, line);
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

    protected void lineRead(ContainerLoader loader, int num, String line) throws IOException {
        if (!line.startsWith(comment)) {
            addInstance(loader, num, parseLine(line));
        }
    }

    private void addInstance(ContainerLoader loader, int num, String[] columns) {
        InstanceDraft draft = new InstanceDraftImpl(loader, loader.getAttributeCount());
        int i = 0;
        AttributeRole role;
        AttributeDraft attr;
        for (String value : columns) {
            try {
                attr = loader.getAttribute(i);
                if (attr == null) {
                    report.logIssue(new Issue("Missing " + i + "-th attribute definition", Issue.Level.WARNING));
                    return;
                }
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

    public void setSeparator(char separator) {
        if (this.separator != separator) {
            this.separator = separator;
        }
    }

    public ArrayList<Integer> getSkippedIndexes() {
        return skippedIndexes;
    }

    public void setSkippedIndexes(ArrayList<Integer> skippedIndexes) {
        this.skippedIndexes = skippedIndexes;
    }

}
