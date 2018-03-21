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
package org.clueminer.export.impl;

import java.io.File;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.gui.ClusteringExportGui;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public abstract class ClusteringExporter extends AbstractExporter implements ClusteringExportGui {

    protected DendroViewer viewer;
    protected DendrogramMapping mapping;
    protected Clustering clustering;

    @Override
    public void setViewer(DendroViewer analysis) {
        this.viewer = analysis;
        this.mapping = analysis.getDendrogramMapping();
        this.clustering = analysis.getDendrogramMapping().getColumnsClustering();
    }

    @Override
    public void setDendrogramMapping(DendrogramMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public void setClustering(Clustering clustering) {
        this.clustering = clustering;
    }

    @Override
    public Runnable getRunner(File file, Preferences pref, ProgressHandle ph) {
        return getRunner(file, mapping, pref, ph);
    }

}
