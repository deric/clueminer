package org.clueminer.eval;

import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.CountingPairs;
import org.clueminer.math.Matrix;
import org.openide.util.lookup.ServiceProvider;

/**
 * Normalized Mutual Information
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusterEvaluator.class)
public class NMI extends ClusterEvaluator implements ClusterEvaluation {

    private static final long serialVersionUID = -480979241137671097L;
    private static final String name = "NMI";

    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @param count total number of elements N (in whole dataset)
     * @param elements
     * @return
     */
    //this is cool but hard to cast...
    // private double entropy(int count, Integer... elements)
    protected double entropy(int count, int[] elements) {
        double entropy = 0;
        double pk;
        for (int d : elements) {
            if (d != 0) {
                pk = d / (double) count;
                entropy += pk * Math.log(pk);
            }
        }
        return -entropy;
    }

    /**
     * Computes score against class label (must be provided)
     *
     * @param clusters
     * @param dataset
     * @return
     */
    @Override
    public double score(Clustering<? extends Cluster> clusters, Dataset<? extends Instance> dataset) {
        double nmi = 0.0;
        if (clusters.size() == 0) {
            return nmi;
        }

        int instancesCnt = clusters.instancesCount();
        Table<String, String, Integer> table = CountingPairs.contingencyTable(clusters);
        Map<String, Integer> res;
        double c1entropy = entropy(clusters.instancesCount(), clusters.clusterSizes());


        Map<String, Integer> klassSizes = new HashMap<String, Integer>(table.columnKeySet().size());

        double mutualInformation = 0;
        int common;
        int klassSize;
        for (String klass : table.columnKeySet()) {
            for (String cluster : table.rowKeySet()) {
                //has some assignments of class to a given cluster
                if (table.get(cluster, klass) != null) {
                    res = CountingPairs.countAssignments(table, klass, cluster);
                    if (klassSizes.containsKey(klass)) {
                        klassSize = klassSizes.get(klass);
                    } else {
                        klassSize = res.get("tp") + res.get("fp");
                        klassSizes.put(klass, klassSize);
                    }
                    //System.out.println("klass size = " + klassSize);

                    int clusterSize = res.get("tp") + res.get("fn");
                    //itersection is number of true positives
                    common = res.get("tp");
                    //System.out.println("a = " + klass + ", b = " + cluster);
                    //System.out.println("common = " + common);

                    if (common > 0) {
                        mutualInformation += (common / (double) instancesCnt)
                                * Math.log(instancesCnt
                                * common / (double) (klassSize * clusterSize));
                    }
                }

            }
        }
        int[] clusterSizes = new int[klassSizes.size()];
        int i = 0;
        for (String key : klassSizes.keySet()) {
            clusterSizes[i++] = klassSizes.get(key);
        }

        double classEntropy = entropy(dataset.size(), clusterSizes);

        nmi = mutualInformation / ((c1entropy + classEntropy) / 2);

        return nmi;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        return score(clusters, dataset);
    }

    /**
     * Should be maximized, maximum value is 1.0
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean compareScore(double score1, double score2) {
        return score1 > score2;
    }
}
