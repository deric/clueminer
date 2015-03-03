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
 * Smallest intra cluster distance
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class WB extends AbstractEvaluator {

    private static String NAME = "WB";
    private static final long serialVersionUID = 5214048863548979381L;

    public WB() {
        dm = EuclideanDistance.getInstance();
    }

    public WB(DistanceMeasure dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        double dw = 0, fw = 0;
        double db = 0, fb = 0;
        double distance;

        Dataset clust, second;
        Instance x, y;
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            for (int j = 0; j < clust.size(); j++) {
                x = clust.instance(j);
                // calculate sum of intra cluster distances dw and count their
                // number.
                for (int k = j + 1; k < clust.size(); k++) {
                    y = clust.instance(k);
                    distance = dm.measure(x, y);
                    dw += distance;
                    fw++;
                }
                // calculate sum of inter cluster distances dw and count their
                // number.
                for (int k = i + 1; k < clusters.size(); k++) {
                    second = clusters.get(k);
                    for (int l = 0; l < second.size(); l++) {
                        y = second.instance(l);
                        distance = dm.measure(x, y);
                        db += distance;
                        fb++;
                    }
                }
            }
        }
        double wb = (dw / fw) / (db / fb);
        return wb;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        return score(clusters, dataset);
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        // should be minimized
        return score1 < score2;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }
}
