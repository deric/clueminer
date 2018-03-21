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
package org.clueminer.plot;

import java.util.Date;
import java.util.HashSet;
import javax.swing.SwingWorker;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Timeseries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.neighbor.KDTree;

/**
 *
 * @author deric
 * @param <E>
 */
public class PlotSearcher<E extends ContinuousInstance> extends SwingWorker<KDTree<E>, Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(PlotSearcher.class);
    private final Timeseries<E> dataset;
    private final HashSet<Integer> instances;
    private final double[] tpd;
    private final int n;

    public PlotSearcher(Timeseries<E> dataset, HashSet<Integer> instances) {
        this.dataset = dataset;
        this.instances = instances;
        this.tpd = dataset.getTimePointsArray();

        //total work items
        n = instances.size() * dataset.attributeCount();
    }

    @Override
    protected KDTree<E> doInBackground() throws Exception {
        KDTree<E> tree;
        LOG.trace("computing KD-tree for {}", dataset.getName());
        double[][] keys = new double[n][2];
        double[] key;
        ContinuousInstance[] data = new ContinuousInstance[n];
        E inst;
        int j = 0;
        for (int idx : instances) {
            inst = dataset.get(idx);
            int i = 0;
            for (Date d : dataset.getTimePointsCollection()) {
                key = new double[2]; //2D coordinates
                key[0] = d.getTime();
                key[1] = inst.get(i++);
                keys[j] = key;
                data[j] = inst;
                LOG.debug("indexing {} -> {}", key, inst);
                j++;
            }
            this.setProgress(((idx + 1) * 100) / n);
        }
        tree = new KDTree(keys, data);
        LOG.trace("finished KD-tree for {}", dataset.getName());
        return tree;
    }

}
