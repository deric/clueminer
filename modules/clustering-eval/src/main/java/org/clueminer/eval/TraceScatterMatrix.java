/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.CosineDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Trace Scatter Matrix - E_1 from the Zhao 2001 paper
 *
 * Distance measure has to be CosineSimilarity
 *
 * @author Andreas De Rijcke
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = InternalEvaluator.class)
public class TraceScatterMatrix<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static String NAME = "TraceSM";
    private static final long serialVersionUID = -3714149292456837484L;

    public TraceScatterMatrix() {
        dm = new CosineDistance();
    }

    public TraceScatterMatrix(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        Dataset<? extends Instance> dataset = clusters.getLookup().lookup(Dataset.class);
        if (dataset == null) {
            throw new RuntimeException("missing dataset");
        }
        E[] clusterCentroid = (E[]) new Instance[clusters.size()];
        // calculate centroid all instances
        E overAllCentroid = clusters.getCentroid();
        int[] clusterSizes = new int[clusters.size()];

        // calculate centroids of each cluster
        for (int i = 0; i < clusters.size(); i++) {
            clusterCentroid[i] = clusters.get(i).getCentroid();
            clusterSizes[i] = clusters.get(i).size();
        }

        // calculate trace of the between-cluster scatter matrix.
        double sum = 0;
        for (int i = 0; i < clusters.size(); i++) {
            double cos = dm.measure(clusterCentroid[i], overAllCentroid);
            sum += cos * clusterSizes[i];
        }
        return sum;
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        // should be minimalized
        return Math.abs(score1) < Math.abs(score2);
    }

    @Override
    public void setDistanceMeasure(Distance dm) {
        throw new UnsupportedOperationException("Should use cosine distance.");
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

    @Override
    public double getMin() {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double getMax() {
        return Double.POSITIVE_INFINITY;
    }
}
