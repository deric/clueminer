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

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        double dw = 0, fw = 0;
        double db = 0, fb = 0;
        double nd, sd, pb;

        Dataset first, second;
        Instance x, y;
        for (int i = 0; i < clusters.size(); i++) {
            first = clusters.get(i);
            for (int j = 0; j < first.size(); j++) {
                x = first.instance(j);
                // calculate sum of intra cluster distances dw and count their
                // number.
                for (int k = j + 1; k < first.size(); k++) {
                    y = first.instance(k);
                    double distance = dm.measure(x, y);
                    dw += distance;
                    fw++;
                }
                // calculate sum of inter cluster distances dw and count their
                // number.
                for (int k = i + 1; k < clusters.size(); k++) {
                    second = clusters.get(k);
                    for (int l = 0; l < second.size(); l++) {
                        y = second.instance(l);
                        double distance = dm.measure(x, y);
                        db += distance;
                        fb++;
                    }
                }
            }
        }
        // calculate total number of distances
        nd = fw + fb;
        // calculate mean dw and db
        double meanDw = dw / fw;
        double meanDb = db / fb;
        // calculate standard deviation of all distances (sum inter and intra)
        double tmpSdw = 0, tmpSdb = 0;
        double distance;
        for (int i = 0; i < clusters.size(); i++) {
            first = clusters.get(i);
            for (int j = 0; j < first.size(); j++) {
                x = first.instance(j);
                for (int k = j + 1; k < first.size(); k++) {
                    y = first.instance(k);
                    distance = dm.measure(x, y);
                    tmpSdw += (distance - meanDw) * (distance - meanDw);
                }
                for (int k = i + 1; k < clusters.size(); k++) {
                    second = clusters.get(k);
                    for (int l = 0; l < second.size(); l++) {
                        y = second.instance(l);
                        distance = dm.measure(x, y);
                        tmpSdb += (distance - meanDb) * (distance - meanDb);
                    }
                }
            }
        }
        sd = Math.sqrt((tmpSdw + tmpSdb) / nd);
        // calculate point biserial score
        pb = (meanDb - meanDw) * Math.sqrt(((fw * fb) / (nd * nd))) / sd;
        return pb;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        return score(clusters, dataset);
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
