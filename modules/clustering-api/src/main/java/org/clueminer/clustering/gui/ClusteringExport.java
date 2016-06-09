/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.clustering.gui;

import java.io.File;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public interface ClusteringExport extends ExporterGUI {

    /**
     * Creates Runnable object for performing the export
     *
     * @param file
     * @param mapping
     * @param pref
     * @param ph
     * @return
     */
    Runnable getRunner(File file, DendrogramMapping mapping, Preferences pref, final ProgressHandle ph);

    /**
     * Display exporting options
     */
    void showDialog();

    /**
     * Set graphical component displaying clustering
     *
     * @param analysis
     */
    void setViewer(DendroViewer analysis);

    /**
     * Dendrogram mapping
     *
     * @param mapping
     */
    void setDendrogramMapping(DendrogramMapping mapping);

    /**
     *
     * @param clustering
     */
    void setClustering(Clustering clustering);
}
