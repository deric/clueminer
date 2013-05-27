package org.clueminer.evaluation.external;

import org.clueminer.clustering.api.ExternalEvaluator;
import com.google.common.collect.BiMap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import java.util.Map;
import java.util.Set;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;

/**
 * @see http://en.wikipedia.org/wiki/Fowlkes%E2%80%93Mallows_index
 * @author Tomas Barton
 */
public class FowlkesMallows extends ExternalEvaluator {

    private static final long serialVersionUID = 101045082257039885L;
    private static String name = "Fowlkes-Mallows";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        BiMap<String, String> matching = CountingPairs.findMatching(table);
        Map<String, Integer> res;

        int tp, fp, fn;
        double index = 0.0;
        double fowles;
        //for each cluster we have score of quality
        for (String cluster : matching.values()) {
            res = CountingPairs.countAssignments(table, matching.inverse().get(cluster), cluster);
            tp = res.get("tp");
            fp = res.get("fp");
            fn = res.get("fn");
            fowles = tp * Math.sqrt((tp + fp) * (tp + fn));
            index += fowles;
        }

        //average value
        return index / clusters.size();
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        return score(clusters, dataset);
    }
    
    /**
     * Should be maximized
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean compareScore(double score1, double score2) {
        return (score1 > score2);
    }

    @Override
    public double score(Clustering<Cluster> c1, Clustering<Cluster> c2) {
        double index = 0;

        int instancesCnt = c1.instancesCount();

        if (c1.instancesCount() != c2.instancesCount()) {
            throw new RuntimeException("clusterings have different numbers of instances");
        }

        int common;
        for (Cluster<Instance> a : c1) {
            final int clusterSize = a.size();
            for (Cluster<Instance> b : c2) {
                Set<Instance> intersection = Sets.intersection(a, b);
                common = intersection.size();
                //System.out.println("a = " + a.getName() + ", b = " + b.getName());
                //System.out.println("common = " + common);

                if (common > 0) {
                    index += (common / (double) instancesCnt)
                            * Math.log(instancesCnt
                            * common / (double) (clusterSize * b.size()));
                }
            }
        }
        return index;
    }
}
