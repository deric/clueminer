package org.clueminer.fixtures;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Tomas Barton
 */
public class FluorescenceFixture extends AbstractFixture {
    
    private static String folder = "flourescence/";

    public FluorescenceFixture() {
        super();
    }

    public File testData() throws IOException {        
        return resource(folder + "AP-01_2012112.arff");
    }
}
