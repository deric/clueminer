package org.clueminer.fixtures;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Tomas Barton
 */
public class CommonFixture extends AbstractFixture {

    public File irisData() throws IOException {
        return new File(makePath("iris/iris", "data"));
    }

    public File irisArff() throws IOException {
        return new File(makePath("iris/iris", "arff"));
    }

    public File wineData() throws IOException {
        return new File(makePath("wine/wine", "data"));
    }

    public File umgArff() throws IOException {
        System.out.println(makePath("umg/data_umg2", "arff"));
        File f = new File(makePath("umg/data_umg2", "arff"));
        checkExistence(f);
        return f;
    }

    public File simpleCluster() throws IOException {
        return new File(getPath() + "/simple/cluster.arff");
    }

    public File yeastData() throws IOException {
        String datasetName = "yeast";
        return new File(getPath() + File.separatorChar + datasetName + File.separatorChar + datasetName + ".arff");
    }

    public File wellSeparatedCsv() throws IOException {
        File f = new File(makePath("simple/well-separated/well-separated", "csv"));
        checkExistence(f);
        return f;
    }

    public File miguel() throws IOException {
        return new File(getPath() + "/miguel/miguel.csv");
    }
}
