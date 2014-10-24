package org.clueminer.clustering.benchmark;

import com.beust.jcommander.Parameter;
import java.io.File;
import org.clueminer.utils.FileUtils;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public class BenchParams {

    @Parameter(names = "--dir", description = "directory for results", required = false)
    public String home = System.getProperty("user.home") + File.separatorChar
            + NbBundle.getMessage(FileUtils.class, "FOLDER_Home");

    @Parameter(names = "--n", description = "size of biggest dataset", required = false)
    public int n = 20;

    @Parameter(names = "--n-small", description = "size of smallest", required = false)
    public int nSmall = 5;

    @Parameter(names = "--steps", description = "number of datasets which will be generated")
    public int steps = 4;

    @Parameter(names = "--repeat", description = "number of repetitions of each algorithm")
    public int repeat = 2;

    @Parameter(names = "--dimension", description = "number of attributes of each dataset")
    public int dimension = 5;

    @Parameter(names = "--linkage", description = "linkage method")
    public String linkage = "Single Linkage";

}
