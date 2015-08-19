package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * I_1 from the Zhao 2001 paper
 *
 * @author Andreas De Rijcke
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = InternalEvaluator.class)
public class SumOfAveragePairwiseSimilarities<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final long serialVersionUID = 3813005489221333305L;
    private static String NAME = "Sum of AVG parwise similarities";

    public SumOfAveragePairwiseSimilarities() {
        dm = EuclideanDistance.getInstance();
    }

    public SumOfAveragePairwiseSimilarities(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double sum = 0;
        Cluster clust;
        for (int i = 0; i < clusters.size(); i++) {
            double tmpSum = 0;
            clust = clusters.get(i);
            for (int j = 0; j < clust.size(); j++) {
                for (int k = 0; k < clust.size(); k++) {
                    double error = dm.measure(clust.instance(j), clust.instance(k));
                    tmpSum += error;
                }
            }
            sum += tmpSum / clust.size();
        }
        return sum;
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        // TODO check right condition or code
        //should be minimized; in paper: maxed!!
        return score1 < score2;
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
