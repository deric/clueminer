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
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.StatsNum;
import org.clueminer.dataset.row.DoubleArrayDataRow;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trace(W-1 B)
 *
 * Friedman, H. P.; Rubin, J. On some invariant criteria for grouping data.
 * Journal of the American Statistical Association, volume 62, no. 320, 1967:
 * pp. 1159–1178. F
 *
 * @author deric
 */
//@ServiceProvider(service = InternalEvaluator.class)
public class TraceWiB<E extends Instance, C extends Cluster<E>> extends AbstractEvaluator<E, C> {

    private static final long serialVersionUID = 6195054290041907628L;
    private static final String NAME = "TraceWiB";
    private static final String CALLSIGN = "twb";
    private static final Logger LOG = LoggerFactory.getLogger(TraceWiB.class);

    public TraceWiB() {
        dm = new EuclideanDistance();
    }

    public TraceWiB(Distance dist) {
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
        Matrix W = withinGroupScatter(clusters);
        Dataset<E> dataset = clusters.getLookup().lookup(Dataset.class);
        if (dataset == null) {
            throw new RuntimeException("missing original dataset");
        }

        int k = clusters.size();
        int d = dataset.attributeCount();

        //dataset mean
        E mu = (E) new DoubleArrayDataRow(k);
        double mean;
        for (int g = 0; g < d; g++) {
            Attribute attr = dataset.getAttribute(g);
            mean = attr.statistics(StatsNum.MEAN);
            mu.set(g, mean);
        }
        // between scatter matrix (d x d)
        Matrix B = bgMatrix(clusters);

        return W.inverse().times(B).trace();
    }

    /**
     * BGSS from geometrical means
     *
     * @param clusters
     * @return
     */
    public double bgss(Clustering<E, C> clusters) {
        Dataset<E> dataset = clusters.getLookup().lookup(Dataset.class);
        if (dataset == null) {
            throw new RuntimeException("missing original dataset");
        }

        int k = clusters.size();
        int d = dataset.attributeCount();

        //dataset mean
        E mu = (E) new DoubleArrayDataRow(k);
        double mean;
        for (int g = 0; g < d; g++) {
            Attribute attr = dataset.getAttribute(g);
            mean = attr.statistics(StatsNum.MEAN);
            mu.set(g, mean);
        }

        double bgss = 0.0;
        C clust;
        double val;
        for (int i = 0; i < k; i++) {
            clust = clusters.get(i);
            val = dm.measure(mu, clust.getCentroid());
            bgss += clust.size() * val;

        }
        return bgss;
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
