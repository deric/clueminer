/*
 * Copyright (C) 2015 clueminer.org
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

import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.distance.api.Distance;
import org.clueminer.distance.api.KNN;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = KNN.class)
public class CachingKNN implements KNN {

    private static final String name = "caching k-nn";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int[] nnIds(int idx, int k, Dataset<? extends Instance> dataset, Props params) {
        Instance[] nn = nn(idx, k, dataset, params);
        int[] res = new int[k];
        for (int i = 0; i < k; i++) {
            res[i] = nn[i].getIndex();
        }
        return res;
    }

    @Override
    public Instance[] nn(int idx, int k, Dataset<? extends Instance> dataset, Props params) {
        KnnCache cache = KnnCache.getInstance();
        Instance[] res;
        if (cache.contains(dataset, idx)) {
            res = cache.get(dataset, idx);
            if (res.length < k) {
                res = updateNN(dataset, idx, k, cache, params);
            }
        } else {
            res = updateNN(dataset, idx, k, cache, params);
        }
        return res;
    }

    private Instance[] updateNN(Dataset<? extends Instance> dataset, int idx, int k, KnnCache cache, Props params) {
        String dmProvider = params.get(AbstractClusteringAlgorithm.DISTANCE, "Euclidean");
        Distance dm = DistanceFactory.getInstance().getProvider(dmProvider);
        Instance[] res = computeNN(dataset, idx, k, dm);
        cache.put(dataset, idx, res);
        return res;
    }

    private Instance[] computeNN(Dataset<? extends Instance> dataset, int idx, int k, Distance dm) {
        Instance target = dataset.get(idx);
        ForgetingQueue queue = new ForgetingQueue(k, dm, target);

        for (int i = 0; i < dataset.size(); i++) {
            if (i != idx) {
                queue.check(dataset.get(i));
            }
        }
        return queue.getResult();
    }
}
