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
package org.clueminer.evaluation.inline;

import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.math.Matrix;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class MoEvaluator implements ClusterEvaluation {

    private List<ClusterEvaluation> objectives;

    @Override
    public String getName() {
        if (objectives == null) {
            return "(none)";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < objectives.size(); i++) {
                if (i > 0) {
                    sb.append(" & ");
                }
                sb.append(objectives.get(i).getName());

            }
            return sb.toString();
        }
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Props params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double score(Clustering<? extends Cluster> clusters, Matrix proximity, Props params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isBetter(double score1, double score2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compare(double score1, double score2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isExternal() {
        return true;
    }

    @Override
    public boolean isMaximized() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getMin() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getMax() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<ClusterEvaluation> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<ClusterEvaluation> objectives) {
        this.objectives = objectives;
    }

}
