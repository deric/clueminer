package org.clueminer.export.impl;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.gui.ClusterAnalysis;
import org.clueminer.dataset.api.Instance;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class CsvExportRunner implements Runnable {

    private final File file;
    private final ClusterAnalysis analysis;
    private final Preferences pref;

    public CsvExportRunner(File file, ClusterAnalysis analysis, Preferences pref) {
        this.file = file;
        this.analysis = analysis;
        this.pref = pref;
    }

    @Override
    public void run() {
        boolean quoteStrings = pref.getBoolean("quote_strings", false);
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file));
            String[] line, tmp;
            int size;
            Instance raw;
            HierarchicalResult result = analysis.getResult();
            if (result != null) {
                Clustering<Cluster> clust = result.getClustering();
                for (Cluster<? extends Instance> c : clust) {
                    for (Instance inst : c) {
                        line = inst.getName().split(",");
                        for (int i = 0; i < line.length; i++) {
                            line[i] = line[i].trim();
                        }
                        size = line.length + 1;

                        int dataSize = 0;
                        String[] data = null;
                        if (pref.getBoolean("raw_data", false)) {
                            raw = inst;
                            while (raw.getAncestor() != null) {
                                raw = raw.getAncestor();
                            }

                            data = raw.toStringArray();
                            dataSize = data.length;
                            size += dataSize;
                        }

                        int preprocessSize = 0;
                        String[] prepro = null;
                        if (pref.getBoolean("preprocess_data", false)) {
                            prepro = inst.toStringArray();
                            preprocessSize = prepro.length;
                            size += preprocessSize;
                        }

                        //append cluster label
                        tmp = new String[size];
                        System.arraycopy(line, 0, tmp, 0, line.length);
                        if (dataSize > 0) {
                            System.arraycopy(data, 0, tmp, line.length, dataSize);
                        }
                        if (preprocessSize > 0) {
                            System.arraycopy(prepro, 0, tmp, line.length + dataSize, preprocessSize);
                        }

                        line = tmp;
                        line[size - 1] = c.getName();
                        writer.writeNext(line, quoteStrings);
                    }
                }
            } else {
                throw new RuntimeException("no clustering result. did you run clustering?");
            }
            writer.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
