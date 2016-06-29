/*
 * Copyright (C) 2011-2016 clueminer.org
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

import java.lang.reflect.Array;
import java.util.Arrays;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.neighbor.KNNSearch;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.sort.MaxHeap;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 * @param <T>
 */
@ServiceProvider(service = KNNSearch.class)
public class CachingKNN<T extends Instance> implements KNNSearch<T> {

    public static final String NAME = "caching k-nn";

    private Dataset<T> dataset;

    private Distance dm;

    /**
     * Whether to exclude query object self from the neighborhood.
     */
    private boolean identicalExcluded = true;

    public CachingKNN() {
        this.dm = EuclideanDistance.getInstance();
    }

    public CachingKNN(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Neighbor<T>[] knn(T q, int k) {
        KnnCache<Neighbor<T>> cache = KnnCache.getInstance();
        Neighbor<T>[] res;
        if (dataset == null) {
            throw new RuntimeException("dataset not set");
        }
        if (q == null) {
            throw new RuntimeException("q not set");
        }
        if (cache.contains(dataset, q.getIndex())) {
            res = cache.get(dataset, q.getIndex());
            if (res.length < k) {
                res = updateNN(q.getIndex(), k, cache);
            }
        } else {
            res = updateNN(q.getIndex(), k, cache);
        }
        return res;
    }

    private Neighbor<T>[] updateNN(int idx, int k, KnnCache cache) {
        Neighbor<T>[] res = computeNN(idx, k, dm);
        cache.put(dataset, idx, res);
        return res;
    }

    private Neighbor<T>[] computeNN(int idx, int k, Distance dm) {
        Instance q = dataset.get(idx);
        double dist;

        Neighbor<T> neighbor = new Neighbor<>(null, 0, Double.MAX_VALUE);
        @SuppressWarnings("unchecked")
        Neighbor<T>[] neighbors = (Neighbor<T>[]) Array.newInstance(neighbor.getClass(), k);
        MaxHeap<Neighbor<T>> heap = new MaxHeap<>(neighbors);
        for (int i = 0; i < k; i++) {
            heap.add(neighbor);
            neighbor = new Neighbor<>(null, 0, Double.MAX_VALUE);
        }

        for (int i = 0; i < dataset.size(); i++) {
            if (q.equals(dataset.get(i)) && identicalExcluded) {
                continue;
            }

            dist = dm.measure(q, dataset.get(i));
            //replace smallest value in the heap
            Neighbor<T> datum = heap.peek();
            if (dm.compare(dist, datum.distance)) {
                datum.distance = dist;
                datum.index = i;
                datum.key = (T) dataset.get(i);
                heap.heapify();
            }
        }

        heap.sort();
        Arrays.sort(neighbors);
        return neighbors;

    }

    public Dataset<? extends Instance> getDataset() {
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
    public Neighbor[] knn(T q, int k, Props params) {
        String dmProvider = params.get(Algorithm.DISTANCE, "Euclidean");
        this.dm = DistanceFactory.getInstance().getProvider(dmProvider);
        return knn(q, k);
    }

    @Override
    public void setDataset(Dataset<T> dataset) {
        this.dataset = dataset;
    }

    @Override
    public Neighbor<T> nearest(T q) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
