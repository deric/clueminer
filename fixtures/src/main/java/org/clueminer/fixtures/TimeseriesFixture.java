package org.clueminer.fixtures;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Tomas Barton
 */
public class TimeseriesFixture extends AbstractFixture {

    private static String PREFIX = "sax/timeseries/";

    public File data01() throws IOException {
        return resource(PREFIX + "timeseries01.csv");
    }

    public File data02() throws IOException {
        return resource(PREFIX + "timeseries02.csv");
    }

    public File irBenzin() throws IOException {
        return resource("ir/40757_3.CSV");
    }
}
