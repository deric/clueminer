package org.clueminer.fixtures;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tomas Barton
 */
public class AbstractFixture {

    protected String localPath;

    public AbstractFixture() {
        try {
            localPath = getPath();
        } catch (IOException ex) {
            Logger.getLogger(AbstractFixture.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected final String getPath() throws IOException {
        File dir = new File(getClass().getProtectionDomain().getCodeSource().
                getLocation().getFile() + "/../../src/main/resources/data");
        return dir.getCanonicalPath();
    }

    protected void checkExistence(File f) {
        if (!f.exists()) {
            throw new RuntimeException("file " + f.getPath() + " not found");
        }
    }

    public String makePath(String name, String ext) throws IOException {
        return localPath + "/" + name + "." + ext;
    }
}
