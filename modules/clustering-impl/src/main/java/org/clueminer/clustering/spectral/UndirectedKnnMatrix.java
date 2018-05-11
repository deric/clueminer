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
package org.clueminer.clustering.spectral;

import java.lang.reflect.Array;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author mikusmi1
 */
@ServiceProvider(service = MatrixConvertor.class)
public class UndirectedKnnMatrix<E extends Instance> implements MatrixConvertor<E> {

    private static final String NAME = "undirected k-neighborhood matrix";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Matrix buildMatrix(Matrix similarityMatrix, Props params) {
        int k = params.getInt("KnnK", 4);

        if (k <= 1) {
            throw new RuntimeException("Number of clusters should be at least 2");
        }

        boolean mutual = params.getBoolean("Mutual", false);

        JMatrix knnMatrix = new JMatrix(similarityMatrix.rowsCount(), similarityMatrix.columnsCount(), 0);

        for (int i = 0; i < similarityMatrix.rowsCount(); i++) {

            double dist;
            Neighbor<Integer> neighbor = new Neighbor<>(null, 0, Double.MIN_VALUE);
            @SuppressWarnings("unchecked")
            Neighbor<Integer>[] neighbors = (Neighbor<Integer>[]) Array.newInstance(neighbor.getClass(), k);

            // Init
            int initProcessIndex = k;
            int addIndex = 0;
            for (int j = 0; j < initProcessIndex; j++) {

                if (i == j) {
                    knnMatrix.set(i, j, 1.0);
                    initProcessIndex++;
                    continue;
                }

                dist = similarityMatrix.get(i, j);
                neighbors[addIndex] = new Neighbor<>(i, j, dist);
                addIndex++;
            }

            HeapSort.sort(neighbors);

            for (int j = initProcessIndex; j < similarityMatrix.columnsCount(); j++) {
                if (i == j) {
                    knnMatrix.set(i, j, 1.0);
                    continue;
                }

                dist = similarityMatrix.get(i, j);

                Neighbor<Integer> smallestVal = neighbors[0];
                if (dist > smallestVal.distance) {
                    smallestVal.distance = dist;
                    smallestVal.index = j;
                    smallestVal.key = (Integer) i;
                    HeapSort.sort(neighbors);
                }
            }

            for (Neighbor<Integer> n : neighbors) {
                knnMatrix.set(n.key, n.index, n.distance);
                if (mutual == false) {
                    knnMatrix.set(n.index, n.key, n.distance);
                }
            }
        }

        if (mutual == true) {
            for (int i = 0; i < knnMatrix.rowsCount(); i++) {
                for (int j = 0; j < knnMatrix.columnsCount(); j++) {
                    if (knnMatrix.get(i, j) == 0.0 && knnMatrix.get(j, i) != 0.0) {
                        knnMatrix.set(i, j, 0.0);
                        knnMatrix.set(j, i, 0.0);
                    }
                }
            }
        }

        return knnMatrix;
    }
}
