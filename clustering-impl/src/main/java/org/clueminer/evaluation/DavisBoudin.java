package org.clueminer.evaluation;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.math.Matrix;

/**
 * Davis-Boudin index
 *
 * the value of the DB index between [0, infinity) zero being a sign for a good
 * cluster
 *
 * @cite Davies, David L., and Donald W. Bouldin. "A cluster separation
 * measure." Pattern Analysis and Machine Intelligence, IEEE Transactions on 2
 * (1979): 224-227.
 *
 * @author Tomas Barton
 */
public class DavisBoudin extends ClusterEvaluator {

    private static final long serialVersionUID = -6973489229802690101L;
    private static String name = "Dunn index";

    public DavisBoudin() {
        dm = new EuclideanDistance();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset) {
        double db = 0;
        Cluster<Instance> x, y;
        Instance centroidX;
        for (int i = 0; i < clusters.size(); i++) {
            x = clusters.get(i);
            centroidX = x.getCentroid();
            /**
             * @todo implement
             */
            for (int j = i + 1; j < clusters.size(); j++) {
                y = clusters.get(j);
                
            }
            
        }
            
        return db;
    }

    @Override
    public double score(Clustering clusters, Dataset dataset, Matrix proximity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean compareScore(double score1, double score2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
