package org.clueminer.cluster;

import java.io.File;
import java.io.IOException;
import org.junit.Test;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class CreateFolderTest {
/**
 * @FIXME fails on Linux 64bit
 */
    @Test
    public void testCreateFolder() {
        
        String dir = System.getProperty("user.home") + File.separatorChar + "testFolder";
        File f = new File(dir);
        if(!f.exists()){
            try {
                FileUtil.createFolder(f);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
