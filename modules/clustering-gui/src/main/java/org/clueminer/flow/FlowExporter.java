/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.flow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import org.openide.windows.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class FlowExporter implements ActionListener {

    private static final String PREF_KEY = "last_folder";
    private File defaultFolder = null;
    private JFileChooser fileChooser;
    private FileFilter jsonFilter;
    private static final RequestProcessor RP = new RequestProcessor("Flow exporter");
    private RequestProcessor.Task task;
    private final NodeContainer container;
    private static final Logger LOG = LoggerFactory.getLogger(FlowExporter.class);

    public FlowExporter(NodeContainer container) {
        this.container = container;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Preferences pref = NbPreferences.root().node("/org/clueminer/cluster-preview");
        displayFileChooser(pref);
    }

    private void displayFileChooser(Preferences pref) {
        String folder = pref.get(PREF_KEY, null);
        if (folder != null) {
            defaultFolder = new File(folder);
        }
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            fileChooser.setDialogTitle("Save flow");
            fileChooser.setAcceptAllFileFilterUsed(true);
            fileChooser.setCurrentDirectory(defaultFolder);
            fileChooser.setMultiSelectionEnabled(true);

            jsonFilter = new FileFilter() {

                @Override
                public boolean accept(File file) {
                    String filename = file.getName();
                    return file.isDirectory() || filename.endsWith(".json");
                }

                @Override
                public String getDescription() {
                    return "JSON (*.json)";
                }
            };
            fileChooser.addChoosableFileFilter(jsonFilter);

        }
        defaultFolder = fileChooser.getCurrentDirectory();
        if (defaultFolder != null) {
            pref.put(PREF_KEY, fileChooser.getCurrentDirectory().getAbsolutePath());
        }
        if (fileChooser.showOpenDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();

            Object retval = NotifyDescriptor.YES_OPTION;
            if (retval.equals(NotifyDescriptor.YES_OPTION)) {
                final ProgressHandle ph = ProgressHandle.createHandle("Saving flow");

                task = RP.create(() -> {
                    try {
                        ph.start(2);
                        if (files.length == 0 || files[0] == null) {
                            throw new RuntimeException("missing target file: " + files[0]);
                        }
                        File f = files[0];
                        ph.progress(1);
                        Files.write(Paths.get(f.getAbsolutePath()), container.serialize().getBytes());
                        ph.progress(2);
                        LOG.info("flow saved to {}", f.getAbsolutePath());
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                });
                //task.addTaskListener(parent);
                task.addTaskListener(new TaskListener() {
                    @Override
                    public void taskFinished(org.openide.util.Task task) {
                        //make sure that we get rid of the ProgressHandle
                        //when the task is finished
                        ph.finish();

                    }
                });
                task.schedule(0);

            }
        }
    }

}
