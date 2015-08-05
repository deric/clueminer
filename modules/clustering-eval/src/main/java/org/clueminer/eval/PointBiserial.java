package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * In statistics the point-biserial coefficient is a correlation measure between
 * a continuous variable A and a binary variable B.
 *
 * Adapted from point biserial correlation evaluator (Brogden 1949)
 *
 * @cite Mitligan, G. W. (1981a). A Monte Carlo study of thirty internal
 * criterion measures for cluster analysis. Psychometrika, 46, 187-199.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class PointBiserial extends AbstractEvaluator {

    private static String NAME = "PointBiserial";
    private static final long serialVersionUID = -3222061698654228829L;

    public PointBiserial() {
        dm = EuclideanDistance.getInstance();
    }

    public PointBiserial(DistanceMeasure dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Simplified computation of PointBiserial index
     *
     * @param clusters
     * @param params
     * @return
     */
    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        double nw = numW(clusters);
        double nt = numT(clusters);
        double nb = nt - nw;
        double sw = 0.0, sb;

        //sum of within cluster distances
        for (Cluster clust : clusters) {
            sw += sumWithin(clust);
        }
        //sum of between cluster distances
        sb = sumBetween(clusters);

        return (sw / nw - sb / nb) * Math.sqrt(nw * nb) / nt;
    }

    @Override
    public boolean isBetter(double score1, double score2) {
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
