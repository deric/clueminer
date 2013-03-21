package org.clueminer.fixtures;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Tomas Barton
 */
public class TimeseriesFixture extends AbstractFixture {

    public String saxTimeseries(String name) throws IOException {
        return makePath("sax/timeseries/" + name, "csv");
    }
    
    public File data01() throws IOException {
        return new File(saxTimeseries("timeseries01.csv"));
    }
    
    public File data02() throws IOException {
        return new File(saxTimeseries("timeseries02.csv"));
    }
}
