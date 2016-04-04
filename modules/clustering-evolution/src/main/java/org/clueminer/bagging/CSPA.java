/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.bagging;

import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Consensus;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 * Inspired by Cluster-based Similarity Partitioning Algorithm (CSPA) by Strehl,
 * Ghosh
 *
 * Strehl, Alexander, and Joydeep Ghosh. "Cluster ensembles---a knowledge reuse
 * framework for combining multiple partitions." The Journal of Machine Learning
 * Research 3 (2003): 583-617.
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class CSPA<E extends Instance, C extends Cluster<E>> extends CoAssocMatrix implements Consensus<E, C> {

    public static final String name = "CSPA";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Clustering<E, C> reduce(Clustering[] clusts, Algorithm<E, C> alg,
            ColorGenerator cg, Props props) {
        Matrix coassoc = createMatrix(clusts);

        return null;
    }

}
