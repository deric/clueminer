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
package org.clueminer.clustering.aggl;

import java.util.PriorityQueue;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AgglClusteringTest {

    private static final Distance DM = EuclideanDistance.getInstance();
    private static final double DELTA = 1e-9;

    /**
     * Compare results of parallel implementation with the serial one
     */
    @Test
    public void testRowSimilarityMatrixParSym() {
        Dataset<? extends Instance> dataset = FakeClustering.schoolData();

        Matrix input = dataset.asMatrix();
        int triangle = ((dataset.size() - 1) * dataset.size()) >>> 1;
        PriorityQueue<Element> pq = new PriorityQueue<>(triangle);
        int threads = 8;
        Matrix parSim = AgglClustering.rowSimilarityMatrixParSym(input, DM, pq, threads);
        //parSim.printLower(5, 2);
        pq = new PriorityQueue<>(triangle);
        //make sure paralell version returns same results as the serial one
        Matrix ref = AgglClustering.rowSimilarityMatrix(input, DM, pq);
        //ref.printLower(5, 2);

        for (int i = 0; i < ref.rowsCount(); i++) {
            for (int j = 0; j < ref.columnsCount(); j++) {
                //System.out.println("[" + i + ", " + j + "] = " + ref.get(i, j) + " vs. " + parSim.get(i, j));
                assertEquals(ref.get(i, j), parSim.get(i, j), DELTA);
            }
        }
    }

    @Test
    public void testRowSimilarityMatrixParSymLock() {
        Dataset<? extends Instance> dataset = FakeClustering.schoolData();

        Matrix input = dataset.asMatrix();
        int triangle = ((dataset.size() - 1) * dataset.size()) >>> 1;
        PriorityQueue<Element> pq = new PriorityQueue<>(triangle);
        int threads = 16;
        Matrix parSim = AgglClustering.rowSimilarityMatrixParSymLock(input, DM, pq, threads);
        //parSim.printLower(5, 2);
        pq = new PriorityQueue<>(triangle);
        //make sure paralell version returns same results as the serial one
        Matrix ref = AgglClustering.rowSimilarityMatrix(input, DM, pq);
        //ref.printLower(5, 2);

        for (int i = 0; i < ref.rowsCount(); i++) {
            for (int j = 0; j < ref.columnsCount(); j++) {
                //System.out.println("[" + i + ", " + j + "] = " + ref.get(i, j) + " vs. " + parSim.get(i, j));
                assertEquals(ref.get(i, j), parSim.get(i, j), DELTA);
            }
        }
    }

    @Test
    public void testColumnSimilarityMatrix() {
        Dataset<? extends Instance> dataset = FakeClustering.schoolData();
        Matrix sim = AgglClustering.columnSimilarityMatrix(dataset.asMatrix(), DM);
        assertEquals(4, sim.rowsCount());
        assertEquals(4, sim.columnsCount());
    }
}
