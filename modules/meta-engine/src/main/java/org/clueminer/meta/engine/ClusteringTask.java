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
package org.clueminer.meta.engine;

import java.util.concurrent.Callable;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Executor;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.clueminer.utils.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container for task execution
 *
 * @author deric
 */
public class ClusteringTask<E extends Instance, C extends Cluster<E>> implements Callable<Clustering<E, C>> {

    private Executor exec;
    private final Dataset<E> dataset;
    private final Props conf;
    private final long timeLimit;
    private static final Logger LOG = LoggerFactory.getLogger(ClusteringTask.class);


    public ClusteringTask(Dataset<E> dataset, Props conf, long timeLimit) {
        this.dataset = dataset;
        this.conf = conf;
        this.timeLimit = timeLimit;
    }

    @Override
    public Clustering<E, C> call() throws Exception {
        Clustering<E, C> c;

        StopWatch time = new StopWatch(true);
        c = exec.clusterRows(dataset, conf);
        time.endMeasure();
        c.lookupAdd(time);
        LOG.info("{} clustering finished in {}", conf.get(AlgParams.ALG), time.formatSec());
        cleanUp(c);

        return c;
    }

    /**
     * Remove empty clusters
     *
     * @param clustering
     * @return
     */
    private Clustering<E, C> cleanUp(Clustering<E, C> clustering) {
        for (int i = 0; i < clustering.size(); i++) {
            Cluster<E> c = clustering.get(i);
            if (c.isEmpty()) {
                clustering.remove(i);
            }
        }
        return clustering;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setExecutor(Executor executor) {
        this.exec = executor;
    }

}
