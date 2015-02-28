/*
 * Copyright (C) 2015 clueminer.org
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
package org.clueminer.oo.api;

import java.io.Serializable;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.utils.Props;

/**
 * An interface for representing solution of an objective optimization problem
 *
 * @author deric
 * @param <T> type of stored properties
 */
public interface OpSolution<T> extends Serializable {

    void setObjective(int index, double value);

    /**
     * Value of the objective function
     *
     * @param index
     * @return
     */
    double getObjective(int index);

    T getVariableValue(int index);

    void setVariableValue(int index, T value);

    String getVariableValueString(int index);

    int getNumberOfVariables();

    int getNumberOfObjectives();

    double getOverallConstraintViolationDegree();

    void setOverallConstraintViolationDegree(double violationDegree);

    int getNumberOfViolatedConstraints();

    void setNumberOfViolatedConstraints(int numberOfViolatedConstraints);

    void setAttribute(Object id, Object value);

    Object getAttribute(Object id);

    /**
     * *
     * TODO: we might move methods bellow to ClusteringSolution interface which
     * would extends generalized objective optimization problem
     */
    /**
     * Some mutations might bring us to invalid state
     *
     * @return true when fitness could be counted
     */
    boolean isValid();

    /**
     * Algorithm properties
     *
     * @return
     */
    Props getProps();

    /**
     *
     * @param clustering
     * @return
     */
    EvaluationTable evaluationTable(Clustering<? extends Cluster> clustering);

    /**
     * Run clustering according to its genom
     *
     * @return resulting clustering
     */
    Clustering<? extends Cluster> updateCustering();

    /**
     * From encoded information about clustering should generate real
     * assignments to clusters
     *
     * @return final clustering
     */
    Clustering<? extends Cluster> getClustering();
}
