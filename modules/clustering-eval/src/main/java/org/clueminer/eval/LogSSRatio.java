/*
 * Copyright (C) 2011-2015 clueminer.org
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
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Similar index to Calinski-Harabasz index {@link CalinskiHarabasz}
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = InternalEvaluator.class)
public class LogSSRatio<E extends Instance, C extends Cluster<E>> extends CalinskiHarabasz<E, C> implements InternalEvaluator<E, C> {

    private static final String name = "Log SS Ratio";
    private static final long serialVersionUID = 1027250256090361526L;

    public LogSSRatio() {
        dm = EuclideanDistance.getInstance();
    }

    public LogSSRatio(Distance distance) {
        this.dm = distance;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        if (clusters.size() > 1) {
            double w = 0.0, b = 0.0;
            //centroid of all data
            Instance centroid = clusters.getCentroid();
            double d;
            for (int i = 0; i < clusters.size(); i++) {
                C x = clusters.get(i);
                w += sumOfSquaredError(x);
                d = dm.measure(centroid, x.getCentroid());
                b += (x.size()) * FastMath.pow(d, 2);
            }

            if (w == 0.0) {
                return 0.0;
            }
            return Math.log(b / w);
        } else {
            /*
             * To avoid division by zero,
             * with just one cluster we can't compute index
             */
            return Double.NaN;
        }
    }

}
