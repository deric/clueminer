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
 * This index represents an adaptation of Goodman and Kruskal's
 * Gamma statistic for use in a clustering situation (Baker & Hubert, 1975).
 * The index is computed as [ s(+) - s(-) ] / [ s(+) + s(-) ] where s(+)
 * represents the number of consistent comparisons involving between and
 * within cluster distances, and s(-) represents the number of inconsistent
 * outcomes (Milligan, 1981a). Maximum values were taken to represent
 * the correct hierarchy level.
 *
 * @cite F. B. Baker and L. J. Hubert. Measuring the power of hierarchical
 * cluster analysis. Journal of the American Statistical Association, 70:31–38,
 * 1975
 *
 * @author Tomas Barton
 *
 */
@ServiceProvider(service = InternalEvaluator.class)
public class Gamma extends AbstractEvaluator {

    private static final String NAME = "Gamma";
    private static final long serialVersionUID = 4782242459481724512L;
    public static final String S_PLUS = "s+";
    public static final String S_MINUS = "s-";

    public Gamma() {
        dm = EuclideanDistance.getInstance();
    }

    public Gamma(DistanceMeasure dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        Sres s = computeSTable(clusters);
        // calculate gamma
        return (s.plus - s.minus) / (double) (s.plus + s.minus);
    }

    public Sres computeSTable(Clustering<? extends Cluster> clusters) {
        Instance x, y;
        Cluster c;
        double distance;
        Sres s = new Sres();

        if (clusters.hasValidation(S_PLUS) && clusters.hasValidation(S_MINUS)) {
            s.plus = (int) clusters.getValidation(S_PLUS);
            s.minus = (int) clusters.getValidation(S_MINUS);
        } else {
            int numWClustPairs = numW(clusters);
            double[] iw = new double[numWClustPairs];
            int l = 0;
            // calculate max intra cluster distance
            for (int i = 0; i < clusters.size(); i++) {
                c = clusters.get(i);
                for (int j = 0; j < c.size() - 1; j++) {
                    x = c.instance(j);
                    for (int k = j + 1; k < c.size(); k++) {
                        y = c.instance(k);
                        distance = dm.measure(x, y);
                        iw[l++] = distance;
                    }
                }
            }
            betweenDistance(clusters, iw, s);
        }
        return s;
    }

    protected void betweenDistance(Clustering<? extends Cluster> clusters, double[] withinDist, Sres s) {
        Instance x, y;
        Cluster a, b;
        double distance;
        for (int i = 0; i < clusters.size(); i++) {
            a = clusters.get(i);
            for (int j = 0; j < a.size(); j++) {
                x = a.instance(j);
                for (int k = i + 1; k < clusters.size(); k++) {
                    b = clusters.get(k);
                    for (int l = 0; l < b.size(); l++) {
                        y = b.instance(l);
                        distance = dm.measure(x, y);
                        for (int m = 0; m < withinDist.length; m++) {
                            //we don't count cases when distance is the same
                            if (distance < withinDist[m]) {
                                s.plus++;
                            }
                            if (distance > withinDist[m]) {
                                s.minus++;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Should be maximized
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean isBetter(double score1, double score2) {
        // should be maximized. range = [-1,1]
        return score1 > score2;
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

    @Override
    public double getMin() {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double getMax() {
        return Double.POSITIVE_INFINITY;
    }

    public class Sres {

        public int plus;
        public int minus;

        public Sres() {
            plus = 0;
            minus = 0;
        }
    }

}
