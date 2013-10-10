package org.clueminer.fixtures;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Tomas Barton
 */
public class MLearnFixture extends AbstractFixture {

    public File bosthouse() throws IOException {
        return resource("bosthouse/bosthouse.txt");
    }

    public File cars() throws IOException {
        return resource("car/cars.tab");
    }

    public File dermatology() throws IOException {
        return resource("dermatology/dermatology.arff");
    }

    public File forrestFires() throws IOException {
        return resource("forrestfires/forestfires.csv");
    }

    public File iris() throws IOException {
        return resource("iris/iris.data");
    }

    public File irisMissing() throws IOException {
        return resource("iris/iris-missings.data");
    }

    public File irisQuoted1() throws IOException {
        return resource("iris/iris-quoted1.data");
    }

    public File irisQuoted2() throws IOException {
        return resource("iris/iris-quoted2.data");
    }
}
