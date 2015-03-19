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
package org.clueminer.eval.external;

import com.google.common.collect.Sets;
import java.util.Set;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class NMIsum extends NMIbase implements ClusterEvaluation {

    private static final String name = "NMIsum";

    @Override
    public String getName() {
        return name;
    }

    /**
     * We want to compare two clusterings to evaluate how similar they are
     *
     * @param c1
     * @param c2
     * @param params
     * @return
     */
    @Override
    public double score(Clustering<Cluster> c1, Clustering<Cluster> c2, Props params) {
        double nmi = 0.0;
        if (c1.size() == 0 || c2.size() == 0) {
            return nmi;
        }
        int instancesCnt = c1.instancesCount();

        if (c1.instancesCount() != c2.instancesCount()) {
            throw new RuntimeException("clusterings have different numbers of instances");
        }

        double c1entropy = entropy(c1.instancesCount(), c1.clusterSizes());
        double c2entropy = entropy(c2.instancesCount(), c2.clusterSizes());

        double mutualInformation = 0;
        int common;
        for (Cluster<Instance> a : c1) {
            final int clusterSize = a.size();
            for (Cluster<Instance> b : c2) {
                Set<Instance> intersection = Sets.intersection(a, b);
                common = intersection.size();
                //System.out.println("a = " + a.getName() + ", b = " + b.getName());
                //System.out.println("common = " + common);

                if (common > 0) {
                    mutualInformation += (common / (double) instancesCnt)
                            * Math.log(instancesCnt
                                    * common / (double) (clusterSize * b.size()));
                }
            }
        }

        nmi = 2 * mutualInformation / (c1entropy + c2entropy);

        return nmi;
    }

}
