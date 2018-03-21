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
package org.clueminer.clustering.seed;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.clueminer.clustering.ClusterHelper;
import org.clueminer.clustering.api.SeedSelection;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Select randomly unique data points from the dataset.
 *
 * @author Tomas Barton
 * @param <E> data type
 */
@ServiceProvider(service = SeedSelection.class)
public class RandomMedoidsSelection<E extends Instance> extends AbstractSelection implements SeedSelection<E> {

    private static final String NAME = "random";

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Select k random indexes from dataset <0, |dataset|>
     *
     * @param dataset
     * @param params
     * @return selected prototypes
     */
    @Override
    public E[] selectPrototypes(Dataset<E> dataset, Props params) {
        int k = params.getInt("k");
        E[] prototypes = (E[]) new Instance[k];
        setRandom(ClusterHelper.initSeed(params));
        IntSet indicies = new IntOpenHashSet(k);
        int index;

        if (k > dataset.size()) {
            throw new RuntimeException("k (= " + k + ") can't be bigger that |dataset| == " + dataset.size());
        }

        //Keep sampling, we can't use the same point twice.
        while (indicies.size() < k) {
            index = rand.nextInt(dataset.size());
            //TODO create method to do uniform sampling for a select range
            indicies.add(index);
        }

        int j = 0;
        for (Integer i : indicies) {
            prototypes[j++] = dataset.get(i);
        }
        return prototypes;
    }

}
