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
package org.clueminer.knn;

import java.util.HashSet;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.neighbor.NearestNeighborSearch;
import org.clueminer.neighbor.Neighbor;

/**
 *
 * @author deric
 * @param <T>
 */
public abstract class AbstractKNN<T extends Instance> implements NearestNeighborSearch<T> {

    /**
     * Whether to exclude query object self from the neighborhood.
     */
    protected boolean identicalExcluded = true;

    protected Dataset<T> dataset;

    protected Distance dm;

    protected HashSet<Integer> exclude;

    public Dataset<T> getDataset() {
        return dataset;
    }

    @Override
    public boolean isIdenticalExcluded() {
        return identicalExcluded;
    }

    @Override
    public void setIdenticalExcluded(boolean identicalExcluded) {
        this.identicalExcluded = identicalExcluded;
    }

    @Override
    public void setDistanceMeasure(Distance dm) {
        this.dm = dm;
    }

    @Override
    public void setDataset(Dataset<T> dataset) {
        this.dataset = dataset;
    }

    @Override
    public void setExclude(HashSet<Integer> exclude) {
        this.exclude = exclude;
    }

    @Override
    public Neighbor<T> nearest(T q) {
        T neighbor = null;
        int index = -1;
        double max = Double.MAX_VALUE;
        double d;
        for (int i = 0; i < dataset.size(); i++) {
            if (q == dataset.get(i) && identicalExcluded) {
                continue;
            }

            d = dm.measure(q, dataset.get(i));
            if (d < max) {
                neighbor = (T) dataset.get(i);
                index = i;
                max = d;
            }
        }

        return new Neighbor<>(neighbor, index, max);
    }

}
