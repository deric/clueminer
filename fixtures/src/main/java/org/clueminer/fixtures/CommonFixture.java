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

    public File wineArff() throws IOException {
        return resource("wine/wine.arff");
    }

    public File umgArff() throws IOException {
        return resource("umg/data_umg2.arff");
    }

    public File simpleCluster() throws IOException {
        return resource("simple/cluster.arff");
    }

    public File yeastData() throws IOException {
        String datasetName = "yeast";
        return resource(datasetName + File.separatorChar + datasetName + ".arff");
    }

    public File wellSeparatedCsv() throws IOException {
        return resource("simple/well-separated/well-separated.csv");
    }

    public File miguel() throws IOException {
        return resource("miguel/miguel.csv");
    }

    public File bananaArff() throws IOException {
        return resource("bananas/banana.arff");
    }

    public File glassArff() throws IOException {
        return resource("glass/glass.arff");
    }

    public File ionosphereArff() throws IOException {
        return resource("ionosphere/ionosphere.arff");
    }

    public File vehicleArff() throws IOException {
        return resource("vehicle/vehicle.arff");
    }

    public File insectArff() throws IOException {
        return resource("insect/insect.arff");
    }

    public File sonarArff() throws IOException {
        return resource("sonar/sonar.arff");
    }

    public File dermatologyArff() throws IOException {
        return resource("dermatology/dermatology.arff");
    }

    public File schoolData() throws IOException {
        return resource("school/school.data");
    }
}
