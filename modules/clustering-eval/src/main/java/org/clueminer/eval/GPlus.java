package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Based on similar concepts as Gamma index.
 *
 * @cite F. J. Rohlf. Methods of comparing classifications. Annual Review of
 * Ecology and Systematics, 5:101–113, 1974.
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class GPlus extends Gamma {

    private static final String NAME = "G+";
    private static final long serialVersionUID = 558399535473028351L;

    public GPlus() {
        dm = EuclideanDistance.getInstance();
    }

    public GPlus(DistanceMeasure dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        //double nw = numW(clusters);
        double nt = numT(clusters);

        Sres s = computeSTable(clusters);
        double gPlus = (2 * s.minus) / (nt * (nt - 1));
        return gPlus;
    }

    /**
     * Should be minimized
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean isBetter(double score1, double score2) {
        // should be minimized: range = [0,x] with x= fb/nd
        return score1 < score2;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

    @Override
    public double getMin() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getMax() {
        return 0;
    }
}
