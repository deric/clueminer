package org.clueminer.export.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractExporter implements ActionListener {

    protected static final RequestProcessor RP = new RequestProcessor("Export");
    protected RequestProcessor.Task task;
    protected static final String prefKey = "last_folder";
    protected DialogDescriptor dialog = null;
    protected File defaultFolder = null;
    protected FileFilter fileFilter;
    private static final Logger logger = Logger.getLogger(AbstractExporter.class.getName());

    public abstract String getName();

    public abstract void updatePreferences(Preferences p);

    public abstract JPanel getOptions();

    public abstract FileFilter getFileFilter();

    public abstract String getExtension();

    public abstract boolean hasData();

    public abstract Runnable getRunner(File file, Preferences pref, ProgressHandle ph);

    public void showDialog() {
        dialog = new DialogDescriptor(getOptions(), "Export", true, NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.OK_CANCEL_OPTION,
                DialogDescriptor.BOTTOM_ALIGN, null, this);

        dialog.setClosingOptions(new Object[]{});
        DialogDisplayer.getDefault().notifyLater(dialog);
    }

    public void export() {
        Preferences p = NbPreferences.root().node("/clueminer/exporter");
        updatePreferences(p);
        makeExport(p);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == DialogDescriptor.OK_OPTION) {
            export();
            dialog.setClosingOptions(null);
        } else if (event.getSource() == DialogDescriptor.CANCEL_OPTION) {
            dialog.setClosingOptions(null);
        }
    }

    protected JFileChooser getFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setDialogTitle(getName());
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setCurrentDirectory(defaultFolder);

        fileChooser.addChoosableFileFilter(getFileFilter());
        return fileChooser;
    }

    public void makeExport(Preferences pref) {
        if (!hasData()) {
            NotifyDescriptor d
                    = new NotifyDescriptor.Message("No data for export", NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            logger.warning("missing data for export");
            return;
        }
        String folder = pref.get(prefKey, null);
        if (folder != null) {
            defaultFolder = new File(folder);
        }
        JFileChooser fileChooser = getFileChooser();

        defaultFolder = fileChooser.getCurrentDirectory();
        pref.put(prefKey, fileChooser.getCurrentDirectory().getAbsolutePath());

        if (fileChooser.showSaveDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filename = file.getName();

            if (fileChooser.getFileFilter() == fileFilter) {
                if (!filename.endsWith(getExtension())) {
                    file = new File(file.getAbsolutePath() + getExtension());
                }
            }

            Object retval = NotifyDescriptor.YES_OPTION;
            if (file.exists()) {
                NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                        "This file already exists. Do you want to overwrite it?",
                        "Overwrite",
                        NotifyDescriptor.YES_NO_OPTION);
                retval = DialogDisplayer.getDefault().notify(d);
            }

            if (retval.equals(NotifyDescriptor.YES_OPTION)) {
                final ProgressHandle ph = ProgressHandle.createHandle(getName() + ":" + file.getName());
                createTask(file, pref, ph);
            } else {
                makeExport(pref);
            }
        }
    }

    /**
     *
     * @param file
     * @param pref
     * @param ph
     */
    protected void createTask(final File file, Preferences pref, final ProgressHandle ph) {
        task = RP.create(getRunner(file, pref, ph));
        //task.addTaskListener(analysis);
        logger.log(Level.INFO, "starting export to {0}", file.getAbsolutePath());
        task.addTaskListener(new TaskListener() {
            @Override
            public void taskFinished(org.openide.util.Task task) {
                //make sure that we get rid of the ProgressHandle
                //when the task is finished
                ph.finish();
                logger.log(Level.INFO, "export to {0} finished", file.getAbsolutePath());
            }
        });
        task.schedule(0);
    }

}
