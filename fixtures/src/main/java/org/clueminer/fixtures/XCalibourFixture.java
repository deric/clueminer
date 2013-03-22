package org.clueminer.fixtures;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Tomas Barton
 */
public class XCalibourFixture extends AbstractFixture {

    public XCalibourFixture() {
        super();
    }

    public File testData() throws IOException {
        String config = "T0014BW.cdf";
        return resource("xcalibour/" + config);
    }

    public File testData2() throws IOException {
        String config = "T0091.cdf";
        return resource("xcalibour/" + config);
    }
}
