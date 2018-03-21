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
package org.clueminer.export.arff;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.export.impl.ClusteringExporter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.lookup.ServiceProvider;
import org.clueminer.clustering.gui.ClusteringExportGui;

/**
 * Export clustering into ARFF format with cluster assignment as labels
 *
 * @author deric
 */
@ServiceProvider(service = ClusteringExportGui.class)
public class ArffExporter extends ClusteringExporter implements ActionListener, ClusteringExportGui {

    public static final String title = "Export to ARFF (cluster number as label)";
    public static final String EXT = ".arff";

    @Override
    public String getName() {
        return title;
    }

    @Override
    public void updatePreferences(Preferences p) {
        //nothing to do yet.
    }

    @Override
    public JPanel getOptions() {
        return new JPanel();
    }

    @Override
    public FileFilter getFileFilter() {
        if (fileFilter == null) {
            fileFilter = new FileFilter() {

                @Override
                public boolean accept(File file) {
                    String filename = file.getName();
                    return file.isDirectory() || filename.endsWith(".arff");
                }

                @Override
                public String getDescription() {
                    return "ARFF (*.arff)";
                }
            };
        }
        return fileFilter;
    }

    @Override
    public String getExtension() {
        return EXT;
    }

    @Override
    public Runnable getRunner(File file, DendrogramMapping mapping, Preferences pref, ProgressHandle ph) {
        if (mapping != null) {
            return new ArffExportRunner(file, mapping.getRowsClustering(), pref, ph);
        }
        return new ArffExportRunner(file, clustering, pref, ph);
    }

    @Override
    public boolean hasData() {
        return clustering != null;
    }

}
