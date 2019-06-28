/*
 * Copyright (C) 2011-2019 clueminer.org
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
package org.clueminer.evolution.gui;

import java.io.File;
import java.util.prefs.Preferences;
import org.clueminer.clustering.gui.ExporterGUI;
import org.clueminer.evolution.api.Evolution;
import org.netbeans.api.progress.ProgressHandle;

/**
 * Interface for exporting evolution results
 *
 * @author Tomas Barton
 */
public interface EvolutionExport extends ExporterGUI {

    /**
     * Creates Runnable object for performing the export
     *
     * @param file
     * @param evolution
     * @param pref
     * @param ph
     * @return
     */
    Runnable getRunner(File file, Evolution evolution, Preferences pref, final ProgressHandle ph);


    /**
     * Evolution is used for obtaining data
     *
     * @param evolution
     */
    void setEvolution(Evolution evolution);
}
