package org.clueminer.clustering.aggl;

import java.util.HashMap;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 * Over-optimized version of hierarchical clustering with complete linkage
 * algorithm, instead of using general Lance-Williams formula, simple max
 * function is used
 *
 * @author Tomas Barton
 */
public class HacLwComplete extends HACLW implements AgglomerativeClustering {

    private final static String name = "HAC-LW-Complete";

    @Override
    public String getName() {
        return name;
    }

    /**
     * Complete linkage is computed as maximum distance between two clusters
     *
     * @param r
     * @param q
     * @param a
     * @param b
     * @param sim
     * @param linkage
     * @param cache
     * @return
     */
    @Override
    public double updateProximity(int r, int q, int a, int b, Matrix sim,
            ClusterLinkage linkage, HashMap<Integer, Double> cache, int ma, int mb, int mq) {
        double dist = Math.max(fetchDist(a, q, sim, cache), fetchDist(b, q, sim, cache));
        cache.put(map(r, q), dist);
        return dist;
    }

    @Override
    protected void checkParams(Props props) {
        if (!props.get(AgglParams.LINKAGE).equals("Complete Linkage")) {
            throw new RuntimeException(getName() + " algorithm does not support linkage: " + props.get(AgglParams.LINKAGE));
        }
    }

    @Override
    public boolean isLinkageSupported(String linkage) {
        return linkage.equals("Complete Linkage");
    }
}
