package org.clueminer.clustering.aggl;

import org.clueminer.clustering.algorithm.HClustResult;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 * SLINK clustering - also known as nearest neighbour clustering (a variant of
 * hierarchical clustering algorithm with single linkage)
 *
 * R. Sibson (1973). "SLINK: an optimally efficient algorithm for the
 * single-link cluster method". The Computer Journal (British Computer Society)
 * 16 (1): 30–34. doi:10.1093/comjnl/16.1.30.
 *
 * @author Tomas Barton
 */
public class SLINK extends AbstractClusteringAlgorithm implements AgglomerativeClustering {

    public static final String name = "SLINK";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Clustering<Cluster> cluster(Matrix matrix, Props props) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HierarchicalResult hierarchy(Dataset<? extends Instance> dataset, Props pref) {
        AgglParams params = new AgglParams(pref);
        Integer minclusters = null;
        int[] processed = new int[dataset.size()];
        //storage for distances
        double[] m = new double[dataset.size()];

        //pi = new HashMap<>(dataset.size());
        //lambda = new HashMap<>(dataset.size());
        int[] pi = new int[dataset.size()];
        double[] lambda = new double[dataset.size()];
        DistanceMeasure dm = params.getDistanceMeasure();

        int i = 0, id;
        for (Instance inst : dataset) {
            id = inst.getIndex();
            step1(id, lambda, pi);
            step2(id, processed, i, dm, m, dataset);
            step3(id, processed, i, dm, lambda, pi, m);
            step4(id, processed, i, lambda, pi);
            processed[i] = id;
            i++;
        }

        //we don't need m anymore
        m = null;

        // Build clusters identified by their target object
        int minc = minclusters != null ? minclusters : dataset.size();

        HierarchicalResult result = new HClustResult(dataset);


        return result;
    }

    @Override
    public HierarchicalResult hierarchy(Matrix input, Dataset<? extends Instance> dataset, Props pref) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HierarchicalResult hierarchy(Matrix matrix, Props props) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void step1(int id, double[] lambda, int[] pi) {
        //pi.put(id, id);
        //lambda.put(id, Double.POSITIVE_INFINITY);
        // P(n+1) = n+1:
        pi[id] = id;
        // L(n+1) = infinity
        lambda[id] = Double.POSITIVE_INFINITY;
    }

    private void step2(int newId, int[] processed, int i, DistanceMeasure dm, double[] m, Dataset<? extends Instance> dataset) {
        for (int j = 0; j < i; j++) {
            // M(i) = dist(i, n+1)
            m[j] = dm.measure(dataset.get(newId), dataset.get(processed[j]));
        }

    }

    private void step3(int newId, int[] processed, int i, DistanceMeasure dm, double[] lambda, int[] pi, double[] m) {
        double l_i, m_i, mp_i;
        int p_i, id;
        for (int j = 0; j < i; j++) {
            id = processed[j];
            l_i = lambda[id];
            m_i = m[id];
            p_i = pi[id];
            mp_i = m[p_i];

            // if L(i) >= M(i)
            //TODO: replace by dm.compare
            if (l_i >= m_i) {
                // M(P(i)) = min { M(P(i)), L(i) }
                m[p_i] = Math.min(mp_i, l_i);

                // L(i) = M(i)
                lambda[id] = m_i;

                // P(i) = n+1;
                pi[id] = newId;
            } else {
                // M(P(i)) = min { M(P(i)), M(i) }
                m[p_i] = Math.min(mp_i, m_i);
            }
        }
    }

    /**
     * Update clusters if it is necessary
     *
     * @param newId
     * @param processed
     * @param i
     * @param lambda
     * @param pi
     */
    private void step4(int newId, int[] processed, int i, double[] lambda, int[] pi) {
        int id;
        double l_i, lp_i;
        for (int j = 0; j < i; j++) {
            id = processed[j];
            l_i = lambda[id];
            lp_i = lambda[pi[id]];
            // if L(i) >= L(P(i))
            if (l_i >= lp_i) {
                // P(i) = n+1
                pi[id] = newId;
            }
        }
    }

    private void extractClusters(Dataset<? extends Instance> dataset, double[] lambda, int[] pi, int minclusters) {
        int[] order;


    }

}
