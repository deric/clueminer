package org.clueminer.project.api;

import java.io.File;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Tomas Barton
 */
public interface ProjectInformation {
    
    public void open();
    
    public void close();

    public boolean isOpen();

    public boolean isClosed();

    public boolean isInvalid();

    public String getName();

    public boolean hasFile();

    public String getFileName();

    public File getFile();

    public Project getProject();

    public void addChangeListener(ChangeListener listener);

    public void removeChangeListener(ChangeListener listener);
}
