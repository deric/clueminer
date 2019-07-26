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
package org.clueminer.clustering.algorithm.cure;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Random;
import org.apache.commons.math3.util.FastMath;
import org.clueminer.clustering.api.Configurator;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 * @param <E>
 */
public class CUREConfig<E extends Instance> implements Configurator<E> {

    private static CUREConfig instance;

    protected Distance distanceFunction;
    protected int n;
    protected int k;
    protected int minRepresentativeCount;
    protected double shrinkFactor;
    protected double representationProbablity;
    protected int numPartitions;
    protected int reduceFactor;

    protected int currentRepAdditionCount;
    protected IntOpenHashSet blacklist;
    protected Random random;
    protected int clusterCnt;
    protected DendroNode[] nodes;
    protected ColorGenerator colorGenerator;
    protected CureCluster<E> outliers;

    public CUREConfig() {

    }

    public static CUREConfig getInstance() {
        if (instance == null) {
            instance = new CUREConfig();
        }
        return instance;
    }

    @Override
    public void configure(Dataset<E> dataset, Props params) {
        params.putInt(CURE.K, (int) Math.sqrt(dataset.size()));
        if (!params.containsKey(CURE.SHRINK_FACTOR)) {
            params.putDouble(CURE.SHRINK_FACTOR, 0.5);
        }
    }

    @Override
    public double estimateRunTime(Dataset<E> dataset, Props params) {
        return dataset.size() * Math.log(FastMath.pow(dataset.size(), 3) * dataset.attributeCount());
    }

}
