package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * The C-index was reviewed in Hubert and Levin (1976). It is computed as
 *
 * C_index = [d_w - min(d_w)] / [max(d_w) - min(d_w)],
 *
 * where d_w is the sum of the within cluster distances. The index was found to
 * exhibit excellent recovery characteristics by Milligan (1981a). The minimum
 * value across the hierarchy levels was used to indicate the optimal number of
 * clusters
 *
 * @cite L. Hubert and J. Schultz. Quadratic assignment as a general
 * data-analysis strategy. British Journal of Mathematical and Statistical
 * Psychologie, 29:190–241, 1976.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class CIndex extends AbstractEvaluator {

    private static final long serialVersionUID = -4725798362682980138L;
    private static String NAME = "C-index";

    public CIndex() {
        dm = EuclideanDistance.getInstance();
    }

    public CIndex(DistanceMeasure dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        double dw = 0;
        double minDw, maxDw;
        double minSum = 0.0, maxSum = 0.0;

        Instance x, y;
        // calculate intra cluster distances and sum of all
        //for each cluster
        double distance;
        for (Cluster clust : clusters) {
            minDw = Double.MAX_VALUE;
            maxDw = Double.MIN_VALUE;
            for (int i = 0; i < clust.size(); i++) {
                x = clust.instance(i);
                for (int j = 0; j < i; j++) {
                    y = clust.instance(j);
                    distance = dm.measure(x, y);
                    if (!Double.isNaN(distance)) {
                        dw += distance;
                        //max distance between any pair in the same cluster (in entire dataset)
                        if (distance > maxDw) {
                            maxDw = distance;
                        }
                        //min distance between any pair in the same cluster (in entire dataset)
                        if (distance < minDw) {
                            minDw = distance;
                        }
                    }
                }
            }
            minSum += minDw;
            maxSum += maxDw;
        }

        // calculate C Index
        double cIndex = (dw - minSum) / (maxSum - minSum);
        return cIndex;
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        // should be minimized ( smallest intra cluster distances)
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
