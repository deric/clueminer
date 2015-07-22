package org.clueminer.eval;

import java.util.Arrays;
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
        int n = clusters.instancesCount();
        int numPairs = (n * (n - 1)) / 2;
        int numWClustPairs = 0;
        for (Cluster clust : clusters) {
            numWClustPairs += (clust.size() * clust.size() - n);
        }

        numWClustPairs /= 2;
        //pessimistic size guess
        double[] dist = new double[3 * numWClustPairs];
        int l = 0;
        for (Cluster clust : clusters) {
            minDw = Double.MAX_VALUE;
            maxDw = Double.MIN_VALUE;
            for (int i = 0; i < clust.size(); i++) {
                x = clust.instance(i);
                for (int j = 0; j < i; j++) {
                    y = clust.instance(j);
                    distance = dm.measure(x, y);
                    if (!Double.isNaN(distance)) {
                        dist[l++] = distance;
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
         //   minSum += minDw;
            //   maxSum += maxDw;
        }
        double tmp[] = new double[l];
        System.arraycopy(dist, 0, tmp, 0, l);
        dist = tmp;

        System.out.println("numPairs = " + numPairs);
        System.out.println("numClustPairs = " + numWClustPairs);
        System.out.println("assigned = " + l);
        Arrays.sort(dist);
        System.out.println("first: " + dist[0]);
        System.out.println("last: " + dist[l - 1]);

        for (int i = 0; i < numWClustPairs; i++) {
            minSum += dist[i];
        }

        for (int i = numWClustPairs; i < l; i++) {
            maxSum += dist[i];
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
