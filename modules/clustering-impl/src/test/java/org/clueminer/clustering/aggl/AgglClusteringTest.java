package org.clueminer.clustering.aggl;

import java.util.PriorityQueue;
import org.clueminer.cluster.FakeClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AgglClusteringTest {

    private static final DistanceMeasure dm = new EuclideanDistance();
    private static final double delta = 1e-9;

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
        Matrix parSim = AgglClustering.rowSimilarityMatrixParSym(input, dm, pq, threads);
        //parSim.printLower(5, 2);
        pq = new PriorityQueue<>(triangle);
        //make sure paralell version returns same results as the serial one
        Matrix ref = AgglClustering.rowSimilarityMatrix(input, dm, pq);
        //ref.printLower(5, 2);

        for (int i = 0; i < ref.rowsCount(); i++) {
            for (int j = 0; j < ref.columnsCount(); j++) {
                //System.out.println("[" + i + ", " + j + "] = " + ref.get(i, j) + " vs. " + parSim.get(i, j));
                assertEquals(ref.get(i, j), parSim.get(i, j), delta);
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
        Matrix parSim = AgglClustering.rowSimilarityMatrixParSymLock(input, dm, pq, threads);
        //parSim.printLower(5, 2);
        pq = new PriorityQueue<>(triangle);
        //make sure paralell version returns same results as the serial one
        Matrix ref = AgglClustering.rowSimilarityMatrix(input, dm, pq);
        //ref.printLower(5, 2);

        for (int i = 0; i < ref.rowsCount(); i++) {
            for (int j = 0; j < ref.columnsCount(); j++) {
                //System.out.println("[" + i + ", " + j + "] = " + ref.get(i, j) + " vs. " + parSim.get(i, j));
                assertEquals(ref.get(i, j), parSim.get(i, j), delta);
            }
        }
    }

    @Test
    public void testColumnSimilarityMatrix() {
        Dataset<? extends Instance> dataset = FakeClustering.schoolData();
        Matrix sim = AgglClustering.columnSimilarityMatrix(dataset.asMatrix(), dm);
        assertEquals(4, sim.rowsCount());
        assertEquals(4, sim.columnsCount());
    }
}
