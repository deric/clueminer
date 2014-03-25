package org.clueminer.importer.impl;

import java.io.File;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.clueminer.io.importer.api.Report;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.spi.FileImporter;
import org.clueminer.types.ContainerLoader;
import org.clueminer.types.FileType;
import org.clueminer.utils.progress.Progress;
import org.clueminer.utils.progress.ProgressTicket;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = FileImporter.class)
public class CsvImporter implements FileImporter, LongTask {

    private String separator;
    private static final String name = "CSV";
    private File file;
    private Reader reader;
    private ContainerLoader container;
    private Report report;
    private ProgressTicket progressTicket;
    private boolean cancel = false;

    public CsvImporter() {
        separator = ",";
    }

    @Override
    public String getName() {
        return name;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean isAccepting(Collection mimeTypes) {
        String mime = mimeTypes.toString();
        //this will match pretty much anything
        return mime.contains("text") || mime.contains("octet-stream");
    }

    @Override
    public boolean execute(ContainerLoader loader) {
        this.container = loader;
        this.report = new Report();
        LineNumberReader lineReader = ImportUtils.getTextReader(reader);
        try {
            importData(lineReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return !cancel;
    }

    @Override
    public ContainerLoader getContainer() {
        return container;
    }

    @Override
    public Report getReport() {
        return report;
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

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }

    private void importData(LineNumberReader reader) throws Exception {
        Progress.start(progressTicket);        //Progress

        List<String> lines = new ArrayList<String>();
        for (; reader.ready();) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                lines.add(line);
            }
        }

        Progress.switchToDeterminate(progressTicket, lines.size());

        //Magix regex
        Pattern pattern = Pattern.compile("(?<=(?:,|;|\\s|^)\")(.*?)(?=(?<=(?:[^\\\\]))\",|;|\"\\s|\"$)|(?<=(?:,|;|\\s|^)')(.*?)(?=(?<=(?:[^\\\\]))',|;|'\\s|'$)|(?<=(?:,|;|\\s|^))(?=[^'\"])(.*?)(?=(?:,|;|\\s|$))|(?<=,|;)($)");

        Matcher m;
        for (String line : lines) {
            if (cancel) {
                return;
            }
            m = pattern.matcher(line);
            int count = 0;
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                if (start != end) {
                    String data = line.substring(start, end);
                    data = data.trim();
                    if (!data.isEmpty() && !data.toLowerCase().equals("null")) {
                        addInstance(data);
                    }
                }
                count++;
            }
            Progress.progress(progressTicket);      //Progress
        }
    }

    private void addInstance(String data) {
        //container.
    }
}
