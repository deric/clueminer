package org.clueminer.clustering.preview;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomas Barton
 */
public class MetaLoaderDialog implements ActionListener {

    private static final String prefKey = "last_folder";
    private File defaultFolder = null;
    private JFileChooser fileChooser;
    private FileFilter csvFilter;
    private static final RequestProcessor RP = new RequestProcessor("Meta loader");
    private RequestProcessor.Task task;
    private Dataset<? extends Instance>[] datasets;
    private TaskListener parent;
    private Map<Integer, Color> metaColors;

    public MetaLoaderDialog(TaskListener l) {
        parent = l;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Preferences pref = NbPreferences.root().node("/org/clueminer/cluster-preview");
        displayFileChooser(pref);
    }

    private void displayFileChooser(Preferences pref) {
        String folder = pref.get(prefKey, null);
        if (folder != null) {
            defaultFolder = new File(folder);
        }
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            fileChooser.setDialogTitle("Load meta-data");
            fileChooser.setAcceptAllFileFilterUsed(true);
            fileChooser.setCurrentDirectory(defaultFolder);
            fileChooser.setMultiSelectionEnabled(true);

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
        if (defaultFolder != null) {
            pref.put(prefKey, fileChooser.getCurrentDirectory().getAbsolutePath());
        }
        if (fileChooser.showOpenDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();

            Object retval = NotifyDescriptor.YES_OPTION;
            if (retval.equals(NotifyDescriptor.YES_OPTION)) {
                final ProgressHandle ph = ProgressHandle.createHandle("Loading meta-data");
                datasets = new Dataset[files.length];
                final MetaLoaderRunner runner = new MetaLoaderRunner(files, pref, ph, datasets);
                task = RP.create(runner);
                //task.addTaskListener(parent);
                task.addTaskListener(new TaskListener() {
                    @Override
                    public void taskFinished(org.openide.util.Task task) {
                        //make sure that we get rid of the ProgressHandle
                        //when the task is finished
                        ph.finish();
                        System.out.println("task finished");
                        metaColors = runner.getColors();
                        datasets = runner.getResult();
                        parent.taskFinished(task);

                    }
                });
                task.schedule(0);

            }
        }
    }

    public Dataset<? extends Instance>[] getDatasets() {
        return datasets;
    }

    public Map<Integer, Color> getColors() {
        return metaColors;
    }
}
