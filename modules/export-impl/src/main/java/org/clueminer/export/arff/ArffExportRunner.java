/*
 * Copyright (C) 2011-2015 clueminer.org
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.io.ARFFWriter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
class ArffExportRunner<E extends Instance, C extends Cluster<E>> implements Runnable {

    private final File file;
    private final Clustering<E, C> clustering;
    private final Preferences pref;
    private final ProgressHandle ph;

    public ArffExportRunner(File file, Clustering<E, C> clustering, Preferences pref, ProgressHandle ph) {
        this.file = file;
        this.clustering = clustering;
        this.pref = pref;
        this.ph = ph;
    }

    @Override
    public void run() {
        Dataset<E> dataset;
        try (ARFFWriter writer = new ARFFWriter(new FileWriter(file))) {
            if (clustering != null) {
                dataset = clustering.getLookup().lookup(Dataset.class);
                ph.start(dataset.size());
                int cnt = 0;
                writer.writeHeader(dataset, labels(clustering));
                E inst;
                //this might be relatively expensive, but we keep same order as
                //in case of original dataset (easier to diff)
                for (int i = 0; i < dataset.size(); i++) {
                    inst = dataset.get(i);
                    writer.write(inst, instLabel(inst, clustering));
                }
                ph.progress(cnt++);
                writer.close();
            } else {
                throw new RuntimeException("missing mapping");
            }

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private String instLabel(E inst, Clustering<E, C> clustering) {
        Cluster c = clustering.assignedCluster(inst);
        if (c.isOutlier()) {
            return c.getName();
        }
        return String.valueOf(c.getClusterId());
    }

    private String[] labels(Clustering<E, C> clustering) {
        String[] res = new String[clustering.size()];
        Cluster c;
        for (int i = 0; i < clustering.size(); i++) {
            c = clustering.get(i);
            if (c.isOutlier()) {
                res[i] = c.getName();
            } else {
                res[i] = String.valueOf(c.getClusterId());
            }
        }
        return res;
    }

}
