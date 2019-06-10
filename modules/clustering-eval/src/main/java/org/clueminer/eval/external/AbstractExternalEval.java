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
package org.clueminer.eval.external;

import com.google.common.collect.Table;
import java.io.Serializable;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ExternalEvaluator;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.eval.AbstractComparator;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public abstract class AbstractExternalEval<E extends Instance, C extends Cluster<E>> extends AbstractComparator<E, C>
        implements ClusterEvaluation<E, C>, ExternalEvaluator<E, C>, Serializable {

    private static final long serialVersionUID = 7150802573224388450L;

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
        return true;
    }

    /**
     * Determines whether first score is better than the second
     *
     * @param score1
     * @param score2
     * @return true if score1 is better than score2
     */
    @Override
    public boolean isBetter(double score1, double score2) {
        if (isMaximized()) {
            return score1 > score2;
        }
        return score1 < score2;
    }

    /**
     * Sum occurrences of given cluster in all classes
     *
     * @param contTable
     * @param klass
     * @return
     */
    protected double sumKlass(Table<String, String, Integer> contTable, String clust) {
        double sum = 0.0;
        for (String klass : contTable.columnKeySet()) {
            sum += value(contTable, clust, klass);
        }
        return sum;
    }

    protected double sumCluster(Table<String, String, Integer> contTable, String klass) {
        double sum = 0.0;
        for (String clust : contTable.rowKeySet()) {
            sum += value(contTable, clust, klass);
        }
        return sum;
    }

    /**
     * Retrieve value from contingency table as Double
     *
     * @param contTable
     * @param cluster
     * @param klass
     * @return
     */
    protected double value(Table<String, String, Integer> contTable, String cluster, String klass) {
        Integer i = contTable.get(cluster, klass);
        if (i == null) {
            return 0.0;
        }
        return i.doubleValue();
    }

    protected double HK(Table<String, String, Integer> contTable, int n) {
        double h_c = 0.0;
        double skall;
        for (String cluster : contTable.rowKeySet()) {
            skall = sumKlass(contTable, cluster) / (double) n;
            h_c += skall * Math.log(skall);
        }
        return h_c;
    }

    protected double HC(Table<String, String, Integer> contTable, int n) {
        double h_c = 0.0;
        double scall;
        for (String klass : contTable.columnKeySet()) {
            scall = sumCluster(contTable, klass) / (double) n;
            h_c += scall * Math.log(scall);
        }
        return h_c;
    }

}
