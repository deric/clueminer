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
package org.clueminer.knn;

import java.util.List;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.neighbor.RNNSearch;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 * @param <T>
 */
@ServiceProvider(service = RNNSearch.class)
public class LinearRNN<T extends Instance> extends AbstractKNN<T> implements RNNSearch<T> {

    public static final String name = "linear RNN";

    public LinearRNN() {
        this.dm = EuclideanDistance.getInstance();
    }

    public LinearRNN(Dataset<T> dataset) {
        this.dataset = dataset;
        this.dm = EuclideanDistance.getInstance();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void range(T q, double radius, List<Neighbor<T>> neighbors) {
        if (radius <= 0.0) {
            throw new IllegalArgumentException("Invalid radius: " + radius);
        }

        for (int i = 0; i < dataset.size(); i++) {
            if (q.getIndex() == dataset.get(i).getIndex() && identicalExcluded) {
                continue;
            }

            //filter out noise
            if (exclude != null && exclude.contains(i)) {
                continue;
            }

            double d = dm.measure(q, dataset.get(i));

            if (d <= radius) {
                neighbors.add(new Neighbor<>((T) dataset.get(i), i, d));
            }
        }
    }

}
