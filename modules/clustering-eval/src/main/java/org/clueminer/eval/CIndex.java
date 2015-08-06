package org.clueminer.eval;

import com.google.common.collect.MinMaxPriorityQueue;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
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
 * @cite Dalrymple-Alford, E. C. The measurement of clustering in free recall.
 * Psychological Bulletin, 1970, 75, 32-34.
 *
 * @cite Hubert, L.; Levin, J. A general statistical framework for assessing
 * categorical clustering in free recall. Psychological Bulletin, volume 83, no.
 * 6, 1976: p. 1072.
 *
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
        double minSum = 0.0, maxSum = 0.0;

        Instance x, y;
        // calculate intra cluster distances and sum of all
        //for each cluster
        double distance;
        //int n = clusters.instancesCount();
        //int numTPairs = (n * (n - 1)) / 2;
        int numWClustPairs = 0;
        //number of within pairs
        for (Cluster clust : clusters) {
            numWClustPairs += clust.size() * clust.size();
            //numWClustPairs += (clust.size() * (clust.size() - 1)) / 2;
        }
        numWClustPairs = (numWClustPairs - clusters.instancesCount()) / 2; // - N

        //distances within a cluster
        MinMaxPriorityQueue<Double> pqMax = MinMaxPriorityQueue.create();
        MinMaxPriorityQueue<Double> pqMin = MinMaxPriorityQueue.create();
        for (Cluster clust : clusters) {
            for (int i = 0; i < clust.size(); i++) {
                x = clust.instance(i);
                for (int j = 0; j < i; j++) {
                    y = clust.instance(j);
                    distance = dm.measure(x, y);
                    if (!Double.isNaN(distance)) {
                        dw += distance;
                        //TODO: should we really include within cluster distances
                        checkDistance(distance, numWClustPairs, pqMin, pqMax);
                    }
                }
            }
        }
        //between distances
        Cluster xc, yc;
        for (int i = 0; i < clusters.size(); i++) {
            xc = clusters.get(i);
            for (int m = 0; m < xc.size(); m++) {
                x = xc.instance(m);
                for (int j = 0; j < i; j++) {
                    yc = clusters.get(j);
                    for (int k = 0; k < yc.size(); k++) {
                        y = yc.instance(k);
                        distance = dm.measure(x, y);
                        if (!Double.isNaN(distance)) {
                            checkDistance(distance, numWClustPairs, pqMin, pqMax);
                        }
                    }
                }
            }
        }

        //sum of smallest {numWClustPairs} distances
        for (Double d : pqMin) {
            minSum += d;
        }

        //sum of largest {numWClustPairs} distances
        for (Double d : pqMax) {
            maxSum += d;
        }

        // calculate C Index
        double cIndex = (dw - minSum) / (maxSum - minSum);
        return cIndex;
    }

    /**
     * Check whether distance is either smallest or largest distance in the
     * dataset
     *
     * @param distance
     * @param numWClustPairs
     * @param pqMin
     * @param pqMax
     */
    private void checkDistance(double distance, int numWClustPairs, MinMaxPriorityQueue<Double> pqMin, MinMaxPriorityQueue<Double> pqMax) {
        if (pqMax.size() >= numWClustPairs) {
            if (distance > pqMax.peekFirst()) {
                //remove smallest number
                pqMax.removeFirst();
                pqMax.add(distance);
            }
        } else {
            pqMax.add(distance);
        }
        if (pqMin.size() >= numWClustPairs) {
            if (distance < pqMax.peekLast()) {
                pqMin.add(distance);
            }
        } else {
            pqMin.add(distance);
        }
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
