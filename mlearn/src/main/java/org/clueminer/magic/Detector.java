package org.clueminer.magic;

import java.io.BufferedReader;

/**
 *
 * @author Tomas Barton
 */
public interface Detector {

    public DatasetProperties detect(BufferedReader file);
}
