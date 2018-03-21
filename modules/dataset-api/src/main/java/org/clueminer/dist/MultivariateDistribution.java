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
package org.clueminer.dist;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Vector;

/**
 * Interface represents the contract that any continuous multivariate distribution must implement
 *
 * @author deric
 */
public interface MultivariateDistribution extends Cloneable, Serializable {

    /**
     * Computes the log of the probability density function. If the
     * probability of the input is zero, the log of zero would be
     * {@link Double#NEGATIVE_INFINITY}. Instead, -{@link Double#MAX_VALUE} is returned.
     *
     * @param x the array for the vector the get the log probability of
     * @return the log of the probability.
     * @throws ArithmeticException if the vector is not the correct length, or the distribution has not yet been set
     */
    double logPdf(double... x);

    /**
     * Computes the log of the probability density function. If the
     * probability of the input is zero, the log of zero would be
     * {@link Double#NEGATIVE_INFINITY}. Instead, -{@link Double#MAX_VALUE} is returned.
     *
     * @param x the vector the get the log probability of
     * @return the log of the probability.
     * @throws ArithmeticException if the vector is not the correct length, or the distribution has not yet been set
     */
    double logPdf(Vector<Double> x);

    /**
     * Returns the probability of a given vector from this distribution. By definition,
     * the probability will always be in the range [0, 1].
     *
     * @param x the array of the vector the get the log probability of
     * @return the probability
     * @throws ArithmeticException if the vector is not the correct length, or the distribution has not yet been set
     */
    double pdf(double... x);

    /**
     * Returns the probability of a given vector from this distribution. By definition,
     * the probability will always be in the range [0, 1].
     *
     * @param x the vector the get the log probability of
     * @return the probability
     * @throws ArithmeticException if the vector is not the correct length, or the distribution has not yet been set
     */
    double pdf(Vector<Double> x);

    /**
     * Sets the parameters of the distribution to attempt to fit the given list of vectors.
     * All vectors are assumed to have the same weight.
     *
     * @param <V>     the vector type
     * @param dataset the list of data points
     * @return <tt>true</tt> if the distribution was fit to the data, or <tt>false</tt>
     * if the distribution could not be fit to the data set.
     */
    <V extends Vector<Double>> boolean setUsingData(List<V> dataset);

    /**
     * Sets the parameters of the distribution to attempt to fit the given list of vectors.
     * All vectors are assumed to have the same weight.
     *
     * @param <V>        the vector type
     * @param dataset    the list of data points
     * @param threadpool the source of threads for computation
     * @return <tt>true</tt> if the distribution was fit to the data, or <tt>false</tt>
     * if the distribution could not be fit to the data set.
     */
    <V extends Vector<Double>> boolean setUsingData(List<V> dataset, ExecutorService threadpool);

    /**
     * Sets the parameters of the distribution to attempt to fit the given list of data points.
     * The {@link DataPoint#getWeight()  weights} of the data points will be used.
     *
     * @param dataPoints the list of data points to use
     * @return <tt>true</tt> if the distribution was fit to the data, or <tt>false</tt>
     * if the distribution could not be fit to the data set.
     */
    boolean setUsingDataList(List<Instance<Double>> dataPoints);

    /**
     * Sets the parameters of the distribution to attempt to fit the given list of data points.
     * The {@link DataPoint#getWeight()  weights} of the data points will be used.
     *
     * @param dataPoints the list of data points to use
     * @param threadpool the source of threads for computation
     * @return <tt>true</tt> if the distribution was fit to the data, or <tt>false</tt>
     * if the distribution could not be fit to the data set.
     */
    boolean setUsingDataList(List<Instance<Double>> dataPoints, ExecutorService threadpool);

    /**
     * Sets the parameters of the distribution to attempt to fit the given list of data points.
     * The {@link DataPoint#getWeight()  weights} of the data points will be used.
     *
     * @param dataSet the data set to use
     * @return <tt>true</tt> if the distribution was fit to the data, or <tt>false</tt>
     * if the distribution could not be fit to the data set.
     */
    boolean setUsingData(Dataset dataSet);

    /**
     * Sets the parameters of the distribution to attempt to fit the given list of data points.
     * The {@link DataPoint#getWeight()  weights} of the data points will be used.
     *
     * @param dataset    the data set to use
     * @param threadpool the source of threads for computation
     * @return <tt>true</tt> if the distribution was fit to the data, or <tt>false</tt>
     * if the distribution could not be fit to the data set.
     */
    boolean setUsingData(Dataset dataset, ExecutorService threadpool);

    MultivariateDistribution clone();

    /**
     * Performs sampling on the current distribution.
     *
     * @param count the number of iid samples to draw
     * @param rand  the source of randomness
     * @return a list of sample vectors from this distribution
     */
    List<Vector<Double>> sample(int count, Random rand);
}
