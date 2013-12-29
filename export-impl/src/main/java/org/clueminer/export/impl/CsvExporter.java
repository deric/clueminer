package org.clueminer.export.impl;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.gui.ClusterAnalysis;
import org.clueminer.dataset.api.Instance;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomas Barton
 */
public class CsvExporter {

    private static CsvExporter instance;
    private File defaultFolder = null;
    private JFileChooser fileChooser;
    private FileFilter csvFilter;

    public static CsvExporter getDefault() {
        if (instance == null) {
            instance = new CsvExporter();
        }
        return instance;
    }

    private CsvExporter() {
    }

    public void export(ClusterAnalysis analysis) {
        if (analysis != null) {
            try {
                Preferences p = NbPreferences.root().node("/clueminer/csvexporter");
                String folder = p.get("default_folder", null);
                if (folder != null) {
                    defaultFolder = new File(folder);
                } else {
                    defaultFolder = null;
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

                //     fileChooser.setSelectedFile(new File(panel.getName()));
                defaultFolder = fileChooser.getCurrentDirectory();
                //     p.put("default_folder", fileChooser.getCurrentDirectory().getAbsolutePath());
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
                        CSVWriter writer = new CSVWriter(new FileWriter(file));
                        String[] line, tmp;
                        int size;
                        try {
                            HierarchicalResult result = analysis.getResult();
                            if (result != null) {
                                Clustering<Cluster> clust = result.getClustering();
                                for (Cluster<? extends Instance> c : clust) {
                                    for (Instance inst : c) {
                                        line = inst.getName().split(",");
                                        size = line.length + 1;
                                        //append cluster label
                                        tmp = new String[size];
                                        System.arraycopy(line, 0, tmp, 0, line.length);
                                        line = tmp;
                                        line[size - 1] = c.getName();
                                        writer.writeNext(line);
                                    }
                                }
                            } else {
                                throw new RuntimeException("no clustering result. did you run clustering?");
                            }

                        } finally {
                            writer.close();
                        }
                    } else {
                        export(analysis);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
