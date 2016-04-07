package org.clueminer.export.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.csv.CSVWriter;
import org.clueminer.dataset.api.Instance;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class CsvExportRunner<E extends Instance, C extends Cluster<E>> implements Runnable {

    private final File file;
    private final DendrogramMapping<E, C> mapping;
    private final Preferences pref;
    private final ProgressHandle ph;
    private boolean includeClass = false;

    public CsvExportRunner(File file, DendrogramMapping mapping, Preferences pref, ProgressHandle ph) {
        this.file = file;
        this.mapping = mapping;
        this.pref = pref;
        this.ph = ph;
    }

    @Override
    public void run() {
        boolean quoteStrings = pref.getBoolean("quote_strings", false);
        char separator = pref.get("separator", ",").charAt(0);

        try (CSVWriter writer = new CSVWriter(new FileWriter(file), separator)) {
            String[] line, tmp;
            int size;
            Instance raw;
            //TODO: allow exporting columns result
            HierarchicalResult<E, C> result = mapping.getRowsResult();
            if (result != null) {
                //number of items in dataset must be same as number of instances in clusters
                ph.start(result.getDataset().size());
                Clustering<E, C> clust = result.getClustering();
                int cnt = 0;
                for (Cluster<E> c : clust) {
                    for (E inst : c) {
                        line = inst.getName().split(",");
                        for (int i = 0; i < line.length; i++) {
                            line[i] = line[i].trim();
                        }
                        //+name
                        size = line.length + 1;
                        //class attr
                        if (includeClass) {
                            size += 1;
                        }

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
                        int offsetClass = 0;
                        if (includeClass) {
                            offsetClass = 1;
                            //prepend class value
                            tmp[0] = (String) inst.classValue();
                        }
                        System.arraycopy(line, 0, tmp, offsetClass, line.length);
                        if (dataSize > 0) {
                            System.arraycopy(data, 0, tmp, line.length, dataSize);
                        }
                        if (preprocessSize > 0) {
                            System.arraycopy(prepro, 0, tmp, line.length + dataSize, preprocessSize);
                        }

                        line = tmp;
                        line[size - 1] = String.valueOf(c.getClusterId());
                        writer.writeNext(line, quoteStrings);
                        ph.progress(cnt++);
                    }
                }
            } else {
                throw new RuntimeException("no clustering result. did you run clustering?");
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public boolean isIncludeClass() {
        return includeClass;
    }

    public void setIncludeClass(boolean includeClass) {
        this.includeClass = includeClass;
    }

}
