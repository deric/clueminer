/**
 * %HEADER%
 */
package org.clueminer.io;

import java.io.File;
import java.io.FileFilter;

/**
 * A FileFilter that accepts only directories
 * 
 * @see FileFilter
 * 
 * @author Thomas Abeel
 *
 */
public class DirectoryFilter implements FileFilter {

    @Override
    public boolean accept(File file) {
        return file.isDirectory();
    }

}
