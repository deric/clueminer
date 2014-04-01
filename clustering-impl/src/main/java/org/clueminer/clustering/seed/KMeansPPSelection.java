package org.clueminer.clustering.seed;

import org.clueminer.clustering.api.SeedSelection;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.math.Vector;

/**
 *
 * http://www.stanford.edu/~darthur/kMeansPlusPlus.pdf : k-means++: The
 * Advantages of Careful Seeding
 *
 * @author Tomas Barton
 */
public class KMeansPPSelection extends AbstractSelection implements SeedSelection {

    private static final String name = "k-means++ selection";
    private DistanceMeasure dm = new EuclideanDistance();

    @Override
    public String getName() {
        return name;
    }

    public DistanceMeasure getDistanceMeasure() {
        return dm;
    }

    public void setDistanceMeasure(DistanceMeasure dm) {
        this.dm = dm;
    }

    @Override
    public int[] selectIntIndices(Dataset<? extends Instance> dataset, int k) {
        int[] indices = new int[k];
        //Initial random point
        indices[0] = rand.nextInt(dataset.size());

        double[] closestDist = new double[dataset.size()];
        double sqrdDistSum = 0.0;
        double newDist;

        for (int j = 1; j < k; j++) {
            //Compute the distance from each data point to the closest mean
            Vector newMean = dataset.get(indices[j - 1]);//Only the most recently added mean needs to get distances computed.
            for (int i = 0; i < dataset.size(); i++) {
                newDist = dm.measure(newMean, dataset.get(i));

                if (newDist < closestDist[i] || j == 1) {
                    newDist *= newDist;
                    sqrdDistSum -= closestDist[i];//on inital, -= 0  changes nothing. on others, removed the old value
                    sqrdDistSum += newDist;
                    closestDist[i] = newDist;
                }
            }

            //Choose new x as weighted probablity by the squared distances
            double rndX = rand.nextDouble() * sqrdDistSum;
            double searchSum = 0;
            int i = -1;
            while (searchSum < rndX && i < dataset.size() - 1) {
                searchSum += closestDist[++i];
            }

            indices[j] = i;
        }
        return indices;
    }

}
