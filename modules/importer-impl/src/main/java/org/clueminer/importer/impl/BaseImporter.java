/*
 * Copyright (C) 2011-2016 clueminer.org
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

import javax.swing.event.EventListenerList;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.Report;
import org.clueminer.longtask.LongTaskErrorHandler;
import org.clueminer.longtask.LongTaskExecutor;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.spi.AnalysisListener;
import org.clueminer.spi.FileImporter;
import org.clueminer.utils.progress.ProgressTicket;
import org.openide.util.Exceptions;

/**
 * Common code for all importers.
 *
 * @author deric
 */
public abstract class BaseImporter implements FileImporter, LongTask {

    protected Container container;
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
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public Report getReport() {
        return report;
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

}
