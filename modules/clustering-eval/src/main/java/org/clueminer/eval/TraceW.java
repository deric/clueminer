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
 * This index has been one of the most popular indices suggested for use in
 * clustering context
 *
 * (Milligan and Cooper 1985; Edwards and Cavalli-Sforza 1965; Friedman and
 * Rubin 1967; Orloci 1967; Fukunaga and Koontz 1970).
 *
 * Milligan, Glenn W., and Martha C. Cooper. "An examination of procedures for
 * determining the number of clusters in a data set." Psychometrika 50.2 (1985):
 * 159-179.
 *
 * @see similar index {@link Deviation}
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = InternalEvaluator.class)
public class TraceW<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final long serialVersionUID = 6195054290041907628L;
    private static final String NAME = "TraceW";
    private static final String CALLSIGN = "trw";

    public TraceW() {
        dm = new EuclideanDistance();
    }

    public TraceW(Distance dist) {
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
        return wgss(clusters);
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
