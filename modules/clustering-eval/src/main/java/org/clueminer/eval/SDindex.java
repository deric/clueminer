package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Lower value of SD index means better clustering. The bases of SD validity
 * index are the average scattering of clusters and total separation of
 * clusters. The scattering is calculated by variance of the clusters and
 * variance of the dataset, thus it can measure the homogeneity and compactness
 * of the clusters.
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 *
 * @cite M. Halkidi and M. Vazirgiannis and Y. Batistakis: Quality Scheme
 * Assessment in the Clustering Process, Proc. of the 4th European Conference on
 * Principles of Data Mining and Knowledge Discovery, pp. 265-276, 2000
 *
 * SD index is based on a criterion proposed for fuzzy clustering:
 *
 * @cite Ramze Rezaee, B.P.F. Lelieveldt, J.H.C Reiber. "A new cluster validity
 * index for the fuzzy c-mean", Pattern Recognition Letters, 19, pp237-246,
 * 1998.
 */
@ServiceProvider(service = InternalEvaluator.class)
public class SDindex<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final long serialVersionUID = 4323522308319865590L;
    private static final String name = "SD index";

    public SDindex() {
        dm = EuclideanDistance.getInstance();
    }

    public SDindex(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Sum of variances for each cluster
     *
     * @param clusters
     * @return
     */
    protected double varianceSum(Clustering<E, C> clusters) {
        double varSum = 0.0;

        for (int i = 0; i < clusters.size(); i++) {
            varSum += clusterVariance(clusters.get(i), clusters.get(i).getCentroid());
        }
        return varSum;
    }

    protected double scattering(Clustering<E, C> clusters) {
        //compute intra dataset variance of whole dataset
        double datasetVar = 0.0, var;

        Dataset<E> dataset = clusters.getLookup().lookup(Dataset.class);
        int dim;
        if (dataset == null) {
            dim = clusters.get(0).attributeCount();
        } else {
            dim = dataset.attributeCount();
        }

        for (int d = 0; d < dim; d++) {
            var = attrVar(clusters, d);
            datasetVar += var * var; //norm over all attributes
        }
        // norm of the variance vector
        datasetVar = Math.sqrt(datasetVar);

        return varianceSum(clusters) / (clusters.size() * datasetVar);
    }

    /**
     * Largest and smallest distance between centroids
     *
     * @param cl
     * @return
     */
    protected double dispersion(Clustering<E, C> cl) {
        double dissimilarity = 0.0;
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double dist, sum;
        for (int i = 0; i < cl.size(); i++) {
            sum = 0.0;
            for (int j = 0; j < cl.size(); j++) {
                if (j != i) {
                    dist = dm.measure(cl.get(i).getCentroid(), cl.get(j).getCentroid());
                    sum += dist;
                    //just reducing number of checks
                    if (j < i) {
                        if (dist > max) {
                            max = dist;
                        }
                        if (dist < min) {
                            min = dist;
                        }
                    }
                }
            }
            dissimilarity += 1.0 / sum;
        }
        dissimilarity *= max / min;
        return dissimilarity;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double scatt = scattering(clusters);
        double dis = dispersion(clusters);

        /**
         * The formula should have been
         *
         * sd(C) = alpha * Scatt(|C|) + Dis(|C|)
         *
         * where alpha is a weighting factor equal to Dis(c_max) where c_max is
         * the maximum number of input clusters. In case of crisp clustering
         * c_max is equal to k, thus Dis(c_max) == Dis(k), then we get following
         * formula:
         *
         * sd(C) = Dis(|C|) * (Scatt(|C|) + 1)
         *
         */
        return dis * (scatt + 1);
    }

    private double clusterVariance(Cluster<E> clust, E centroid) {
        double sigma, var, totalVar = 0.0;

        for (int k = 0; k < clust.attributeCount(); k++) {
            var = 0.0;
            for (int j = 0; j < clust.size(); j++) {
                double dist = clust.instance(j).value(k) - centroid.value(k);
                var += dist * dist;

            }
            sigma = var / clust.size();
            totalVar += sigma * sigma;
        }
        return Math.sqrt(totalVar); //a norm
    }

    /**
     * Should be minimized
     *
     * @param score1
     * @param score2
     * @return
     */
    @Override
    public boolean isBetter(double score1, double score2) {
        return (score1 < score2);
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
