/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.clustering.spectral;

import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.Configurator;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

public class SpectralClustering<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> implements ClusteringAlgorithm<E, C> {

    public static final String NAME = "Spectral Clustering";
    public static final String K = "k";

    //min and max values are used as limit for evolutionary algorithms
    @Param(name = SpectralClustering.K, description = "expected number of clusters", required = true, min = 2, max = 25)
    private int k;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> data, Props params) {
        if (data == null || data.isEmpty()) {
            throw new RuntimeException("The dataset should not be empty");
        }

        k = params.getInt(SpectralClustering.K);
        if (k <= 1) {
            throw new RuntimeException("Number of clusters should be at least 2");
        }
        //TODO: implement

        Clustering result = Clusterings.newList(k, data);
        result.setParams(params);
        params.put(AlgParams.ALG, getName());

        return result;
    }

    @Override
    public Configurator<E> getConfigurator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isDeterministic() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
