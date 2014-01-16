package org.clueminer.export.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.clueminer.clustering.gui.ClusterAnalysis;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
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
public class CsvExporter implements ActionListener, PropertyChangeListener {

    private static CsvExporter instance;
    private File defaultFolder = null;
    private JFileChooser fileChooser;
    private FileFilter csvFilter;
    private CsvOptions options;
    private static final String prefKey = "last_folder";
    private DialogDescriptor d = null;
    private static final RequestProcessor RP = new RequestProcessor("CSV Export");
    private RequestProcessor.Task task;
    private ClusterAnalysis analysis;

    public static CsvExporter getDefault() {
        if (instance == null) {
            instance = new CsvExporter();
        }
        return instance;
    }

    public JPanel getOptions() {
        if (options == null) {
            options = new CsvOptions();
        }
        return options;
    }

    private CsvExporter() {
    }

    public void setAnalysis(ClusterAnalysis analysis) {
        this.analysis = analysis;
    }

    public void showDialog() {

        d = new DialogDescriptor(getOptions(), "Export to CSV", true, NotifyDescriptor.OK_CANCEL_OPTION,
                                 NotifyDescriptor.OK_CANCEL_OPTION,
                                 DialogDescriptor.BOTTOM_ALIGN, null, this);

        d.setClosingOptions(new Object[]{});
        d.addPropertyChangeListener(this);
        DialogDisplayer.getDefault().notifyLater(d);
    }

    public void export(Preferences pref) {
        if (analysis != null) {

            String folder = pref.get(prefKey, null);
            if (folder != null) {
                defaultFolder = new File(folder);
            }
            if (fileChooser == null) {
                fileChooser = new JFileChooser();
                fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
                fileChooser.setDialogTitle("Export CSV");
                fileChooser.setAcceptAllFileFilterUsed(true);
                fileChooser.setCurrentDirectory(defaultFolder);

                csvFilter = new FileFilter() {

                    @Override
                    public boolean accept(File file) {
                        String filename = file.getName();
                        return file.isDirectory() || filename.endsWith(".csv");
                    }

                    @Override
                    public String getDescription() {
                        return "CSV (*.csv)";
                    }
                };
                fileChooser.addChoosableFileFilter(csvFilter);

            }
            defaultFolder = fileChooser.getCurrentDirectory();
            pref.put(prefKey, fileChooser.getCurrentDirectory().getAbsolutePath());
            if (fileChooser.showSaveDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String filename = file.getName();
                FileFilter fileFilter = fileChooser.getFileFilter();

                String format;
                if (fileFilter == csvFilter) {
                    if (!filename.endsWith(".csv")) {
                        file = new File(file.getAbsolutePath() + ".csv");
                    }
                    format = "csv";
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
                    final ProgressHandle ph = ProgressHandleFactory.createHandle("Exporting CSV to " + file.getName());
                    task = RP.create(new CsvExportRunner(file, analysis, pref, ph));
                    task.addTaskListener(analysis);
                    task.addTaskListener(new TaskListener() {
                        @Override
                        public void taskFinished(org.openide.util.Task task) {
                            //make sure that we get rid of the ProgressHandle
                            //when the task is finished
                            ph.finish();
                        }
                    });
                    task.schedule(0);

                } else {
                    export(pref);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == DialogDescriptor.OK_OPTION) {
            Preferences p = NbPreferences.root().node("/clueminer/exporter");
            options.updatePreferences(p);
            export(p);

            d.setClosingOptions(null);
        } else if (event.getSource() == DialogDescriptor.CANCEL_OPTION) {
            d.setClosingOptions(null);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        //not much to do
    }

}
