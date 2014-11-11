package org.clueminer.eval;

import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.distance.CosineDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Matrix;
import org.clueminer.utils.DatasetTools;
import org.openide.util.lookup.ServiceProvider;

/**
 * * E_1 from the Zhao 2001 paper
 *
 * Distance measure has to be CosineSimilarity
 *
 * @author Andreas De Rijcke
 * @author Tomas Barton
 */
@ServiceProvider(service = InternalEvaluator.class)
public class TraceScatterMatrix extends AbstractEvaluator {

    private static String NAME = "Trace Scatter Matrix";
    private static final long serialVersionUID = -3714149292456837484L;

    public TraceScatterMatrix() {
        dm = new CosineDistance();
    }

    public TraceScatterMatrix(DistanceMeasure dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        Instance[] clusterCentroid = new Instance[clusters.size()];
        Instance overAllCentroid;
        int[] clusterSizes = new int[clusters.size()];

        // calculate centroids of each cluster
        for (int i = 0; i < clusters.size(); i++) {
            clusterCentroid[i] = DatasetTools.average(clusters.get(i));
            clusterSizes[i] = clusters.get(i).size();
        }

        // calculate centroid all instances
        // firs put all cluster back together
        Dataset data = new SampleDataset();
        data.setAttributes(dataset.getAttributes());
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = 0; j < clusters.get(i).size(); j++) {
                data.add(clusters.get(i).instance(j));
            }
        }
        overAllCentroid = DatasetTools.average(data);
        // calculate trace of the between-cluster scatter matrix.
        double sum = 0;
        for (int i = 0; i < clusters.size(); i++) {
            double cos = dm.measure(clusterCentroid[i], overAllCentroid);
            sum += cos * clusterSizes[i];
        }
        return sum;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        // should be minimalized
        return Math.abs(score1) < Math.abs(score2);
    }

    @Override
    public void setDistanceMeasure(DistanceMeasure dm) {
        throw new UnsupportedOperationException("Should use cosine distance.");
    }

    @Override
    public boolean isMaximized() {
        return false;
    }
}
