package org.clueminer.eval;

import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
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
 * @author Tomas Barton
 *
 */
@ServiceProvider(service = InternalEvaluator.class)
public class Gamma extends AbstractEvaluator {

    private static final String NAME = "Gamma";
    private static final long serialVersionUID = 4782242459481724512L;

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
    public double score(Clustering clusters, Dataset dataset) {
        double maxIntraDist = Double.MIN_VALUE;
        double sPlus = 0, sMinus = 0;

        Instance x, y;
        Dataset a, b;
        // calculate max intra cluster distance
        for (int i = 0; i < clusters.size(); i++) {
            a = clusters.get(i);
            for (int j = 0; j < a.size(); j++) {
                x = a.instance(j);
                for (int k = j + 1; k < a.size(); k++) {
                    y = a.instance(k);
                    double distance = dm.measure(x, y);
                    if (maxIntraDist < distance) {
                        maxIntraDist = distance;
                    }
                }
            }
        }
        // calculate inter cluster distances
        // count sPlus and sMin
        for (int i = 0; i < clusters.size(); i++) {
            a = clusters.get(i);
            for (int j = 0; j < a.size(); j++) {
                x = a.instance(j);
                for (int k = i + 1; k < clusters.size(); k++) {
                    b = clusters.get(k);
                    for (int l = 0; l < b.size(); l++) {
                        y = b.instance(l);
                        double distance = dm.measure(x, y);
                        if (distance < maxIntraDist) {
                            sMinus++;
                        }
                        if (distance > maxIntraDist) {
                            sPlus++;
                        }
                    }
                }
            }
        }
        // calculate gamma
        double gamma = (sPlus - sMinus) / (sPlus + sMinus);
        return gamma;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet.");
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

}
