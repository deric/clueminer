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
import java.util.Collections;
import org.apache.commons.math3.util.FastMath;
import org.clueminer.clustering.api.Configurator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.knn.LinearSearch;
import org.clueminer.neighbor.KNNSearch;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.utils.Props;

/**
 * Parameter estimator for DBSCAN algorithm. Based on recommendation in the
 * original paper.
 *
 * Estimation parameters, especially Eps parameter, is hard without prior
 * knowledge of the dataset. We're using sorted k-NN(4) distances to all data
 * points in order to determine proper settings.
 *
 * @author deric
 * @param <E>
 */
public class DBSCANParamEstim<E extends Instance> implements Configurator<E> {

    /**
     * k-th neighbor used as reference distance for each data point. According
     * to DBSCAN authors after 4th neighbor estimated Eps won't change much
     */
    private int k = 4;
    private double slope;
    private int localNeighborhood = 10;
    private Double[] kdist;
    private int knee;
    private double eps;

    private static DBSCANParamEstim instance;

    private DBSCANParamEstim() {
    }

    public static DBSCANParamEstim getInstance() {
        if (instance == null) {
            instance = new DBSCANParamEstim();
        }
        return instance;
    }

    @Override
    public void configure(Dataset<E> dataset, Props params) {
        params.putInt(DBSCAN.MIN_PTS, k);
        estimate(dataset, params);
    }

    public void estimate(Dataset<E> dataset, Props params) {
        //compute k-dist for dataset
        kdist(dataset);

        knee = findKnee(kdist);
        //recommended eps value
        eps = kdist[knee];
        params.putDouble(DBSCAN.EPS, eps);
    }

    /**
     *
     * @param dataset
     * @return
     */
    private Double[] kdist(Dataset<E> dataset) {
        //k-dist graph data
        KNNSearch<Instance> knn = new LinearSearch(dataset);
        Neighbor[] neighbors;
        kdist = new Double[dataset.size()];
        for (int i = 0; i < dataset.size(); i++) {
            neighbors = knn.knn(dataset.get(i), k);
            kdist[i] = neighbors[k - 1].distance;
        }
        Arrays.sort(kdist, 0, kdist.length - 1, Collections.reverseOrder());

        return kdist;
    }

    private int findKnee(Double[] kdist) {
        int maxX = maxX(kdist);
        slope = slope(kdist, maxX);
        double dist;
        double max = Double.MIN_VALUE;
        int maxIdx = 0;
        for (int i = 1; i < maxX; i++) {
            dist = localMin(kdist, i, i + localNeighborhood, slope);
            if (dist > max) {
                max = dist;
                maxIdx = i;
            }
            //System.out.println(i + " => " + kx + ", max = " + max);
        }
        //System.out.println("max = " + max + ", at " + maxIdx);
        return maxIdx;
    }

    /**
     * Find shortest local distance
     *
     * @param kdist
     * @param from
     * @param to
     * @return
     */
    private double localMin(Double[] kdist, int from, int to, double slope) {
        double min = Double.MAX_VALUE;
        double dist = 0;
        //int minX;
        for (int i = from; i < to; i++) {
            dist = distance(from, kdist[from], i, ref(kdist, i, slope));
            if (dist < min) {
                min = dist;
            }
        }
        return dist;
    }

    private double distance(double x1, double y1, double x2, double y2) {
        double dist = FastMath.pow(x1 - x2, 2) + FastMath.pow(y1 - y2, 2);
        return Math.sqrt(dist);
    }

    /**
     * Computes slope of a linear function
     *
     * @param kdist sorted array of data values (from max to min)
     * @param maxX
     * @return slope of a cure
     */
    public double slope(Double[] kdist, int maxX) {
        int x1 = 0;
        int x2 = maxX;
        double y1 = kdist[x1]; //max y
        double y2 = kdist[x2]; //min y - we're looking for elbow/knee in first half
        return (y2 - y1) / (x2 - x1);
    }

    /**
     * We're looking for the knee point in the first half of the curve
     *
     * @param kdist
     * @return
     */
    private int maxX(Double[] kdist) {
        return (kdist.length - 1) / 2;
    }

    /**
     * Reference straight line
     *
     * @param kdist sorted array of data values (from max to min)
     * @param i
     * @param slope
     * @return
     */
    public double ref(Double[] kdist, int i, double slope) {
        return kdist[0] + (slope * i);
    }

    public int getK() {
        return k;
    }

    public double getSlope() {
        return slope;
    }

    public Double[] getKdist() {
        return kdist;
    }

    /**
     * X coordinate of the knee (elbow) point on the cure
     *
     * @return
     */
    public int getKnee() {
        return knee;
    }

    public double getEps() {
        return eps;
    }

    public double getMinEps() {
        return kdist[maxX(kdist)];
    }

    /**
     * Make sure max eps is in reasonable interval around actual eps
     *
     * @return estimated reasonable max value
     */
    public double getMaxEps() {
        return eps + (kdist[0] - eps) / 2.0;
    }

    @Override
    public double estimateRunTime(Dataset<E> dataset, Props params) {
        return Math.log(FastMath.pow(dataset.size(), 2) * dataset.attributeCount());
    }

}
