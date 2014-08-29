package org.clueminer.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Tomas Barton
 */
public class DialogFileFilter extends javax.swing.filechooser.FileFilter {

    private String description;
    private List<String> extensions;

    public DialogFileFilter(String description) {
        if (description == null) {
            Logger.getLogger(DialogFileFilter.class.getName()).throwing(getClass().getName(), "constructor", new NullPointerException("Description cannot be null."));
        }
        this.description = description;
        this.extensions = new ArrayList<String>();
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory() || extensions.isEmpty()) {
            return true;
        }
        String fileName = file.getName().toLowerCase();
        for (String extension : extensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        StringBuilder buffer = new StringBuilder(description);
        buffer.append(" (");
        for (String extension : extensions) {
            buffer.append("*").append(extension).append(" ");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.append(")").toString();
    }

    public void setDescription(String description) {
        if (description == null) {
            Logger.getLogger(DialogFileFilter.class.getName()).throwing(getClass().getName(), "setDescription", new NullPointerException("Description cannot be null."));
        }
        this.description = description;
    }

    public void addExtension(String extension) {
        if (extension == null) {
            Logger.getLogger(DialogFileFilter.class.getName()).throwing(getClass().getName(), "addExtension", new NullPointerException("Description cannot be null."));
        }
        extensions.add(extension);
    }

    public void addExtensions(String[] extension) {
        if (extension == null) {
            Logger.getLogger(DialogFileFilter.class.getName()).throwing(getClass().getName(), "addExtensions", new NullPointerException("Description cannot be null."));
        }
        extensions.addAll(Arrays.asList(extension));
    }

    public void removeExtension(String extension) {
        extensions.remove(extension);
    }

    public void clearExtensions() {
        extensions.clear();
    }

    public List<String> getExtensions() {
        return extensions;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DialogFileFilter)) {
            return false;
        }
        DialogFileFilter s = (DialogFileFilter) obj;
        if (s.extensions.size() != this.extensions.size()) {
            return false;
        }
        for (int i = 0; i < extensions.size(); i++) {
            if (this.extensions.get(i) == null ? s.extensions.get(i) != null : !this.extensions.get(i).equals(s.extensions.get(i))) {
                if (!this.extensions.get(i).equals(s.extensions.get(i))) {
                    return false;
                }
            }
        }
        if (!description.equals(s.description)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 19 * hash + (this.extensions != null ? this.extensions.hashCode() : 0);
        return hash;
    }

}