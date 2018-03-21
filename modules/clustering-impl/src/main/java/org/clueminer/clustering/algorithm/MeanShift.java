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
package org.clueminer.clustering.algorithm;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class MeanShift<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> implements ClusteringAlgorithm<E, C> {

    private static final String NAME = "Mean Shift";

    public static final String MAX_ITERATIONS = "max_iterations";
    public static final String SCALE_BANDWITH_FACTOR = "scale_bandwith_factor";

    @Param(name = MeanShift.MAX_ITERATIONS, description = "max. number of mean shift iterations", required = false, min = 1000, max = 5000)
    private int iterations = -1;

    @Param(name = MeanShift.SCALE_BANDWITH_FACTOR, description = "scale bandwith factor", required = false, min = 0.0, max = 10.0)
    private double scaleBandwidthFactor = 1.0;

    private static final Logger LOG = LoggerFactory.getLogger(MeanShift.class);

    public MeanShift() {

    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> dataset, Props props) {
        int[] designations = new int[dataset.size()];
        Clustering<E, C> result;
        ExecutorService threadpool = null;
        //try {
        boolean[] converged = new boolean[dataset.size()];
        Arrays.fill(converged, false);

        /*  final KernelFunction k = mkde.getKernelFunction();
            if (threadpool == null) {
                mkde.setUsingData(dataset);
            } else {
                mkde.setUsingData(dataset, threadpool);
            }
            mkde.scaleBandwidth(scaleBandwidthFactor);

            Vec scratch = new DenseVector(dataset.getNumNumericalVars());
            Vec[] xit = new Vec[converged.length];
            for (int i = 0; i < xit.length; i++) {
                xit[i] = dataset.getDataPoint(i).getNumericalValues().clone();
            }
            if (threadpool == null) {
                mainLoop(converged, xit, designations, scratch, k);
            } else {
                mainLoop(converged, xit, designations, k, threadpool);
            }

            assignmentStep(converged, xit, designations);*/
        result = (Clustering<E, C>) Clusterings.newList(5, dataset);

        return result;
        /*        } catch (InterruptedException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        } catch (BrokenBarrierException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }*/
    }

    @Override
    public Configurator<E> getConfigurator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isDeterministic() {
        return false;
    }

}
