package org.clueminer.project.api;

import javax.swing.event.ChangeListener;

/**
 *
 * @author Tomas Barton
 */
public interface WorkspaceInformation {

    public boolean isOpen();

    public boolean isClosed();

    public boolean isInvalid();

    public boolean hasSource();

    public String getSource();

    public String getName();

    public Project getProject();

    public void addChangeListener(ChangeListener listener);

    public void removeChangeListener(ChangeListener listener);
}
