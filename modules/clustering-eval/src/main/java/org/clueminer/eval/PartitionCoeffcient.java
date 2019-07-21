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
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 * Coefficient for fuzzy clusterings counts with *mu* which is degree of
 * membership to a cluster. Doesn't make sense for hard clustering
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class PartitionCoeffcient<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final String NAME = "PC";
    private static final long serialVersionUID = 888558324967098222L;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCallsign() {
        return NAME;
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        //TODO fix this for fuzzy case
        double mu;
        double pc = 0.0;
        for (Cluster<? extends Instance> c : clusters) {
            double sum = 0.0;
            for (Instance inst : c) {
                mu = 1.0; //TODO instace membership
                sum += Math.pow(mu, 2);
            }
            pc = sum / (double) c.size();
        }
        return (pc / (double) clusters.size());
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        return (score1 > score2);
    }

    @Override
    public boolean isMaximized() {
        return true;
    }

    @Override
    public double getMin() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getMax() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
