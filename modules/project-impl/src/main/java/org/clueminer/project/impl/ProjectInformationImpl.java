/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.project.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.ProjectInformation;

/**
 *
 * @author Tomas Barton
 */
public class ProjectInformationImpl implements ProjectInformation {

    public enum Status {

        NEW, OPEN, CLOSED, INVALID
    };
    private static int count = 0;
    //Data
    private String name;
    private Status status = Status.CLOSED;
    private File file;
    private Project project;
    //Event
    private transient List<ChangeListener> listeners;

    public ProjectInformationImpl(Project project) {
        this.project = project;
        name = "Project " + (count++);
        init();
    }

    private void init() {
        listeners = new ArrayList<>();
        status = Status.CLOSED;
        if (file != null) {
            // this.status = Status.INVALID;  if not valid
        }
    }

    @Override
    public void open() {
        this.status = Status.OPEN;
        fireChangeEvent();
    }

    @Override
    public void close() {
        this.status = Status.CLOSED;
        fireChangeEvent();
    }

    @Override
    public Project getProject() {
        return project;
    }

    //PROPERTIES
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
    public boolean hasFile() {
        return file != null;
    }

    @Override
    public String getFileName() {
        if (file == null) {
            return "";
        } else {
            return file.getName();
        }
    }

    public void setName(String name) {
        this.name = name;
        fireChangeEvent();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        fireChangeEvent();
    }

    //EVENTS
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
