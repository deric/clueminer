/*
 * Copyright (C) 2011-2019 clueminer.org
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
 * Originally proposed for fuzzy clustering, however could be adjusted for case
 * of crisp clustering.
 *
 * Very similar to {@link RayTuri}, just different denominator.
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = InternalEvaluator.class)
public class XieBeni<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static String NAME = "Xie-Beni";
    private static String CALLSIGN = "XB";
    private static final long serialVersionUID = -1556797441498915591L;

    public XieBeni() {
        dm = new EuclideanDistance();
    }

    public XieBeni(Distance dist) {
        this.dm = dist;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCallsign() {
        return CALLSIGN;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        double wgss = wgss(clusters);
        double dist;
        Cluster x, y;
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < clusters.size(); i++) {
            x = clusters.get(i);

            //min squared distance between all items
            for (int j = 0; j < i; j++) {
                y = clusters.get(j);
                for (int k = 0; k < x.size(); k++) {
                    for (int l = 0; l < y.size(); l++) {
                        dist = dm.measure(x.get(k), y.get(l));
                        dist *= dist;
                        if (dist < min) {
                            min = dist;
                        }
                    }
                }
            }
        }

        return wgss / (clusters.instancesCount() * min);
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
