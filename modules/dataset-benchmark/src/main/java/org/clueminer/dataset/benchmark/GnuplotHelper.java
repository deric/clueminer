package org.clueminer.dataset.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Tomas Barton
 */
public class GnuplotHelper {

    public static final String gnuplotExtension = ".gpt";

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

    public void bashPlotScript(String[] plots, String dir, String term, String ext) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        //bash script to generate results
        String shFile = dir + File.separatorChar + "plot-" + ext;
        try (PrintWriter template = new PrintWriter(shFile, "UTF-8")) {
            template.write("#!/bin/bash\n"
                    + "cd data\n");
            template.write("TERM=\"" + term + "\"\n");
            for (String plot : plots) {
                template.write("gnuplot -e \"${TERM}\" " + plot + gnuplotExtension + " > ../" + plot + "." + ext + "\n");
            }
        }
        Runtime.getRuntime().exec("chmod u+x " + shFile);
    }

}
