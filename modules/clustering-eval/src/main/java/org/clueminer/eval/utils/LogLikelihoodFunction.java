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
package org.clueminer.eval.utils;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.math.impl.GammaFunction;

public class LogLikelihoodFunction<E extends Instance, C extends Cluster<E>> {
    // tuning parameters?? standard value:

    double alpha0 = 0.1, beta0 = 0.1, lambda0 = 0.1, mu0 = 0.0;

    /**
     * Likelihood value
     *
     * @param N
     * @param sum
     * @param sum2
     * @return
     */
    public double logLikelihoodFunction(double N, double sum, double sum2) {
        double loglikelihood;
        double lambda1 = lambda0 + N;
        double alpha1 = alpha0 + 0.5 * N;
        double beta1 = beta0 + 0.5 * (sum2 - Math.pow(sum, 2) / N) + lambda0
                * Math.pow(sum - mu0 * N, 2) / (2 * lambda1 * N);

        loglikelihood = -0.5 * N * Math.log(2 * Math.PI) + 0.5
                * Math.log(lambda0) + alpha0 * Math.log(beta0)
                - GammaFunction.logGamma(alpha0)
                + GammaFunction.logGamma(alpha1) - alpha1 * Math.log(beta1)
                - 0.5 * Math.log(lambda1);
        return loglikelihood;
    }

    /**
     * Likelihood of all instances in a given cluster
     *
     * @param cluster
     * @return
     */
    public double logLikelihood(Dataset cluster) {
        int instanceLength = cluster.attributeCount();
        int count = instanceLength * cluster.size();
        double sum = 0;
        double sum2 = 0;
        double val;

        for (int row = 0; row < cluster.size(); row++) {
            for (int column = 0; column < instanceLength; column++) {
                val = cluster.instance(row).value(column);
                sum += val;
                sum2 += val * val;
            }
        }

        double loglikelihood = logLikelihoodFunction(count, sum, sum2);
        if (loglikelihood == Double.NEGATIVE_INFINITY
                || loglikelihood == Double.POSITIVE_INFINITY) {
            loglikelihood = 0;
        }
        return loglikelihood;
    }

    public double withinCluster(Cluster<E> cluster) {
        int dim = cluster.attributeCount();
        int count = dim * cluster.size();
        double sum = 0;
        double sum2 = 0;
        double val;

        E centroid = cluster.getCentroid();
        EuclideanDistance dist = EuclideanDistance.getInstance();

        for (E inst : cluster) {
            val = dist.measure(centroid, inst);
            sum += val;
            sum2 += val * val;
        }

        double loglikelihood = logLikelihoodFunction(count, sum, sum2);
        if (loglikelihood == Double.NEGATIVE_INFINITY
                || loglikelihood == Double.POSITIVE_INFINITY) {
            loglikelihood = 0;
        }
        return loglikelihood;
    }

    public double logLikelihood(Cluster cluster, int attr) {
        int dim = cluster.attributeCount();
        int count = dim * cluster.size();
        double sum = 0;
        double sum2 = 0;
        double val;

        for (int row = 0; row < cluster.size(); row++) {
            val = cluster.instance(row).value(attr);
            sum += val;
            sum2 += val * val;
        }

        double loglikelihood = logLikelihoodFunction(count, sum, sum2);
        if (loglikelihood == Double.NEGATIVE_INFINITY
                || loglikelihood == Double.POSITIVE_INFINITY) {
            loglikelihood = 0;
        }
        return loglikelihood;
    }

    /**
     * Sum of loglikelihood of each attribute
     *
     * @param cluster
     * @return
     */
    public double attrLikelihoodSum(Cluster<E> cluster) {
        int dim = cluster.attributeCount();
        double loglikelihood = 0;
        for (int column = 0; column < dim; column++) {
            loglikelihood += logLikelihood(cluster, column);
        }
        return loglikelihood;
    }

    /**
     * Total likelihood of finding data for given clustering
     *
     * @param clustering
     * @return
     */
    public double attrSum(Clustering<E, C> clustering) {
        double likelihood = 0;

        for (Cluster<E> clust : clustering) {
            likelihood += attrLikelihoodSum(clust);
        }
        return likelihood;
    }

    /**
     * Total likelihood of finding data for given clustering
     *
     * @param clustering
     * @return
     */
    public double centroidSum(Clustering<E, C> clustering) {
        double likelihood = 0;

        for (Cluster<E> clust : clustering) {
            likelihood += withinCluster(clust);
        }
        return likelihood;
    }

    public double getAlpha0() {
        return alpha0;
    }

    public void setAlpha0(double alpha0) {
        this.alpha0 = alpha0;
    }

    public double getBeta0() {
        return beta0;
    }

    public void setBeta0(double beta0) {
        this.beta0 = beta0;
    }

    public double getLambda0() {
        return lambda0;
    }

    public void setLambda0(double lambda0) {
        this.lambda0 = lambda0;
    }

    public double getMu0() {
        return mu0;
    }

    public void setMu0(double mu0) {
        this.mu0 = mu0;
    }
}
