/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering.seed;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.clueminer.clustering.api.SeedSelection;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.ListUtils;
import org.clueminer.utils.Props;
import org.clueminer.utils.SystemInfo;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * http://www.stanford.edu/~darthur/kMeansPlusPlus.pdf : k-means++: The
 * Advantages of Careful Seeding
 *
 * @author Tomas Barton
 * @param <E>
 */
@ServiceProvider(service = SeedSelection.class)
public class KMeansPPSelection<E extends Instance> extends AbstractSelection implements SeedSelection<E> {

    private static final String NAME = "k-means++";
    private Distance dm = new EuclideanDistance();

    @Override
    public String getName() {
        return NAME;
    }

    public Distance getDistanceMeasure() {
        return dm;
    }

    public void setDistanceMeasure(Distance dm) {
        this.dm = dm;
    }

    @Override
    public E[] selectPrototypes(Dataset<E> dataset, Props params) {
        int k = params.getInt("k");
        E[] prototypes = (E[]) new Instance[k];
        //Initial random point
        prototypes[0] = dataset.get(rand.nextInt(dataset.size()));

        double[] closestDist = new double[dataset.size()];
        double sqrdDistSum = 0.0;
        double newDist;

        for (int j = 1; j < k; j++) {
            //Compute the distance from each data point to the closest mean
            E newMean = prototypes[j - 1];//Only the most recently added mean needs to get distances computed.
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

            prototypes[j] = dataset.get(i);
        }
        return prototypes;
    }

    public E[] selectPrototypes(final Dataset<E> dataset, Props params, ExecutorService threadpool) {
        int k = params.getInt("k");
        E[] prototypes = (E[]) new Instance[k];
        //Initial random point
        prototypes[0] = dataset.get(rand.nextInt(dataset.size()));

        final double[] closestDist = new double[dataset.size()];
        double sqrdDistSum = 0.0;

        //Each future will return the local chance to the overal sqared distance.
        List<Future<Double>> futureChanges = new ArrayList<>(SystemInfo.LogicalCores);

        for (int j = 1; j < k; j++) {
            //Compute the distance from each data point to the closest mean
            final E newMean = prototypes[j - 1];//Only the most recently added mean needs to get distances computed.
            futureChanges.clear();

            int blockSize = dataset.size() / SystemInfo.LogicalCores;
            int extra = dataset.size() % SystemInfo.LogicalCores;
            int pos = 0;
            while (pos < dataset.size()) {
                final int from = pos;
                final int to = Math.min(pos + blockSize + (extra-- > 0 ? 1 : 0), dataset.size());
                pos = to;
                final boolean forceCompute = j == 1;
                Future<Double> future = threadpool.submit(() -> {
                    double sqrdDistChanges = 0.0;
                    for (int i = from; i < to; i++) {
                        double newDist = dm.measure(newMean, dataset.get(i));

                        if (newDist < closestDist[i] || forceCompute) {
                            newDist *= newDist;
                            sqrdDistChanges -= closestDist[i];//on inital, -= 0  changes nothing. on others, removed the old value
                            sqrdDistChanges += newDist;
                            closestDist[i] = newDist;
                        }
                    }

                    return sqrdDistChanges;
                });

                futureChanges.add(future);
            }
            try {
                for (Double change : ListUtils.collectFutures(futureChanges)) {
                    sqrdDistSum += change;
                }
            } catch (ExecutionException | InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }

            //Choose new x as weighted probablity by the squared distances
            double rndX = rand.nextDouble() * sqrdDistSum;
            double searchSum = 0;
            int i = -1;
            while (searchSum < rndX && i < dataset.size() - 1) {
                searchSum += closestDist[++i];
            }

            prototypes[j] = dataset.get(i);
        }
        return prototypes;
    }

}
