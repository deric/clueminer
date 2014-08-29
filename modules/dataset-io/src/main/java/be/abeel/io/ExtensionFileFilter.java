/**
 * %HEADER%
 */
package be.abeel.io;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Vector;

public class ExtensionFileFilter implements FileFilter {
    private Vector<String> extension = new Vector<String>();

    private void addExtension(String ext) {
        // Add a dot, it it doesn't start with one.
        if (!ext.startsWith("\\."))
            ext = "." + ext;
        this.extension.add(ext.toLowerCase());
    }

    public ExtensionFileFilter(List<String> ext) {
        for (String s : ext)
            addExtension(s.toLowerCase());
    }

    public ExtensionFileFilter(String ext) {
        addExtension(ext.toLowerCase());
    }

    public boolean accept(File file) {
        boolean accept = false;
        for (String s : extension) {
            if (file.getName().endsWith(s))
                accept = true;
        }
        return accept;
        // return extension.indexOf(file.getName().toLowerCase().e;
    }
}
