package org.clueminer.clustering.benchmark;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.ObjectArrays;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.clueminer.report.BigORes;
import org.clueminer.report.Reporter;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class GnuplotReporter implements Reporter {

    private String filePath;
    private char separator = ',';

    public GnuplotReporter(String file) {
        this.filePath = file;
    }

    /**
     *
     * @param result
     */
    @Override
    public void finalResult(BigORes result) {
        String[] res = new String[]{result.getLabel(), result.avgTimeMs(),
            result.totalMemoryInMb(), result.totalTimeInS(), result.tps(),
            result.measurements()
        };
        String[] line = ObjectArrays.concat(res, result.getOpts(), String.class);
        writeLine(line);
    }

    protected void writeLine(String[] columns) {
        try (PrintWriter writer = new PrintWriter(filePath, "UTF-8")) {

            CSVWriter csv = new CSVWriter(writer, separator);
            csv.writeNext(columns);
            writer.close();

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
