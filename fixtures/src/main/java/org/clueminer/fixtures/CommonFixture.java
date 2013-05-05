package org.clueminer.fixtures;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Tomas Barton
 */
public class CommonFixture extends AbstractFixture {

    public File irisData() throws IOException {
        return resource("iris/iris.data");
    }

    public File irisArff() throws IOException {
        return resource("iris/iris.arff");
    }

    public File wineData() throws IOException {
        return resource("wine/wine.data");
    }

    public File umgArff() throws IOException {
        return resource("umg/data_umg2.arff");
    }

    public File simpleCluster() throws IOException {
        return resource("simple/cluster.arff");
    }

    public File yeastData() throws IOException {
        String datasetName = "yeast";
        return resource(File.separatorChar + datasetName + File.separatorChar + datasetName + ".arff");
    }

    public File wellSeparatedCsv() throws IOException {
        return resource("simple/well-separated/well-separated.csv");
    }

    public File miguel() throws IOException {
        return resource("miguel/miguel.csv");
    }
}
