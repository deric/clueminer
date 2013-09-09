package org.clueminer.fixtures;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Tomas Barton
 */
public class PosturomedFixture  extends AbstractFixture {
    
    private static String folder = "posturomed/";

    public PosturomedFixture() {
        super();
    }

    public File testData() throws IOException {        
        return resource(folder + "HG01.PSS");
    }
}

