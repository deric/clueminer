package org.clueminer.project;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.ProjectController;
import org.clueminer.project.api.WorkspaceInformation;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class WorkspaceInformationImpl implements WorkspaceInformation {

    public enum Status {

        OPEN, CLOSED, INVALID
    };
    private static int count = 0;
    private Project project;
    private String name;
    private Status status = Status.CLOSED;
    private String source;
    //Lookup
    private transient List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    public WorkspaceInformationImpl(Project project) {
        this(project, "Workspace " + (
                (Lookup.getDefault().lookup(ProjectController.class).getCurrentWorkspace() != null) ? count : 0
                ));
    }

    public WorkspaceInformationImpl(Project project, String name) {
        this.project = project;
        this.name = name;

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        if (pc.getCurrentWorkspace() == null) {
            count = 0;
        }
        count++;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
        fireChangeEvent();
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public boolean hasSource() {
        return source != null;
    }

    public void open() {
        this.status = Status.OPEN;
        fireChangeEvent();
    }

    public void close() {
        this.status = Status.CLOSED;
        fireChangeEvent();
    }

    public void invalid() {
        this.status = Status.INVALID;
    }

    @Override
    public boolean isOpen() {
        return status == Status.OPEN;
    }

    @Override
    public boolean isClosed() {
        return status == Status.CLOSED;
    }

    @Override
    public boolean isInvalid() {
        return status == Status.INVALID;
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    public void fireChangeEvent() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : listeners) {
            listener.stateChanged(event);
        }
    }
}