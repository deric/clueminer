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
package org.clueminer.export.api;

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
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author deric
 * @param <E>
 */
public interface ClusteringExport<E extends Instance> {

    String getName();

    JPanel getOptions();

    void updatePreferences(Preferences p);

    FileFilter getFileFilter();

    String getExtension();

    boolean hasData();

    Runnable getRunner(File file, Preferences pref, ProgressHandle ph);

    void setDataset(Dataset<E> dataset);

    void setClusterings(Collection<? extends Clustering> clusterings);

    void setReference(ClusterEvaluation evaluator);

    ClusterEvaluation getEvaluator();

    void setResults(Object2DoubleOpenHashMap<String> results);

    void showDialog();

}
