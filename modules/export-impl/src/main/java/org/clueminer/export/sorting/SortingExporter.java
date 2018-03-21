/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.export.sorting;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.io.File;
import java.util.Collection;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.export.api.ClusteringExport;
import org.clueminer.export.impl.AbstractExporter;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public class SortingExporter<E extends Instance> extends AbstractExporter implements ClusteringExport<E> {

    public static final String title = "Export to CSV";
    public static final String ext = ".csv";
    private SortingOptions options;
    private Object2DoubleOpenHashMap<String> results;
    private Dataset<? extends Instance> dataset;
    private Collection<? extends Clustering> clusterings;
    private ClusterEvaluation evaluator;

    public SortingExporter() {
    }

    @Override
    public JPanel getOptions() {
        if (options == null) {
            options = new SortingOptions();
        }
        return options;
    }

    @Override
    public String getName() {
        return title;
    }


    @Override
    public void updatePreferences(Preferences p) {
        options.updatePreferences(p);
    }


    @Override
    public FileFilter getFileFilter() {
        if (fileFilter == null) {
            fileFilter = new FileFilter() {

                @Override
                public boolean accept(File file) {
                    String filename = file.getName();
                    return file.isDirectory() || filename.endsWith(ext);
                }

                @Override
                public String getDescription() {
                    return "CSV (*.csv)";
                }
            };
        }
        return fileFilter;
    }

    @Override
    public String getExtension() {
        return ext;
    }

    @Override
    public boolean hasData() {
        return results != null;
    }

    @Override
    public Runnable getRunner(File file, Preferences pref, ProgressHandle ph) {
        return new SortingRunner(file, this, pref, ph);
    }

    public Object2DoubleOpenHashMap<String> getResults() {
        return results;
    }

    public void setResults(Object2DoubleOpenHashMap<String> results) {
        this.results = results;
    }

    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    public void setDataset(Dataset<E> dataset) {
        this.dataset = dataset;
    }

    public void setClusterings(Collection<? extends Clustering> clusterings) {
        this.clusterings = clusterings;
    }

    public Collection<? extends Clustering> getClusterings() {
        return clusterings;
    }

    public void setReference(ClusterEvaluation evaluator) {
        this.evaluator = evaluator;
    }

    public ClusterEvaluation getEvaluator() {
        return evaluator;
    }
}
