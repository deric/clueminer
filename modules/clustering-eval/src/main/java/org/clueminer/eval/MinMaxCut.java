package org.clueminer.eval;

import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ClusterEvaluator.class)
public class MinMaxCut extends ClusterEvaluator {

    private static final String NAME = "min-max cut";
    private static final long serialVersionUID = -4963722097900153865L;

    public MinMaxCut() {
        dm = EuclideanDistance.getInstance();
    }

    public MinMaxCut(DistanceMeasure dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {

        Dataset a, b;
        double sum = 0;
        for (int i = 0; i < clusters.size(); i++) {
            double tmpTop = 0;
            double tmp = 0;
            a = clusters.get(i);
            for (int j = 0; j < a.size(); j++) {
                for (int k = 0; k < clusters.size(); k++) {
                    b = clusters.get(k);
                    for (int p = 0; p < b.size(); p++) {
                        if (a.instance(j) != b.instance(p)) {
                            double error = dm.measure(a.instance(j), b.instance(p));
                            tmpTop += error;
                        }
                    }
                }
                for (int k = 0; k < a.size(); k++) {
                    double error = dm.measure(a.instance(j), a.instance(k));
                    tmp += error;
                }
            }
            double tmpSum = tmpTop / tmp;
            sum += tmpSum;
        }
        return sum;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean compareScore(double score1, double score2) {
        // should be minimized
        return score1 < score2;
    }
}
