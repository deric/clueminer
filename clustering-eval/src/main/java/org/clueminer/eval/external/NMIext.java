package org.clueminer.eval.external;

import com.google.common.collect.Sets;
import java.util.Set;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.NMI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class NMIext extends NMI implements ExternalEvaluator {

    /**
     * We want to compare two clusterings to evaluate how similar they are
     *
     * @param c1
     * @param c2
     * @return
     */
    @Override
    public double score(Clustering<Cluster> c1, Clustering<Cluster> c2) {
        double nmi = 0.0;
        if (c1.size() == 0 || c2.size() == 0) {
            return nmi;
        }
        int instancesCnt = c1.instancesCount();

        if (c1.instancesCount() != c2.instancesCount()) {
            throw new RuntimeException("clusterings have different numbers of instances");
        }

        double c1entropy = entropy(c1.instancesCount(), c1.clusterSizes());
        double c2entropy = entropy(c2.instancesCount(), c2.clusterSizes());

        double mutualInformation = 0;
        int common;
        for (Cluster<Instance> a : c1) {
            final int clusterSize = a.size();
            for (Cluster<Instance> b : c2) {
                Set<Instance> intersection = Sets.intersection(a, b);
                common = intersection.size();
                //System.out.println("a = " + a.getName() + ", b = " + b.getName());
                //System.out.println("common = " + common);

                if (common > 0) {
                    mutualInformation += (common / (double) instancesCnt)
                            * Math.log(instancesCnt
                                    * common / (double) (clusterSize * b.size()));
                }
            }
        }

        nmi = mutualInformation / ((c1entropy + c2entropy) / 2);

        return nmi;
    }

}
