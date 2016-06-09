/*
 * Copyright (C) 2011-2016 clueminer.org
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

import org.apache.commons.math3.util.FastMath;
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
 * @param <E>
 * @param <C>
 * @cite Ratkowsky, D. A., and G. N. Lance. "A criterion for determining the
 * number of groups in a classification." Australian Computer Journal 10.3
 * (1978): 115-117.
 *
 * @author deric
 */
@ServiceProvider(service = InternalEvaluator.class)
public class RatkowskyLance<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final long serialVersionUID = 3195054290041907628L;
    private static String name = "Ratkowsky-Lance";

    public RatkowskyLance() {
        dm = new EuclideanDistance();
    }

    public RatkowskyLance(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double c = 0.0;
        //number of dimensions
        int dim = clusters.get(0).attributeCount();
        for (int i = 0; i < dim; i++) {
            c += bgss(clusters, i) / tss(clusters, i);
        }

        c /= dim;

        return c / Math.sqrt(clusters.size());
    }

    /**
     * Between group dispersion for each attribute
     *
     * @param clusters
     * @param d
     * @return
     */
    private double bgss(Clustering<E, C> clusters, int d) {
        Cluster clust;
        double bgss = 0.0;
        double avg;
        double mu = attrMean(clusters, d);

        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            avg = 0.0;
            for (int j = 0; j < clust.size(); j++) {
                avg += clust.get(j).get(d);
            }
            avg /= clust.size();
            bgss += clust.size() * FastMath.pow(mu - avg, 2);

        }
        return bgss;
    }

    private double tss(Clustering<E, C> clusters, int d) {
        Dataset<E> dataset = clusters.getLookup().lookup(Dataset.class);
        double nvar = attrVar(clusters, d);
        int n;
        //variance for specific attribute
        if (dataset == null) {
            n = clusters.instancesCount();
        } else {
            n = dataset.size();
        }
        return nvar * n;
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        return score1 > score2;
    }

    @Override
    public boolean isMaximized() {
        return true;
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
