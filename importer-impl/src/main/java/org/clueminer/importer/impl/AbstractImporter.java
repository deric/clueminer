package org.clueminer.importer.impl;

import java.io.File;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import org.clueminer.importer.ImportController;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.Report;
import org.clueminer.longtask.LongTaskErrorHandler;
import org.clueminer.longtask.LongTaskExecutor;
import org.clueminer.longtask.spi.LongTask;
import org.clueminer.spi.AnalysisListener;
import org.clueminer.spi.FileImporter;
import org.clueminer.utils.progress.ProgressTicket;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractImporter implements FileImporter, LongTask {

    protected File file;
    protected Reader reader;
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
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void setReader(Reader reader) {
        this.reader = reader;
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
    public void reload() {
        if (reader != null) {

            LongTask task = null;
            if (this instanceof LongTask) {
                task = (LongTask) this;
            }
            final FileImporter importer = (FileImporter) this;

            final ImportController controller = Lookup.getDefault().lookup(ImportController.class);
            String taskName = NbBundle.getMessage(AbstractImporter.class, "AbstractImporter.taskName");
            executor.execute(task, new Runnable() {
                @Override
                public void run() {
                    try {
                        logger.log(Level.INFO, "reloading file");
                        Container container = controller.importFile(reader, importer);
                        if (container != null && file != null) {
                            container.setSource(file.getAbsolutePath());
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }, taskName, errorHandler);

        } else {
            logger.log(Level.WARNING, "file reader is null");
        }
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
}
