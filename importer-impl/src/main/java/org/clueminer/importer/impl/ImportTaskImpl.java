package org.clueminer.importer.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import org.clueminer.gui.msg.NotifyUtil;
import org.clueminer.importer.ImportController;
import org.clueminer.importer.ImportTask;
import org.clueminer.importer.gui.ImportControllerUIImpl;
import org.clueminer.importer.gui.ReportPanel;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.ContainerLoader;
import org.clueminer.io.importer.api.Report;
import org.clueminer.processor.spi.Processor;
import org.clueminer.processor.spi.ProcessorUI;
import org.clueminer.project.api.ProjectController;
import org.clueminer.project.api.ProjectControllerUI;
import org.clueminer.project.api.Workspace;
import org.clueminer.spi.FileImporter;
import org.clueminer.spi.ImportListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public class ImportTaskImpl implements ImportTask {

    private final FileImporter importer;
    private final FileObject fileObject;
    private final ImportController controller;
    private final transient EventListenerList importListeners = new EventListenerList();
    private final static Logger logger = Logger.getLogger(ImportTaskImpl.class.getName());

    public ImportTaskImpl(FileImporter importer, FileObject fileObject, ImportController controller) {
        this.importer = importer;
        this.fileObject = fileObject;
        this.controller = controller;
    }

    @Override
    public ContainerLoader getContainer() {
        return importer.getContainer().getLoader();
    }

    @Override
    public void run() {
        InputStream stream = null;
        try {
            stream = fileObject.getInputStream();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }

        final String containerSource = fileObject.getNameExt();
        try {
            logger.log(Level.INFO, "imporing file..");
            Container container = controller.importFile(stream, importer);
            if (container != null) {
                container.setSource(containerSource);
            }
            logger.log(Level.INFO, "displaing import dialog...");
            //display import window
            finishImport(container);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (fileObject.getPath().startsWith(System.getProperty("java.io.tmpdir"))) {
                try {
                    fileObject.delete();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private void finishImport(Container container) {
        if (container.verify()) {
            Report report = container.getReport();

            //Report panel
            ReportPanel reportPanel = new ReportPanel();
            reportPanel.setCurrentFile(fileObject);
            reportPanel.setData(report, container);
            DialogDescriptor dd = new DialogDescriptor(reportPanel, NbBundle.getMessage(ImportControllerUIImpl.class, "ReportPanel.title"));
            if (!DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                reportPanel.destroy();
                return;
            }
            reportPanel.destroy();

            if (dd.getValue() == DialogDescriptor.OK_OPTION) {
                //ok button was pressed
                logger.log(Level.INFO, "OOOK");
                final Processor processor = reportPanel.getProcessor();

                //Project
                Workspace workspace = null;
                ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
                ProjectControllerUI pcui = Lookup.getDefault().lookup(ProjectControllerUI.class);
                if (pc.getCurrentProject() == null) {
                    pcui.newProject();
                    workspace = pc.getCurrentWorkspace();
                }
                logger.log(Level.INFO, "processing input file");
                controller.process(container, processor, workspace);
                logger.log(Level.INFO, "dataset size: {0}", container.getLoader().getDataset().size());
                fireDataLoaded();
            } else {
                //cancel button was pressed
            }

            /*  //Process
             final ProcessorUI pui = getProcessorUI(processor);
             final ValidResult validResult = new ValidResult();
             if (pui != null) {

             try {
             SwingUtilities.invokeAndWait(new Runnable() {
             @Override
             public void run() {
             String title = NbBundle.getMessage(ImportControllerUIImpl.class, "ImportControllerUIImpl.processor.ui.dialog.title");
             JPanel panel = pui.getPanel();
             pui.setup(processor);
             final DialogDescriptor dd2 = new DialogDescriptor(panel, title);
             if (panel instanceof ValidationPanel) {
             ValidationPanel vp = (ValidationPanel) panel;
             vp.addChangeListener(new ChangeListener() {
             @Override
             public void stateChanged(ChangeEvent e) {
             dd2.setValid(!((ValidationPanel) e.getSource()).isFatalProblem());
             }
             });
             dd2.setValid(!vp.isFatalProblem());
             }
             Object result = DialogDisplayer.getDefault().notify(dd2);
             if (result.equals(NotifyDescriptor.CANCEL_OPTION) || result.equals(NotifyDescriptor.CLOSED_OPTION)) {
             validResult.setResult(false);
             } else {
             pui.unsetup(); //true
             validResult.setResult(true);
             }
             }
             });
             } catch (InterruptedException ex) {
             Exceptions.printStackTrace(ex);
             } catch (InvocationTargetException ex) {
             Exceptions.printStackTrace(ex);
             }

             }
             if (validResult.isResult()) {

             controller.process(container, processor, workspace);
             fireDataLoaded();
             //StatusLine notify
             String source = container.getSource();
             if (source.isEmpty()) {
             source = NbBundle.getMessage(ImportControllerUIImpl.class, "ImportControllerUIImpl.status.importSuccess.default");
             }
             StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(ImportControllerUIImpl.class, "ImportControllerUIImpl.status.importSuccess", source));
             }*/
        } else {
            NotifyUtil.error("Error", "Bad container", false);
        }
    }

    private ProcessorUI getProcessorUI(Processor processor) {
        for (ProcessorUI pui : Lookup.getDefault().lookupAll(ProcessorUI.class)) {
            if (pui.isUIFoProcessor(processor)) {
                return pui;
            }
        }
        return null;
    }

    @Override
    public void addListener(ImportListener listener) {
        importListeners.add(ImportListener.class, listener);
    }

    @Override
    public void removeListener(ImportListener listener) {
        importListeners.remove(ImportListener.class, listener);
    }

    public void fireDataLoaded() {
        for (ImportListener im : importListeners.getListeners(ImportListener.class)) {
            im.dataLoaded();
        }
    }

    private static class ValidResult {

        private boolean result = true;

        public void setResult(boolean result) {
            this.result = result;
        }

        public boolean isResult() {
            return result;
        }
    }
}
