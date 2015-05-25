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
package org.clueminer.bagging;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.logging.Logger;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.mo.BaseIntProblem;
import org.clueminer.evolution.mo.MoSolution;
import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;

/**
 *
 * @author deric
 */
public class KmProblem extends BaseIntProblem implements IntegerProblem {

    protected final KmEvolution evolution;
    private static final Logger logger = Logger.getLogger(KmProblem.class.getName());

    public KmProblem(KmEvolution evolution) {
        this.evolution = evolution;
        setNumberOfObjectives(evolution.getNumObjectives());
        initializeGenomMapping(evolution.getAlgorithm());
        exec = new ClusteringExecutorCached();
    }

    @Override
    public void evaluate(IntegerSolution solution) {
        ((MoSolution) solution).evaluate();
    }

    @Override
    public IntegerSolution createSolution() {
        return (IntegerSolution) new MoSolution(this);
    }

    private void initializeGenomMapping(ClusteringAlgorithm algorithm) {
        int size = 2;
        mapping = new Int2ObjectOpenHashMap(size);
        lowerLimit = new int[size];
        upperLimit = new int[size];
        mapping.put(0, "k");
        lowerLimit[0] = 2;
        upperLimit[0] = 20;
        mapping.put(1, "iterations");
        lowerLimit[1] = 100;
        upperLimit[1] = 200;

        setNumberOfVariables(size);
    }

    @Override
    public ClusteringAlgorithm getAlgorithm() {
        return evolution.getAlgorithm();
    }

    @Override
    public ClusterEvaluation getObjective(int idx) {
        return evolution.getObjective(idx);
    }

    @Override
    public Dataset<? extends Instance> getDataset() {
        return evolution.getDataset();
    }

    @Override
    public ClusterEvaluation getExternal() {
        return evolution.getExternal();
    }

    @Override
    public boolean iskLimited() {
        return evolution.iskLimited();
    }
}
