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
package org.clueminer.eval.sort;

import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 * Compare using precomputed ranks
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class RankComparator<E extends Instance, C extends Cluster<E>> implements ClusterEvaluation<E, C> {

    protected List<ClusterEvaluation> objectives;
    protected String name = "Rank";
    protected String propName = "rank";

    public void configure(String name, String propName) {
        this.name = name;
        this.propName = propName;
    }

    @Override
    public String getName() {
        if (objectives == null) {
            return "(none)";
        } else {
            return name + " (" + printObjectives() + ")";
        }
    }

    protected String printObjectives() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (ClusterEvaluation ce : objectives) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(ce.getName());
            i++;
        }
        return sb.toString();
    }

    @Override
    public double score(Clustering<E, C> clusters) {
        return clusters.getParams().getDouble(propName);
    }

    @Override
    public double score(Clustering<E, C> clusters, Props params) {
        return clusters.getParams().getDouble(propName);
    }

    @Override
    public double score(Clustering<E, C> clusters, Matrix proximity, Props params) {
        return clusters.getParams().getDouble(propName);
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        return score1 < score2;
    }

    @Override
    public int compare(double s1, double s2) {
        if (s1 < s2) {
            return 1;
        } else if (s1 == s2) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public boolean isExternal() {
        return false;
    }

    @Override
    public boolean isMaximized() {
        return false;
    }

    @Override
    public double getMin() {
        return Double.MAX_VALUE;
    }

    @Override
    public double getMax() {
        return 0.0;
    }

    public List<ClusterEvaluation> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<ClusterEvaluation> objectives) {
        this.objectives = objectives;
    }

    @Override
    public String getHandle() {
        String h = getName().toLowerCase();
        h = h.replace(" ", "_"); //space
        h = h.replace("-", "_");
        h = h.replace("+", "_");
        h = h.replace("&", "_");
        return h;
    }

    @Override
    public int compare(Clustering<E, C> c1, Clustering<E, C> c2) {
        return compare(score(c1), score(c2));
    }

}
