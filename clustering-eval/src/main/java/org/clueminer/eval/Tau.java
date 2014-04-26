package org.clueminer.eval;

import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusterEvaluator.class)
public class Tau extends ClusterEvaluator {

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
    public double score(Clustering clusters, Dataset dataset) {
        double maxIntraDist[] = new double[clusters.size()];
        double sPlus = 0, sMin = 0;
        double fw = 0, fb = 0;
        double t = 0, nd;

        Dataset first, second;
        Instance x, y;
        for (int i = 0; i < clusters.size(); i++) {
            maxIntraDist[i] = Double.MIN_VALUE;
            first = clusters.get(i);
            for (int j = 0; j < first.size(); j++) {
                x = first.instance(j);
                // calculate intra cluster distances, count their number and
                // find max.
                // count t.
                for (int k = j + 1; k < first.size(); k++) {
                    y = first.instance(k);
                    double distance = dm.measure(x, y);
                    fw++;
                    if (maxIntraDist[i] < distance) {
                        maxIntraDist[i] = distance;
                    }
                    // 2 distances (2 pairs of points): t+1
                    t++;
                }
                // calculate inter cluster distances, count their number and
                // find min.
                // count sPlus, sMin and t.
                for (int k = i + 1; k < clusters.size(); k++) {
                    second = clusters.get(k);
                    for (int l = 0; l < second.size(); l++) {
                        y = second.instance(l);
                        double distance = dm.measure(x, y);
                        fb++;
                        if (distance < maxIntraDist[i]) {
                            sMin++;
                        }
                        // 2 distances (2 pairs of points) compaired: t+1
                        t++;
                        if (distance > maxIntraDist[i]) {
                            sPlus++;
                        }
                        // 2 distances (2 pairs of points) compaired: t+1
                        t++;
                    }
                }
            }
        }
        nd = fw + fb;
        double tau = (sPlus - sMin) / Math.sqrt((nd * (nd - 1) / 2 - t) * (nd * (nd - 1) / 2));
        return tau;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean compareScore(double score1, double score2) {
        // should be maximized
        return score1 > score2;
    }

}
