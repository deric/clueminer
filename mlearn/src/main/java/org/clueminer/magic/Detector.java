package org.clueminer.magic;

import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author Tomas Barton
 */
public interface Detector {

    public DatasetProperties detect(BufferedReader file) throws IOException;
}
