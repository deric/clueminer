package org.clueminer.eval;

import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.CosineDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.clueminer.utils.DatasetTools;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusterEvaluator.class)
public class SumOfCentroidSimilarities extends ClusterEvaluator {

    private static String NAME = "Sum of Centroid Similarities";
    private static final long serialVersionUID = -2323688637159800449L;

    public SumOfCentroidSimilarities() {
        dm = new CosineDistance();
    }

    public SumOfCentroidSimilarities(DistanceMeasure dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        Instance[] centroids = new Instance[clusters.size()];
        for (int i = 0; i < clusters.size(); i++) {
            centroids[i] = DatasetTools.average(clusters.get(i));
        }
        double sum = 0;
        Dataset c;
        for (int i = 0; i < clusters.size(); i++) {
            c = clusters.get(i);
            for (int j = 0; j < c.size(); j++) {
                double error = dm.measure(c.instance(j), centroids[i]);
                sum += error;
            }
        }
        return sum;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean compareScore(double score1, double score2) {
        // should be minimized -- probably not, doesnt work
        return score1 > score2;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }
}
