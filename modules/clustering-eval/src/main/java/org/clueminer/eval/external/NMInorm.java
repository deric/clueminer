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
package org.clueminer.eval.external;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Normalized value of NMI-sqrt by difference between "correct" number of
 * clusters
 * and actual number of clusters.
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = ExternalEvaluator.class)
public class NMInorm<E extends Instance, C extends Cluster<E>> extends NMIbase<E, C> {

    private static final String name = "NMI-norm";
    private static final long serialVersionUID = -1387444222605992582L;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double countNMI(double mutualInformation, double ent1, double ent2) {
        return mutualInformation / Math.sqrt(ent1 * ent2);
    }

    @Override
    protected double calculate(Clustering<E, C> clusters, Props params,
            double mutualInformation, double c1entropy, double classEntropy, int klassesSize) {
        //when difference is 0, we'll divide by 1 (no change)
        int clustDiff = 1 + Math.abs(clusters.size() - klassesSize);
        return countNMI(mutualInformation, c1entropy, classEntropy) / (1 + Math.log(clustDiff));
    }

}
