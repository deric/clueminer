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
package org.clueminer.branding.actions;

import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;
import org.clueminer.openfile.OpenFileDialogFilter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileChooserBuilder.SelectionApprover;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.UserCancelException;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Action which allows user open file from disk. It is installed in Menu | File
 * | Open file... .
 *
 * Inspired by NetBeans IDE OpenFileAction
 *
 * @author Jesse Glick
 * @author Marian Petras
 */
@ActionID(
        category = "File",
        id = "org.clueminer.branding.actions.OpenFileAction")
@ActionRegistration(iconBase = "org/clueminer/branding/actions/open-folder16.png",
        displayName = "#CTL_OpenFile")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1150, separatorBefore = 1100),
    @ActionReference(path = "Toolbars/File", position = 300),
    @ActionReference(path = "Shortcuts", name = "D-O")
})
@Messages("CTL_OpenFile=Open File...")
public final class OpenFileAction implements ActionListener {

    /**
     * stores the last current directory of the file chooser
     */
    private static File currentDirectory = null;
    private static boolean running;

    /**
     * Creates and initializes a file chooser.
     *
     * @return the initialized file chooser
     */
    protected JFileChooser prepareFileChooser() {
        FileChooserBuilder fcb = new FileChooserBuilder(OpenFileAction.class);
        fcb.setSelectionApprover(new OpenFileSelectionApprover());
        fcb.setFilesOnly(true);
        fcb.setFileHiding(true);//don't show hidden files
        fcb.addDefaultFileFilters();
        for (OpenFileDialogFilter filter
                : Lookup.getDefault().lookupAll(OpenFileDialogFilter.class)) {
            fcb.addFileFilter(filter);
        }
        JFileChooser chooser = fcb.createFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.getCurrentDirectory().listFiles(); //preload
        chooser.setCurrentDirectory(getCurrentDirectory());
        return chooser;
    }

    /**
     * Displays the specified file chooser and returns a list of selected files.
     *
     * @param chooser file chooser to display
     * @return array of selected files,
     * @exception org.openide.util.UserCancelException if the user cancelled the
     * operation
     */
    public static File[] chooseFilesToOpen(JFileChooser chooser)
            throws UserCancelException {
        File[] files;
        do {
            int selectedOption = chooser.showOpenDialog(
                    WindowManager.getDefault().getMainWindow());

            if (selectedOption != JFileChooser.APPROVE_OPTION) {
                throw new UserCancelException();
            }
            files = chooser.getSelectedFiles();
        } while (files.length == 0);
        return files;
    }

    /**
     * {@inheritDoc} Displays a file chooser dialog and opens the selected
     * files.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            return;
        }
        try {
            running = true;
            JFileChooser chooser = prepareFileChooser();
            File[] files;
            try {
                if (Boolean.getBoolean("nb.native.filechooser")) { //NOI18N
                    String oldFileDialogProp = System.getProperty("apple.awt.fileDialogForDirectories"); //NOI18N
                    System.setProperty("apple.awt.fileDialogForDirectories", "false"); //NOI18N
                    FileDialog fileDialog = new FileDialog(WindowManager.getDefault().getMainWindow());
                    fileDialog.setMode(FileDialog.LOAD);
                    fileDialog.setDirectory(getCurrentDirectory().getAbsolutePath());
                    fileDialog.setTitle(chooser.getDialogTitle());
                    fileDialog.setVisible(true);
                    if (null != oldFileDialogProp) {
                        System.setProperty("apple.awt.fileDialogForDirectories", oldFileDialogProp); //NOI18N
                    } else {
                        System.clearProperty("apple.awt.fileDialogForDirectories"); //NOI18N
                    }

                    if (fileDialog.getDirectory() != null && fileDialog.getFile() != null) {
                        String selFile = fileDialog.getFile();
                        File dir = new File(fileDialog.getDirectory());
                        files = new File[]{new File(dir, selFile)};
                        currentDirectory = dir;
                    } else {
                        throw new UserCancelException();
                    }
                } else {
                    files = chooseFilesToOpen(chooser);
                    currentDirectory = chooser.getCurrentDirectory();
                }
            } catch (UserCancelException ex) {
                return;
            }
            for (File file : files) {
                OpenFile.openFile(file);
            }
        } finally {
            running = false;
        }
    }

    private static File getCurrentDirectory() {
        if (Boolean.getBoolean("netbeans.openfile.197063")) {
            // Prefer to open from parent of active editor, if any.
            TopComponent activated = TopComponent.getRegistry().getActivated();
            if (activated != null && WindowManager.getDefault().isOpenedEditorTopComponent(activated)) {
                DataObject d = activated.getLookup().lookup(DataObject.class);
                if (d != null) {
                    File f = FileUtil.toFile(d.getPrimaryFile());
                    if (f != null) {
                        return f.getParentFile();
                    }
                }
            }
        }
        // Otherwise, use last-selected directory, if any.
        if (currentDirectory != null && currentDirectory.exists()) {
            return currentDirectory;
        }
        // Fall back to default location ($HOME or similar).
        currentDirectory
                = FileSystemView.getFileSystemView().getDefaultDirectory();
        return currentDirectory;
    }

    private static class OpenFileSelectionApprover
            implements SelectionApprover {

        @Override
        public boolean approve(File[] selectedFiles) {
            /* check the files: */
            List<String> errorMsgs = null;
            for (int i = 0; i < selectedFiles.length; i++) {
                String msgPatternRef = null;
                File file = selectedFiles[i];

                if (!file.exists()) {
                    msgPatternRef = "MSG_FileDoesNotExist";             //NOI18N
                } else if (file.isDirectory()) {
                    msgPatternRef = "MSG_FileIsADirectory";             //NOI18N
                } else if (!file.isFile()) {
                    msgPatternRef = "MSG_FileIsNotPlainFile";           //NOI18N
                }
                if (msgPatternRef == null) {
                    continue;
                }
                if (errorMsgs == null) {
                    errorMsgs = new ArrayList<>(selectedFiles.length - i);
                }
                errorMsgs.add(NbBundle.getMessage(OpenFileAction.class,
                        msgPatternRef, file.getName()));
            }
            if (errorMsgs == null) {
                return true;
            } else {
                JPanel panel = new JPanel(
                        new GridLayout(errorMsgs.size(), 0, 0, 2));   //gaps
                for (String errMsg : errorMsgs) {
                    panel.add(new JLabel(errMsg));
                }
                DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(
                                panel, NotifyDescriptor.WARNING_MESSAGE));
                return false;
            }
        }
    }
}
