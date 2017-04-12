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
package org.clueminer.export.evolution;

import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.gui.EvolutionExport;
import org.clueminer.export.impl.AbstractExporter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = EvolutionExport.class)
public class EvolutionCsvExporter extends AbstractExporter implements EvolutionExport {

    private static final String name = "CSV";
    private CsvEvolutionOptions options;
    private Evolution evolution;
    private static final String EXT = ".csv";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JPanel getOptions() {
        if (options == null) {
            options = new CsvEvolutionOptions();
        }
        return options;
    }

    @Override
    public String getExtension() {
        return ".csv";
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
                    return file.isDirectory() || filename.endsWith(EXT);
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
    public Runnable getRunner(File file, Evolution evolution, Preferences pref, ProgressHandle ph) {
        return new EvolutionCsvRunner(file, evolution, pref, ph);
    }

    @Override
    public void setEvolution(Evolution e) {
        this.evolution = e;
    }

    @Override
    public boolean hasData() {
        return evolution != null;
    }

    @Override
    public Runnable getRunner(File file, Preferences pref, ProgressHandle ph) {
        return getRunner(file, evolution, pref, ph);
    }

}
