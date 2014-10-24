package org.clueminer.clustering.benchmark;

import com.beust.jcommander.Parameter;

/**
 *
 * @author Tomas Barton
 */
public class BenchParams {

    @Parameter(names = "--n", description = "size of biggest dataset", required = true)
    public int n;

    @Parameter(names = "--n-small", description = "size of smallest", required = false)
    public int nSmall;

    @Parameter(names = "--steps", description = "number of datasets which will be generated")
    public int steps = 10;

    @Parameter(names = "--repeat", description = "number of repetitions of each algorithm")
    public int repeat = 10;

    @Parameter(names = "--dimension", description = "number of attributes of each dataset")
    public int dimension = 5;

    @Parameter(names = "--linkage", description = "linkage method")
    public String linkage = "Single Linkage";

}
