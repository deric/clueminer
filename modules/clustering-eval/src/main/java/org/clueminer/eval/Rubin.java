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
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.StatsNum;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rubin index
 *
 * Very similar to {@link DetRatio}
 *
 * Friedman, Herman P., and Jerrold Rubin. "On some invariant criteria for
 * grouping data." Journal of the American Statistical Association 62.320
 * (1967): 1159-1178.
 *
 * @author deric
 */
@ServiceProvider(service = InternalEvaluator.class)
public class Rubin<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final String NAME = "Rubin";
    private static final String CALLSIGN = "rub";
    private static final long serialVersionUID = -1636596859242265112L;
    private static final Logger LOG = LoggerFactory.getLogger(Rubin.class);

    public Rubin() {
        dm = new EuclideanDistance();
    }

    public Rubin(Distance dist) {
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
        Dataset<E> dataset = clusters.getLookup().lookup(Dataset.class);
        if (dataset == null) {
            throw new RuntimeException("missing original dataset");
        }
        try {
            Matrix Wq = withinGroupScatter(clusters);

            return tss(clusters) / Wq.trace();
        } catch (RuntimeException ex) {
            LOG.warn(ex.getMessage(), ex);
            return Double.NaN;
        }
    }

    public double scoreOld(Clustering<E, C> clusters, Props params) {
        Dataset<E> dataset = clusters.getLookup().lookup(Dataset.class);
        if (dataset == null) {
            throw new RuntimeException("missing original dataset");
        }
        Matrix X = dataset.asMatrix();

        //Matrix X is expected to be formed by centered vectors (substracted mean for each column vector - attribute)
        Matrix Xc = X.copy();
        for (int j = 0; j < X.columnsCount(); j++) {
            for (int i = 0; i < X.rowsCount(); i++) {
                Xc.set(i, j, Xc.get(i, j) - dataset.getAttribute(j).statistics(StatsNum.MEAN));
            }
        }

        //a matrix d x d (d - number of attributes)
        // T = X'X
        Matrix TT = Xc.transpose().times(Xc);
        try {
            Matrix Wq = withinGroupScatter(clusters);
            //System.out.println("TT trace = " + TT.trace());
            return TT.trace() / Wq.trace();
        } catch (RuntimeException ex) {
            LOG.warn(ex.getMessage(), ex);
            return Double.NaN;
        }
    }

    /**
     * TODO: Maximum difference between scores determine best clustering
     *
     * @param score1
     * @param score2
     * @return
     */
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
