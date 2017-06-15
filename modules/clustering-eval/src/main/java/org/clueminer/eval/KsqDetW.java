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
package org.clueminer.eval;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Marriott index, KsqDetW or k^2 |W| criterion
 * Best partition size is determined by max score difference
 *
 *
 * Marriott, F. H. C. "Practical problems in a method of cluster analysis." Biometrics (1971): 501-514.
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = InternalEvaluator.class)
public class KsqDetW<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> implements InternalEvaluator<E, C> {

    private static final String NAME = "Marriott (KsqDetW)";
    private static final long serialVersionUID = 3727657004516559539L;

    public KsqDetW() {
        dm = EuclideanDistance.getInstance();
    }

    public KsqDetW(Distance dm) {
        this.dm = dm;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double res = clusters.size() * clusters.size();
        if (clusters.size() > 0) {
            //res *= wqMatrix(clusters).det();
            res *= withinGroupScatter(clusters).det();
        }
        return res;
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        return score1 < score2;
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
