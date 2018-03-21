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
package org.clueminer.importer.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import javax.swing.event.EventListenerList;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.io.importer.api.Report;
import org.clueminer.longtask.LongTaskErrorHandler;
import org.clueminer.longtask.LongTaskExecutor;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.spi.AnalysisListener;
import org.clueminer.spi.FileImporter;
import org.clueminer.utils.progress.ProgressTicket;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * Common code for all importers.
 *
 * @author deric
 * @param <E>
 */
public abstract class BaseImporter<E extends InstanceDraft> implements FileImporter<E>, LongTask {

    protected Container<E> container;
    protected Report report;
    protected ProgressTicket progressTicket;
    protected final LongTaskErrorHandler errorHandler;
    protected final LongTaskExecutor executor;
    protected final transient EventListenerList importListeners = new EventListenerList();
    protected boolean cancel = false;

    public BaseImporter() {
        errorHandler = new LongTaskErrorHandler() {
            @Override
            public void fatalError(Throwable t) {
                if (t instanceof OutOfMemoryError) {
                    return;
                }

                Exceptions.printStackTrace(t);
            }
        };
        executor = new LongTaskExecutor(true, "Importer", 10);
        report = new Report();
    }

    @Override
    public Container<E> getContainer() {
        return container;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }

    @Override
    public void addAnalysisListener(AnalysisListener listener) {
        importListeners.add(AnalysisListener.class, listener);
    }

    @Override
    public void removeListener(AnalysisListener listener) {
        importListeners.remove(AnalysisListener.class, listener);
    }

    protected void fireAnalysisFinished() {
        for (AnalysisListener im : importListeners.getListeners(AnalysisListener.class)) {
            im.analysisFinished(container);
        }
    }

    protected void fireAttributeChanged(AttributeDraft attribute, Object property) {
        for (AnalysisListener im : importListeners.getListeners(AnalysisListener.class)) {
            im.attributeChanged(attribute, property);
        }
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean execute(Container<E> container, Reader reader) throws IOException {
        LineNumberReader lineReader = ImportUtils.getTextReader(reader);
        return execute(container, lineReader, -1);
    }

    public boolean execute(Container<E> container, File file) throws IOException {
        LineNumberReader lineReader = ImportUtils.getTextReader(new BufferedReader(new FileReader(file)));
        if (file != null) {
            container.setFile(FileUtil.toFileObject(file));
        }
        return execute(container, lineReader);
    }

    @Override
    public boolean execute(Container<E> container, FileObject file) throws IOException {
        LineNumberReader lineReader = ImportUtils.getTextReader(file);
        container.setFile(file);
        return execute(container, lineReader);
    }

    @Override
    public boolean hasData() {
        if (container != null) {
            return container.getInstanceCount() > 0;
        }
        return false;
    }

}
