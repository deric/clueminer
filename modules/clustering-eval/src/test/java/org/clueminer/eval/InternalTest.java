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
package org.clueminer.eval;

import java.util.Random;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.impl.ArrayDataset;

/**
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class InternalTest<E extends Instance, C extends Cluster<E>> {

    public Instance next(Random rand, InstanceBuilder<? extends Instance> builder, String klass) {
        return builder.create(new double[]{rand.nextDouble(), rand.nextDouble()}, klass);
    }

    public Clustering<E, C> oneClassPerCluster() {
        Clustering<E, C> oneClass = new ClusterList<>(3);
        int size = 10;
        Random rand = new Random();
        Dataset<E> data = new ArrayDataset<>(size, 2);
        data.attributeBuilder().create("x1", "NUMERIC");
        data.attributeBuilder().create("x2", "NUMERIC");

        for (int i = 0; i < size; i++) {
            Instance inst = next(rand, data.builder(), "same class");
            //cluster with single class
            BaseCluster<E> clust = new BaseCluster<>(1);
            clust.setAttributes(data.getAttributes());
            clust.add(inst);
            oneClass.add((C) clust);
        }
        oneClass.lookupAdd(data);
        return oneClass;
    }

}
