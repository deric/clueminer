package org.clueminer.types;

/**
 * File type definition. A simple class which contains a <b>name</b> and
 * <b>extension</b> for a file type/
 *
 * @author Mathieu Bastian
 */
public final class FileType {

    private final String[] extensions;
    private final String name;

    public FileType(String extension, String name) {
        this.extensions = new String[]{extension};
        this.name = name;
    }

    public FileType(String[] extensions, String name) {
        this.extensions = extensions;
        this.name = name;
    }

    public String getExtension() {
        return extensions[0];
    }

    public String[] getExtensions() {
        return extensions;
    }

    public String getName() {
        return name;
    }
}