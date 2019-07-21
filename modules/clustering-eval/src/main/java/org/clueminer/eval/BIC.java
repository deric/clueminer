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
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Bayesian information criterion (also known as the Schwarz criterion)
 *
 * G. Schwarz. Estimating the dimension of a model. Annals of Statistics,
 * 6:461–464, 1978
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = InternalEvaluator.class)
public class BIC<E extends Instance, C extends Cluster<E>> extends AIC<E, C> {

    private static final String NAME = "BIC";
    private static final long serialVersionUID = -8771446315217152043L;

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
        Dataset<E> dataset = clusters.getLookup().lookup(Dataset.class);
        int n = dataset.size();
        int k = clusters.size();
        int d = dataset.attributeCount();
        double bic = n * d * Math.log(2 * Math.PI);

        bic += clusteringLoglikehood(clusters, d);
        bic += n * d + (k * d + k * d * (d + 1) / 2.0) * Math.log(n);
        return bic;
    }
}
