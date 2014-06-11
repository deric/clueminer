package org.clueminer.importer.impl;

import java.io.File;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import org.clueminer.importer.ImportController;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.Report;
import org.clueminer.longtask.LongTaskErrorHandler;
import org.clueminer.longtask.LongTaskExecutor;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.spi.AnalysisListener;
import org.clueminer.spi.FileImporter;
import org.clueminer.utils.progress.ProgressTicket;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractImporter implements FileImporter, LongTask {

    protected Container container;
    protected Report report;
    protected ProgressTicket progressTicket;
    private static final Logger logger = Logger.getLogger(AbstractImporter.class.getName());
    private final LongTaskErrorHandler errorHandler;
    private final LongTaskExecutor executor;
    protected final transient EventListenerList importListeners = new EventListenerList();

    public AbstractImporter() {
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
    public void reload(final FileObject file, final Reader reader) {
        logger.info("reload called");
        LongTask task = null;
        if (this instanceof LongTask) {
            task = (LongTask) this;
        }
        final FileImporter importer = this;
        final ImportController controller = Lookup.getDefault().lookup(ImportController.class);
        String taskName = NbBundle.getMessage(AbstractImporter.class, "AbstractImporter.taskName");
        if (!executor.isRunning()) {
            executor.execute(task, new Runnable() {
                @Override
                public void run() {
                    try {
                        logger.log(Level.INFO, "reloading file");
                        controller.importFile(file, reader, importer, true);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }, taskName, errorHandler);
        } else {
            logger.log(Level.INFO, "executor is still running");
        }
    }

    @Override
    public void reload(File file) {

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
}
