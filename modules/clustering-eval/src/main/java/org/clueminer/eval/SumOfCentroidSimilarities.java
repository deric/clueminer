package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.CosineDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.utils.DatasetTools;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class SumOfCentroidSimilarities extends AbstractEvaluator {

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
    public double score(Clustering<? extends Cluster> clusters, Props params) {
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
    public boolean isBetter(double score1, double score2) {
        // should be minimized -- probably not, doesnt work
        return score1 > score2;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

    @Override
    public double getMin() {
        return 0;
    }

    @Override
    public double getMax() {
        return Double.POSITIVE_INFINITY;
    }
}
