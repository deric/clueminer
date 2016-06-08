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
package org.clueminer.eval;

import java.util.Iterator;
import org.apache.commons.math3.util.FastMath;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.StatsNum;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Matrix;
import org.clueminer.math.Vector;
import org.clueminer.math.impl.DenseVector;
import org.clueminer.math.impl.Stats;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.math.matrix.SymmetricMatrixDiag;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public abstract class AbstractEvaluator<E extends Instance, C extends Cluster<E>>
        extends AbstractComparator<E, C> implements InternalEvaluator<E, C>, ClusterEvaluation<E, C> {

    private static final long serialVersionUID = 6345948849700989503L;

    protected Distance dm;

    @Override
    public void setDistanceMeasure(Distance dm) {
        this.dm = dm;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public boolean isExternal() {
        return false;
    }

    @Override
    public double score(Clustering clusters) {
        return score(clusters, new Props());
    }

    @Override
    public double score(Clustering<E, C> clusters, Matrix proximity, Props params) {
        return score(clusters, params);
    }

    /**
     * Sum of distances within the cluster
     *
     * @param cluster
     * @return
     */
    public double sumWithin(Cluster<E> cluster) {
        double sum = 0.0;
        Instance x, y;
        for (int i = 0; i < cluster.size(); i++) {
            x = cluster.instance(i);
            for (int j = 0; j < i; j++) {
                y = cluster.instance(j);
                sum += dm.measure(x, y);
            }
        }

        return sum;
    }

    public double sumBetween(Clustering<E, C> clusters) {
        Cluster xc, yc;
        Instance x, y;
        double distance;
        double sum = 0.0;
        for (int i = 0; i < clusters.size(); i++) {
            xc = clusters.get(i);
            for (int m = 0; m < xc.size(); m++) {
                x = xc.instance(m);
                for (int j = 0; j < i; j++) {
                    yc = clusters.get(j);
                    for (int k = 0; k < yc.size(); k++) {
                        y = yc.instance(k);
                        distance = dm.measure(x, y);
                        if (!Double.isNaN(distance)) {
                            sum += distance;
                        }
                    }
                }
            }
        }
        return sum;
    }

    /**
     * Number of within-cluster pairs
     *
     * @param clusters
     * @return
     */
    public int numW(Clustering<E, C> clusters) {
        int numWPairs = 0;
        //number of within pairs
        for (Cluster clust : clusters) {
            numWPairs += clust.size() * clust.size();
        }
        return (numWPairs - clusters.instancesCount()) >>> 1; // (numWpairs - N) / 2
    }

    /**
     * Number of cluster pairs in the whole dataset
     *
     * @param clusters
     * @return
     */
    public int numT(Clustering<E, C> clusters) {
        int n = clusters.instancesCount();
        return (n * (n - 1)) >>> 1; // (numWpairs - N) / 2
    }

    /**
     * Sum of squared distance differences to the centroid of the cluster
     *
     * @param x
     * @return
     */
    public double sumOfSquaredError(Cluster<E> x) {
        double squaredErrorSum = 0, dist;
        Instance centroid = x.getCentroid();
        for (Instance inst : x) {
            dist = dm.measure(inst, centroid);
            squaredErrorSum += FastMath.pow(dist, 2);
        }

        return squaredErrorSum;
    }

    /**
     * Variance of given attribute in the dataset
     *
     * @param clusters
     * @param d
     * @return
     */
    public double attrVar(Clustering<E, C> clusters, int d) {
        Dataset<? extends Instance> dataset = clusters.getLookup().lookup(Dataset.class);
        //variance for specific attribute - precomputed
        if (dataset != null) {
            return dataset.getAttribute(d).statistics(StatsNum.VARIANCE);
        }
        //compute variance manually
        double mu = attrMean(clusters, d);
        Iterator<E> iter = clusters.instancesIterator();
        Instance curr;
        double var = 0.0;
        int i = 0;
        while (iter.hasNext()) {
            curr = iter.next();
            var += FastMath.pow(mu - curr.get(d), 2);
            i++;
        }
        return var / (i - 1);
    }

    /**
     * Mean attribute value
     *
     * @param clusters
     * @param d attribute index
     * @return
     */
    public double attrMean(Clustering<E, C> clusters, int d) {
        Dataset<? extends Instance> dataset = clusters.getLookup().lookup(Dataset.class);
        if (dataset != null) {
            return dataset.getAttribute(d).statistics(StatsNum.AVG);
        }
        Iterator<E> iter = clusters.instancesIterator();
        Instance curr;
        double mean = 0.0;
        int i = 0;
        while (iter.hasNext()) {
            curr = iter.next();
            mean += curr.get(d);
            i++;
        }
        return mean / i;
    }

    /**
     * With-in group squared scatter - distances between centroid.
     *
     * @param clusters
     * @return
     */
    public double wgss(Clustering<E, C> clusters) {
        double wgss = 0.0, dist;
        Cluster clust;
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            for (int j = 0; j < clust.size(); j++) {
                dist = dm.measure(clust.get(j), clust.getCentroid());
                wgss += dist * dist;
            }
        }
        return wgss;
    }

    /**
     * We use centered column vectors of the matrix
     *
     * @param clusters
     * @return
     */
    public Matrix totalDispersion(Clustering<E, C> clusters) {
        Instance curr = clusters.get(0).get(0);
        int n = clusters.instancesCount();
        //number of dimensions
        int m = curr.size();
        Matrix t = new SymmetricMatrixDiag(m);
        double value;

        Vector[] cols = new Vector[m];
        double[] mu = new double[m];
        for (int j = 0; j < m; j++) {
            cols[j] = new DenseVector(n);
        }
        Iterator<E> it = clusters.instancesIterator();
        int l = 0;
        while (it.hasNext()) {
            curr = it.next();
            for (int j = 0; j < m; j++) {
                value = curr.get(j);
                mu[j] += value;
                cols[j].set(l, value);
            }
            l++;
        }
        //compute average for each column
        //double var;
        for (int j = 0; j < m; j++) {
            mu[j] = mu[j] / l;
            //var = Stats.variance(cols[j], false);
        }
        //center columns
        it = clusters.instancesIterator();
        l = 0;
        while (it.hasNext()) {
            curr = it.next();
            for (int j = 0; j < m; j++) {
                cols[j].set(l, curr.get(j) - mu[j]);
            }
            l++;
        }

        Vector dx, dy;
        //trace matrix
        for (int i = 0; i < m; i++) {
            dx = cols[i];
            for (int j = 0; j <= i; j++) {
                dy = cols[j];
                value = dx.dot(dy);
                t.set(i, j, value);
            }
        }

        return t;
    }

    /**
     * Within-group (cluster) scatter
     *
     * @param clust
     * @return
     */
    public Matrix wgScatter(Cluster<E> clust) {
        double value;
        int m = clust.attributeCount();
        Instance curr;
        //column vectors
        Vector[] cols = new Vector[m];
        Matrix wg = new SymmetricMatrixDiag(m);
        double[] mu = new double[m];
        for (int j = 0; j < m; j++) {
            cols[j] = new DenseVector(clust.size());
        }
        for (int i = 0; i < clust.size(); i++) {
            curr = clust.get(i);
            for (int j = 0; j < m; j++) {
                value = curr.get(j);
                mu[j] += value;
                cols[j].set(i, value);
            }
        }
        //compute average for each column
        for (int j = 0; j < m; j++) {
            mu[j] = mu[j] / clust.size();
            //subtract mean value
            cols[j] = cols[j].minus(mu[j]);
        }

        //trace matrix
        for (int i = 0; i < m; i++) {
            for (int j = 0; j <= i; j++) {
                value = cols[i].dot(cols[j]);
                wg.set(i, j, value);
            }
        }
        return wg;
    }

    /**
     * Computes within group (cluster) scatter matrix
     *
     * @param clusters
     * @return
     */
    public Matrix withinGroupScatter(Clustering<E, C> clusters) {
        //number of dimensions
        int m = clusters.get(0).attributeCount();
        Matrix wg = new JMatrix(m, m);
        for (Cluster clust : clusters) {
            wg.plusEquals(wgScatter(clust));
        }
        return wg;
    }

    /**
     * Trace of within-group matrix
     *
     * @param clust
     * @return
     */
    public double trwg(C clust) {
        double trace = 0.0;
        double value;
        int m = clust.attributeCount();
        Instance curr;
        //column vectors
        Vector[] cols = new Vector[m];
        int k = clust.size();
        for (int j = 0; j < m; j++) {
            cols[j] = new DenseVector(clust.size());
        }
        for (int i = 0; i < clust.size(); i++) {
            curr = clust.get(i);
            for (int j = 0; j < m; j++) {
                value = curr.get(j);
                cols[j].set(i, value);
            }
        }
        //compute average for each column
        double var;

        for (int j = 0; j < m; j++) {
            var = k * Stats.variance(cols[j], false);
            trace += var;
        }

        return trace;
    }

}
