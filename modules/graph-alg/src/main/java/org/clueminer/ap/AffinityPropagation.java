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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.EuclideanDistance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.exception.ParameterException;
import org.clueminer.math.IntMatrix;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.IntegerMatrix;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.math.matrix.SymmetricMatrixDiag;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * Affinity Propagation
 *
 * @cite Clustering by Passing Messages Between Data Points. Brendan J. Frey and
 * Delbert Dueck, University of Toronto Science 315, 972–976, February 2007
 *
 * @author deric
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class AffinityPropagation<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> {

    public static final String NAME = "AP";

    public static final String DAMPING = "damping";

    /**
     * Damping factor between 0.5 and 1.
     */
    @Param(name = DAMPING, description = "Damping factor (lambda)", min = 0.5, max = 1.0)
    protected double damping = 0.5;

    public static final String MAX_ITERATIONS = "max_iterations";
    public static final String CONV_ITER = "conv_iter";
    public static final String PREFERENCE = "preference";

    @Param(name = CONV_ITER, description = "Max. convergence iterations")
    protected int convergenceIter = 100;

    @Param(name = MAX_ITERATIONS, description = "Maximum number of iterations")
    protected int maxIterations = 100;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> dataset, Props props) {
        double lambda = props.getDouble(DAMPING, damping);
        int maxIter = props.getInt(MAX_ITERATIONS, maxIterations);
        int convits = props.getInt(CONV_ITER, convergenceIter);
        if (props.containsKey(DISTANCE)) {
            distanceFunction = DistanceFactory.getInstance().getProvider(props.get(DISTANCE));
        } else {
            distanceFunction = EuclideanDistance.getInstance();
            props.put(DISTANCE, distanceFunction.getName());
        }
        if (lambda < 0.5 || lambda >= 1) {
            throw new ParameterException("damping must be >= 0.5 and < 1");
        }
        int N = dataset.size();

        double pref;

        //initialize similarities
        Matrix S = similarity(dataset);
        //S.printLower(2, 5);

        if (props.containsKey(PREFERENCE)) {
            pref = props.getDouble(PREFERENCE);
        } else {
            pref = median(S);
            props.putDouble(PREFERENCE, pref);
        }

        //initialize messages
        Matrix A = new JMatrix(N, N);
        Matrix R = new JMatrix(N, N);

        double eps = Double.MIN_VALUE;
        double tiny = tiny();
        Random rand = new Random();

        double value;
        //Remove degeneracies
        for (int i = 0; i < N; i++) {
            for (int j = 1; j < N; j++) {
                value = S.get(i, j);
                S.set(i, j, value + (eps * value + tiny * 100) * rand.nextGaussian());
            }
        }
        int[] se = new int[N];
        int[] E = new int[N];
        //Arrays.fill(E, -1);
        int[] I = new int[N];
        IntMatrix e = new IntegerMatrix(N, convits);
        boolean dn = false, unconverged;

        int i = 0, j, ii, K = 0;

        //Place preference on the diagonal of S
        S.setDiagonal(pref);

        while (!dn) {
            //first, compute responsibilities
            for (ii = 0; ii < N; ii++) {
                double max1 = Double.NEGATIVE_INFINITY, max2 = Double.NEGATIVE_INFINITY, curr;
                int yMax = -1;

                //determine second-largest element of AS
                for (j = 0; j < N; j++) {
                    curr = A.get(ii, j) + S.get(ii, j);

                    if (curr > max1) {
                        max2 = max1;
                        max1 = curr;
                        yMax = j;
                    } else if (curr > max2) {
                        max2 = curr; //second largest value
                    }
                }

                //perform update
                for (j = 0; j < N; j++) {
                    double oldVal = R.get(ii, j);
                    double newVal = (1 - lambda) * (S.get(ii, j) - (j == yMax ? max2 : max1)) + lambda * oldVal;
                    R.set(ii, j, (!isFinite(newVal) ? Double.MAX_VALUE : newVal));
                }
            }

            // secondly, compute availabilities
            for (ii = 0; ii < N; ii++) {
                double[] Rp = new double[N];
                double auxsum = 0;

                for (j = 0; j < N; j++) {
                    if (R.get(j, ii) < 0 && j != ii) {
                        Rp[j] = 0;
                    } else {
                        Rp[j] = R.get(j, ii);
                    }
                    auxsum += Rp[j];
                }

                for (j = 0; j < N; j++) {
                    double oldVal = A.get(j, ii);
                    double newVal = auxsum - Rp[j];

                    if (newVal > 0 && j != ii) {
                        newVal = 0;
                    }
                    A.set(j, ii, (1 - lambda) * newVal + lambda * oldVal);
                }
            }

            // determine clusters and check for convergence
            unconverged = false;

            K = 0;

            for (ii = 0; ii < N; ii++) {
                int ex = (A.get(ii, ii) + R.get(ii, ii) > 0 ? 1 : 0);
                se[ii] = se[ii] - e.get(ii, i % convits) + ex;
                if (se[ii] > 0 && se[ii] < convits) {
                    unconverged = true;
                }
                E[ii] = ex;
                e.set(ii, i % convits, ex);
                K += ex;
            }

            if (i >= (convits - 1) || i >= (maxIter - 1)) {
                dn = ((!unconverged && K > 0) || (i >= (maxIter - 1)));
            }

            if (K > 0) {
                int cluster = 0;

                for (ii = 0; ii < N; ii++) {
                    if (E[ii] > 0) {
                        I[cluster] = ii;
                        cluster++;
                    }
                }

                double maxSim;
                for (ii = 0; ii < N; ii++) {
                    if (E[ii] <= 0) {
                        maxSim = S.get(ii, I[0]);

                        for (j = 1; j < K; j++) {
                            if (S.get(ii, I[j]) > maxSim) {
                                maxSim = S.get(ii, I[j]);
                            }
                        }
                    }
                }
            }
            i++;
        }

        int[] c = maxCol(S, I, K);
        //Dump.array(c, "assignments");
        props.putInt("k", K);
        //System.out.println("k = " + K);
        //Dump.array(I, "I vec");
        return extractClusters(dataset, props, c, K);
    }

    /**
     * Could be replace by Double.isFinite which is available in Java 8
     *
     * @param d
     * @return
     */
    public boolean isFinite(double d) {
        return Math.abs(d) <= Double.MAX_VALUE;
    }

    /**
     * Assign each item to one exemplar.
     *
     * @param S similarity matrix
     * @param I indexes of medoids (exemplars)
     * @param K number of detected medoids (exemplars)
     * @return
     */
    private int[] maxCol(Matrix S, int[] I, int K) {
        int[] c = new int[S.columnsCount()];
        double val, max;
        int maxIdx;
        for (int i = 0; i < S.columnsCount(); i++) {
            max = Double.NEGATIVE_INFINITY;
            maxIdx = -1;
            //find most similar exemplar
            for (int j = 0; j < K; j++) {
                val = S.get(i, I[j]);
                //when comparing to an exemplar, always choose the exemplar
                if (i == I[j]) {
                    maxIdx = j;
                    break;
                }
                if (val > max) {
                    max = val;
                    maxIdx = j;
                }
            }
            c[i] = maxIdx;
        }

        return c;
    }

    protected Clustering<E, C> extractClusters(Dataset<E> dataset, Props props, int[] I, int K) {
        props.put("algorithm", getName());
        Clustering<E, C> res = (Clustering<E, C>) Clusterings.newList(K);
        Cluster<E> curr;
        HashMap<Integer, Integer> mapping = new HashMap<>(K);
        int clusterId;
        if (colorGenerator != null) {
            colorGenerator.reset();
        }
        for (int k = 0; k < I.length; k++) {
            if (mapping.containsKey(I[k])) {
                clusterId = mapping.get(I[k]);
                curr = res.get(clusterId);
            } else {
                curr = res.createCluster();
                curr.setAttributes(dataset.getAttributes());
                if (colorGenerator != null) {
                    curr.setColor(colorGenerator.next());
                }
                clusterId = curr.getClusterId();
                mapping.put(I[k], clusterId);
            }
            curr.add(dataset.get(k));
        }

        res.lookupAdd(dataset);
        res.setParams(props);

        return res;
    }

    protected Matrix similarity(Dataset<E> dataset) {
        Matrix sim = new SymmetricMatrixDiag(dataset.size());
        for (int i = 0; i < sim.rowsCount(); i++) {
            for (int j = i + 1; j < sim.rowsCount(); j++) {
                sim.set(i, j, distanceFunction.measure(dataset.get(i), dataset.get(j)));
            }
        }
        return sim;
    }

    /**
     * The smallest positive usable number.
     *
     * @return
     */
    protected double tiny() {
        long bits = Double.doubleToLongBits(Double.MIN_VALUE);
        bits++;
        return Double.longBitsToDouble(bits);
    }

    private double median(Matrix S) {
        int n = S.rowsCount();
        int capacity = ((n - 1) * n) >>> 1;
        double[] m = new double[capacity];
        double val;
        int l = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                val = S.get(i, j);
                if (!Double.isNaN(val) && val > Double.NEGATIVE_INFINITY) {
                    m[l++] = val;
                }
            }
        }
        if (n != l) {
            double[] tmp = new double[l];
            System.arraycopy(m, 0, tmp, 0, l);
        }
        Arrays.sort(m);

        int middle = m.length / 2;
        if (m.length % 2 == 1) {
            return m[middle];
        } else {
            return (m[middle - 1] + m[middle]) / 2.0;
        }
    }

}
