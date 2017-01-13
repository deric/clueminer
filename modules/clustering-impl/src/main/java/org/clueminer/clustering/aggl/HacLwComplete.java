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
package org.clueminer.clustering.aggl;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 * Over-optimized version of hierarchical clustering with complete linkage
 * algorithm, instead of using general Lance-Williams formula, simple max
 * function is used
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class HacLwComplete<E extends Instance, C extends Cluster<E>> extends HCLW<E, C> implements AgglomerativeClustering<E, C> {

    private final static String NAME = "HAC-LW-Complete";

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Complete linkage is computed as maximum distance between two clusters
     *
     * @param r
     * @param q
     * @param a
     * @param b
     * @param sim
     * @param linkage
     * @param cache
     * @param ma
     * @param mb
     * @param mq
     * @return
     */
    @Override
    public double updateProximity(int r, int q, int a, int b, Matrix sim,
            ClusterLinkage linkage, Int2DoubleMap cache,
            int ma, int mb, int mq) {
        double dist = Math.max(fetchDist(a, q, sim, cache), fetchDist(b, q, sim, cache));
        cache.put(map(r, q), dist);
        return dist;
    }

    @Override
    protected void checkParams(Props props) {
        if (!props.get(AlgParams.LINKAGE).equals("Complete")) {
            throw new RuntimeException(getName() + " algorithm does not support linkage: " + props.get(AlgParams.LINKAGE));
        }
    }

    @Override
    public boolean isLinkageSupported(String linkage) {
        return linkage.equals("Complete");
    }
}
