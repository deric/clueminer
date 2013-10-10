package org.clueminer.fixtures;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Tomas Barton
 */
public class ImageFixture extends AbstractFixture {

    public File insect3d() throws IOException {
        return resource("insect/insect_dataset.png");
    }
}
