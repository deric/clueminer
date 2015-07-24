package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class Tau extends Gamma {

    private static String NAME = "Tau";
    private static final long serialVersionUID = 7019129875909018702L;

    public Tau() {
        dm = EuclideanDistance.getInstance();
    }

    public Tau(DistanceMeasure dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        double nw = numW(clusters);
        double nt = numT(clusters);
        double nb = nt - nw;

        Sres s = computeSTable(clusters);
        return (s.plus - s.minus) / Math.sqrt(nb * nw * (nt * (nt - 1) / 2.0));
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        // should be maximized
        return score1 > score2;
    }

    @Override
    public boolean isMaximized() {
        return true;
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
