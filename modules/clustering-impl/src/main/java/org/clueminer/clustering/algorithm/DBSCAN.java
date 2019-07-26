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
package org.clueminer.clustering.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.Configurator;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.api.factory.Clusterings;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.neighbor.Neighbor;
import org.clueminer.neighbor.RNNSearch;
import org.clueminer.neighbor.RnnFactory;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 * A density-based algorithm for discovering clusters in large spatial databases
 * with noise.
 *
 * @param <E>
 * @param <C>
 * @cite Ester, Martin, et al. "A density-based algorithm for discovering
 * clusters in large spatial databases with noise." Kdd. Vol. 96. No. 34. 1996.
 *
 * @author deric
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class DBSCAN<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> implements ClusteringAlgorithm<E, C> {

    public static final String NAME = "DBSCAN";

    public static final String MIN_PTS = "minPts";
    public static final String EPS = "eps";

    public static final String RNN_ALG = "rnnAlg";

    /**
     * Label for unclassified data samples.
     */
    private static final int UNCLASSIFIED = -1;
    /**
     * The minimum number of points required to form a cluster
     */
    @Param(name = MIN_PTS, description = "minimum number of points required to form a cluster", required = true, min = 1, max = 50)
    private int minPts;
    /**
     * The range of neighborhood.
     */
    @Param(name = EPS, description = "the range of a point neighborhood", required = true, min = 1e-6, max = 1000)
    private double radius;

    private RNNSearch<E> nns;

    private int k;

    public DBSCAN() {

    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Class variables can't be used in order to have thread-safe execution
     *
     * @param dataset
     * @param props
     * @return
     */
    @Override
    public Clustering<E, C> cluster(Dataset<E> dataset, Props props) {
        DBSCANconf conf = scan(dataset, props);
        Clustering res = Clusterings.newList();
        int avgSize = (int) Math.sqrt(dataset.size());
        Cluster curr;
        int clustIdx;
        if (colorGenerator != null) {
            colorGenerator.reset();
        }
        for (int i = 0; i < conf.n; i++) {
            if (conf.y[i] == OUTLIER) {
                clustIdx = k;
            } else {
                clustIdx = conf.y[i];
            }
            if (!res.hasAt(clustIdx)) {
                curr = res.createCluster(clustIdx, avgSize);
                curr.setAttributes(dataset.getAttributes());
                if (colorGenerator != null) {
                    curr.setColor(colorGenerator.next());
                }
            }
            curr = res.get(clustIdx);
            curr.add(dataset.get(i));
        }
        if (res.hasAt(k)) {
            res.get(k).setName(Algorithm.OUTLIER_LABEL);
        }
        res.lookupAdd(dataset);
        res.setParams(props);
        return res;
    }

    private class DBSCANconf {

        protected int k;
        protected int n;
        protected int minPts;
        protected double radius;
        protected int[] y;
        protected RNNSearch<E> nns;

        public DBSCANconf() {

        }
    }

    /**
     * Configure required parameters
     *
     * @param dataset
     * @param props
     * @return
     */
    public DBSCANconf scan(Dataset<E> dataset, Props props) {
        DBSCANconf conf = new DBSCANconf();
        if (!props.containsKey(MIN_PTS)) {
            throw new RuntimeException("missing parameter " + MIN_PTS + ", got: " + props.toJson());
        }
        conf.minPts = props.getInt(MIN_PTS);
        if (conf.minPts < 1) {
            throw new IllegalArgumentException("Invalid minPts: " + conf.minPts);
        }

        if (!props.containsKey(EPS)) {
            throw new RuntimeException("missing parameter " + EPS + ", got: " + props.toJson());
        }
        conf.radius = props.getDouble(EPS);
        if (conf.radius <= 0.0) {
            throw new IllegalArgumentException("Invalid radius: " + conf.radius);
        }

        conf.k = 0;

        String rnnProvider = props.get(RNN_ALG, "linear RNN");
        conf.nns = RnnFactory.getInstance().getProvider(rnnProvider);
        if (conf.nns == null) {
            throw new RuntimeException("RNN provider was not found");
        }
        conf.nns.setDataset(dataset);

        conf.n = dataset.size();
        conf.y = new int[conf.n];
        Arrays.fill(conf.y, UNCLASSIFIED);

        for (int i = 0; i < conf.n; i++) {
            if (conf.y[i] == UNCLASSIFIED) {
                //expand cluster
                List<Neighbor<E>> seeds = new ArrayList<>();
                conf.nns.range(dataset.get(i), conf.radius, seeds);
                //no core points
                if (seeds.size() < conf.minPts) {
                    conf.y[i] = OUTLIER;
                } else {
                    //all points in seeds are density-reachable from Point y[i]
                    conf.y[i] = k;
                    for (int j = 0; j < seeds.size(); j++) {
                        if (conf.y[seeds.get(j).index] == UNCLASSIFIED) {
                            conf.y[seeds.get(j).index] = conf.k;
                            Neighbor<E> neighbor = seeds.get(j);
                            List<Neighbor<E>> secondaryNeighbors = new ArrayList<>();
                            conf.nns.range(neighbor.key, conf.radius, secondaryNeighbors);

                            if (secondaryNeighbors.size() >= conf.minPts) {
                                seeds.addAll(secondaryNeighbors);
                            }
                        }

                        if (conf.y[seeds.get(j).index] == OUTLIER) {
                            conf.y[seeds.get(j).index] = conf.k;
                        }
                    }
                    conf.k++;
                }
            }
        }
        return conf;
    }

    public ArrayList<Instance> findNoise(Dataset<E> dataset, Props props) {
        DBSCANconf conf = scan(dataset, props);
        int n = dataset.size();
        ArrayList<Instance> result = null;
        for (int i = 0; i < n; i++) {
            if (conf.y[i] == OUTLIER) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(dataset.get(i));
            }
        }
        return result;
    }

    public RNNSearch<E> getNns() {
        return nns;
    }

    public void setNns(RNNSearch<E> nns) {
        this.nns = nns;
    }

    @Override
    public Configurator<E> getConfigurator() {
        return DBSCANParamEstim.getInstance();
    }

    @Override
    public boolean isDeterministic() {
        return true;
    }
}
