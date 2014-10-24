package org.clueminer.clustering.benchmark;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.ObjectArrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.dataset.benchmark.PointTypeIterator;
import org.clueminer.report.BigORes;
import org.clueminer.report.Reporter;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class GnuplotReporter implements Reporter {

    private String dataFile;
    private String folder;
    private char separator = ',';
    private AgglomerativeClustering[] algorithms;

    public GnuplotReporter(String folder, String[] opts, AgglomerativeClustering[] algorithms) {
        this.dataFile = folder + File.separatorChar + "results.csv";
        this.algorithms = algorithms;
        writeHeader(opts);

        String memPath = folder + File.separatorChar + "mem.gpt";
        String cpuPath = folder + File.separatorChar + "cpu.gpt";

        writePlotScript(memPath, dataFile, 7, "memory", 2, 3);
        writePlotScript(cpuPath, dataFile, 7, "CPU", 2, 4);
    }

    private void writeHeader(String[] opts) {
        String[] head = new String[]{"label", "avg time (ms)", "memory (MB)", "total time (s)", "tps", "repeats"};
        String[] line = ObjectArrays.concat(head, opts, String.class);
        writeLine(line, false);
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
        writeLine(line, true);
    }

    protected void writeLine(String[] columns, boolean apend) {
        try (PrintWriter writer = new PrintWriter(
                new FileOutputStream(new File(dataFile), apend)
        )) {

            CSVWriter csv = new CSVWriter(writer, separator);
            csv.writeNext(columns, false);
            writer.close();

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     *
     * @param filename
     * @param dataFile
     * @param labelPos column of label which is used for data rows in chart
     * @param type
     * @param x
     * @param y
     */
    private void writePlotScript(String filename, String dataFile, int labelPos, String type, int x, int y) {
        PrintWriter template = null;
        try {
            template = new PrintWriter(filename, "UTF-8");
            template.write(plotComplexity(labelPos, type, x, y, dataFile, algorithms));
            template.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    private String plotComplexity(int labelPos, String yLabel, int x, int y, String dataFile, AgglomerativeClustering[] algorithms) {
        String res = "set datafile separator \",\"\n"
                + "set key outside bottom horizontal box\n"
                + "set title \"foo bar\"\n"
                + "set xlabel \"data size\" font \"Times,7\"\n"
                + "set ylabel \"" + yLabel + "\" font \"Times,7\"\n"
                //   + "set xtics 0,0.5 nomirror\n"
                //   + "set ytics 0,0.5 nomirror\n"
                + "set mytics 2\n"
                + "set mx2tics 2\n"
                + "set grid\n"
                + "set pointsize 0.5\n";
        int i = 0;
        int last = algorithms.length - 1;
        PointTypeIterator pti = new PointTypeIterator();
        for (AgglomerativeClustering alg : algorithms) {
            if (i == 0) {
                res += "plot ";
            }
            res += "\"< awk -F\\\",\\\" '{if($" + labelPos + " == \\\"" + alg.getName() + "\\\") print}' " + dataFile + "\" u " + x + ":" + y + " t \"" + alg.getName() + "\" w p pt " + pti.next();
            if (i != last) {
                res += ", \\\n";
            } else {
                res += "\n";
            }

            i++;
        }
        return res;
    }

}
