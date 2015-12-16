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
package org.clueminer.ap;

import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.exception.ParameterException;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.math.matrix.SymmetricMatrix;
import org.clueminer.utils.Props;

/**
 * Affinity Propagation
 *
 * @cite Clustering by Passing Messages Between Data Points. Brendan J. Frey and
 * Delbert Dueck, University of Toronto Science 315, 972–976, February 2007
 *
 * @author deric
 */
public class AffinityPropagation<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> {

    public static final String NAME = "AP";

    public static final String DAMPING = "damping";

    /**
     * Damping factor between 0.5 and 1.
     */
    @Param(name = DAMPING, description = "Damping factor (lambda)")
    protected double damping = 0.5;

    public static final String MAX_ITERATIONS = "max_iterations";
    public static final String PREFERENCE = "preference";

    @Param(name = MAX_ITERATIONS, description = "Maximum number of iterations")
    protected int maxIterations = 100;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> dataset, Props props) {
        Clustering clustering = null;

        double lambda = props.getDouble(DAMPING, damping);
        int maxIter = props.getInt(MAX_ITERATIONS, maxIterations);

        if (lambda < 0.5 || lambda >= 1) {
            throw new ParameterException("damping must be >= 0.5 and < 1");
        }
        int nSample = dataset.size();

        double pref;
        if (props.containsKey(PREFERENCE)) {
            pref = props.getDouble(PREFERENCE);
        } else {
            //TODO: try median instead
            pref = (dataset.min() - dataset.max()) / 2.0;
        }

        //initialize similarities
        Matrix S;

        //initialize messages
        Matrix A = new JMatrix(nSample, nSample);
        Matrix R = new JMatrix(nSample, nSample);
        Matrix tmp = new JMatrix(nSample, nSample);

        double eps = Double.MIN_VALUE;

        //Remove degeneracies
        return clustering;
    }

    private Matrix similarity(Dataset<E> dataset) {
        Matrix sim = new SymmetricMatrix(dataset.size());
        for (int i = 0; i < sim.rowsCount(); i++) {
            for (int j = i + 1; j < sim.rowsCount(); j++) {

            }

        }
    }

    /**
     * The smallest positive usable number.
     *
     * @return
     */
    private double tiny() {
        long bits = Double.doubleToLongBits(Double.MIN_VALUE);
        bits++;
        return Double.longBitsToDouble(bits);
    }

}
