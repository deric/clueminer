package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.stats.AttrNumStats;
import org.clueminer.utils.DatasetTools;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Lower value of SD index means better clustering
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class SDindex extends AbstractEvaluator {

    private static final long serialVersionUID = 4323522308319865590L;
    private static final String name = "SD index";

    public SDindex() {
        dm = EuclideanDistance.getInstance();
    }

    public SDindex(DistanceMeasure dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        Dataset<? extends Instance> dataset = clusters.getLookup().lookup(Dataset.class);
        if (dataset == null) {
            throw new RuntimeException("missing dataset");
        }
        //compute intra dataset variance of whole dataset
        double datasetVar = 0.0, dev;
        double scattering = 0.0;
        double dissimilarity = 0.0;
        Attribute attr;
        for (int d = 0; d < dataset.attributeCount(); d++) {
            attr = dataset.getAttribute(d);
            dev = attr.statistics(AttrNumStats.STD_DEV);
            datasetVar += dev * dev; //norm over all attributes
        }
        datasetVar = Math.sqrt(datasetVar);

        Instance[] centroids = new Instance[clusters.size()];
        for (int i = 0; i < clusters.size(); i++) {
            centroids[i] = DatasetTools.average(clusters.get(i));
        }

        for (int i = 0; i < clusters.size(); i++) {
            scattering += clusterVariance(clusters.get(i), centroids[i]) / datasetVar;
        }

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double dist;
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = 0; j != i; j++) {
                dissimilarity += 1.0 / dm.measure(centroids[i], centroids[j]);
                //just reducing number of loops
                if (j < i) {
                    dist = dm.measure(centroids[i], centroids[j]);
                    if (dist > max) {
                        max = dist;
                    }
                    if (dist < min) {
                        min = dist;
                    }
                }
            }
        }
        dissimilarity *= max / min;

        scattering = scattering / clusters.size();

        /**
         * @TODO times custom coefficient alpha
         */
        return scattering + dissimilarity;
    }

    private double clusterVariance(Dataset clust, Instance centroid) {
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
