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
package org.clueminer.knn;

import java.util.List;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.neighbor.KNNSearch;
import org.clueminer.neighbor.NearestNeighborSearch;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.neighbor.RNNSearch;

/**
 *
 * @author deric
 * @param <T>
 */
public class LinearSearch<T extends Instance> implements NearestNeighborSearch<T>, KNNSearch<T>, RNNSearch<T> {

    private Dataset<? extends Instance> dataset;

    private Distance dist;

    /**
     * Whether to exclude query object self from the neighborhood.
     */
    private boolean identicalExcluded = true;

    public LinearSearch() {
        this.dist = EuclideanDistance.getInstance();
    }

    public LinearSearch(Dataset<T> dataset) {
        this.dataset = dataset;
        this.dist = EuclideanDistance.getInstance();
    }

    @Override
    public Neighbor<T> nearest(T q) {
        T neighbor = null;
        int index = -1;
        double max = Double.MAX_VALUE;
        for (int i = 0; i < dataset.size(); i++) {
            if (q == dataset.get(i) && identicalExcluded) {
                continue;
            }

            double d = dist.measure(q, dataset.get(i));

            if (d < max) {
                neighbor = (T) dataset.get(i);
                index = i;
                max = d;
            }
        }

        return new Neighbor<>(neighbor, index, max);
    }

    @Override
    public Neighbor[] knn(T q, int k) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void range(T q, double radius, List<Neighbor<T>> neighbors) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    public void setDataset(Dataset<? extends Instance> dataset) {
        this.dataset = dataset;
    }

    public boolean isIdenticalExcluded() {
        return identicalExcluded;
    }

    public void setIdenticalExcluded(boolean identicalExcluded) {
        this.identicalExcluded = identicalExcluded;
    }

    public void setDistanceMeasure(Distance dm) {
        this.dist = dm;
    }

}
