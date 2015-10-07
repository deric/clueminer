package org.clueminer.gnuplot;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class GnuplotHelper {

    public static final String gnuplotExtension = ".gpt";
    protected char separator = ',';
    protected String customTitle;
    protected String currentDir;

    public String mkdir(String folder) {
        File file = new File(folder);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("Failed to create " + folder + " !");
            }
        }
        return file.getAbsolutePath();
    }

    public String safeName(String name) {
        return name.toLowerCase().replace(" ", "_");
    }

    /**
     * Removes from filename the extension and return its name (without path)
     *
     * @param file
     * @return name of the file without the extension
     */
    public String withoutExtension(File file) {
        String name = file.getName();
        int pos = name.lastIndexOf(".");
        if (pos > 0) {
            name = name.substring(0, pos);
        }
        return name;
    }

    private static String bashTemplate(String subdir) {
        StringBuilder sb = new StringBuilder("#!/bin/bash\n");
        sb.append("PWD=\"$(pwd)\"\n");

        if (subdir != null && !subdir.isEmpty()) {
            sb.append("cd ").append(subdir).append("\n");
        }
        return sb.toString();
    }

    /**
     *
     * @param plots plot names without extension
     * @param dir base dir
     * @param gnuplotDir directory with gnuplot file
     * @param term
     * @param ext extentions of output format
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static void bashPlotScript(String[] plots, String dir, String gnuplotDir, String term, String ext)
            throws FileNotFoundException, UnsupportedEncodingException, IOException {
        //bash script to generate results
        String shFile = dir + File.separatorChar + "_plot-" + ext;
        try (PrintWriter template = new PrintWriter(shFile, "UTF-8")) {
            template.write(bashTemplate(gnuplotDir));
            template.write("TERM=\"" + term + "\"\n");
            int pos;
            for (String plot : plots) {
                pos = plot.indexOf(".");
                if (pos > 0) {
                    //remove extension part
                    plot = plot.substring(0, pos);
                }
                template.write("gnuplot -e \"${TERM}\" " + "$PWD" + File.separatorChar
                        + gnuplotDir + File.separatorChar + plot + gnuplotExtension
                        + " > $PWD" + File.separatorChar + plot + "." + ext + "\n");
            }
        }
        Runtime.getRuntime().exec("chmod u+x " + shFile);
    }

    public void writeCsvLine(File file, String[] columns, boolean apend) {
        try (PrintWriter writer = new PrintWriter(
                new FileOutputStream(file, apend)
        )) {

            CSVWriter csv = new CSVWriter(writer, separator);
            csv.writeNext(columns, false);
            writer.close();

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    protected String getDataDir(String dir) {
        return mkdir(dir) + File.separatorChar + "data" + File.separatorChar;
    }

    public void setCurrentDir(String benchmarkDir, String subDirectory) {
        currentDir = benchmarkDir + File.separatorChar + subDirectory;
        mkdir(currentDir);
    }

    public String getCurrentDir() {
        return currentDir;
    }

    /**
     * Writes file with a Gnuplot script
     *
     * @param dir
     * @param scriptFile
     * @param content
     * @return result filename
     */
    public String writeGnuplot(String dir, String scriptFile, String content) {
        PrintWriter template = null;
        String script = dir + File.separatorChar + scriptFile + gnuplotExtension;
        try {
            template = new PrintWriter(script, "UTF-8");
            template.write(content);
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (template != null) {
                template.close();
            }
        }
        return script;
    }

}
