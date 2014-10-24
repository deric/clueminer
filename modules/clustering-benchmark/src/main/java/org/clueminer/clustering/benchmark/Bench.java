package org.clueminer.clustering.benchmark;

import java.io.File;

/**
 *
 * @author Tomas Barton
 */
public class Bench {

    public static void ensureFolder(String folder) {
        File file = new File(folder);
        if (!file.exists()) {
            if (file.mkdirs()) {
                System.out.println("Directory " + folder + " created!");
            } else {
                System.out.println("Failed to create " + folder + "directory!");
            }
        }
    }

}
