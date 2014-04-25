package org.clueminer.clustering.algorithm;

import java.util.Arrays;
import java.util.prefs.Preferences;
import org.clueminer.clustering.HardAssignment;
import org.clueminer.clustering.api.Assignment;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.PartitioningClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.AlgorithmParameters;

/**
 * Partitioning Around Medoids (PAM) - the most common realization of k-medoid
 * clustering
 *
 * @author Tomas Barton
 */
public class PAM extends KClustererBase implements PartitioningClustering {

    private static final String name = "PAM";
    protected int repeats = 1;
    protected int iterLimit = 100;

    @Override
    public String getName() {
        return name;
    }

    protected double cluster(Dataset<? extends Instance> dataset, int[] medioids, Assignment assignments) {
        double totalDistance = 0.0;
        int changes;
        int[] bestMedCand = new int[medioids.length];
        double[] bestMedCandDist = new double[medioids.length];
        double dist;
        Instance current;
        int iter = 0;

        do {
            changes = 0;
            totalDistance = 0.0;

            for (int i = 0; i < dataset.size(); i++) {
                int assign = 0;
                current = dataset.get(i);
                double minDist = distanceMeasure.measure(dataset.get(medioids[0]), current);

                for (int k = 1; k < medioids.length; k++) {
                    dist = distanceMeasure.measure(dataset.get(medioids[k]), current);
                    if (dist < minDist) {
                        minDist = dist;
                        assign = k;
                    }
                }

                //Update which cluster it is in
                if (assignments.assigned(i) != assign) {
                    changes++;
                    assignments.assign(i, assign);
                }
                totalDistance += minDist * minDist;
            }

            //Update the medioids
            Arrays.fill(bestMedCandDist, Double.MAX_VALUE);
            for (int i = 0; i < dataset.size(); i++) {
                double currCandidateDist = 0.0;
                int clusterId = assignments.assigned(i);
                Instance medCandadate = dataset.get(i);
                for (int j = 0; j < dataset.size(); j++) {
                    if (j == i || assignments.assigned(j) != clusterId) {
                        continue;
                    }
                    currCandidateDist += Math.pow(distanceMeasure.measure(medCandadate, dataset.get(j)), 2);
                }

                if (currCandidateDist < bestMedCandDist[clusterId]) {
                    bestMedCand[clusterId] = i;
                    bestMedCandDist[clusterId] = currCandidateDist;
                }
            }
            System.arraycopy(bestMedCand, 0, medioids, 0, medioids.length);
        } while (changes > 0 && iter++ < iterLimit);

        return totalDistance;
    }

    /**
     * TODO: make an interface from this (maybe we could use different
     * estimations methods)
     *
     * @param dataset
     * @return
     */
    public int guessK(Dataset<? extends Instance> dataset) {
        return (int) Math.sqrt(dataset.size() / 2);
    }

    @Override
    public Clustering<Cluster> partition(Dataset<? extends Instance> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clustering<Cluster> partition(Dataset<? extends Instance> dataset, AlgorithmParameters params) {
        Assignment assignments = new HardAssignment(dataset.size());
        int[] medioids = selectMedioids(dataset);

        return null;
    }

    @Override
    public Clustering<Cluster> cluster(Matrix matrix, Preferences props) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clustering<Cluster> cluster(Dataset<? extends Instance> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private int[] selectMedioids(Dataset<? extends Instance> dataset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
