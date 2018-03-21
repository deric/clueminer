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
package org.clueminer.evolution.mo;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.reflect.InvocationTargetException;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;

/**
 *
 * @author Tomas Barton
 */
public class MoProblem extends BaseIntProblem implements IntegerProblem {

    private static final long serialVersionUID = 5458227476117018712L;

    protected final MoEvolution evolution;
    private static final Logger LOG = LoggerFactory.getLogger(MoProblem.class);

    public MoProblem(MoEvolution evolution) {
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
        params = algorithm.getParameters();
        mapping = new Int2ObjectOpenHashMap(params.length);
        int i = 0, size;
        int combinations = 1;
        lowerLimit = new int[params.length];
        upperLimit = new int[params.length];
        for (Parameter p : params) {
            try {
                mapping.put(i, p.getName());
                LOG.info("param {}: {}", i, p.getName());
                lowerLimit[i] = 0;
                switch (p.getType()) {
                    case STRING:
                        ServiceFactory f = getFactory(p);
                        //indexed from zero, must be size - 1
                        size = f.getAll().size();
                        upperLimit[i] = size - 1;
                        combinations *= size;
                        LOG.info("possible values: {}", size);
                        break;
                    case BOOLEAN:
                        upperLimit[i] = 1;
                        combinations *= 2;
                        LOG.info("possible values: {}", 2);
                        break;
                    default:
                        throw new RuntimeException(p.getType() + " is not supported yet (param: " + p.getName() + ")");
                }

                i++;
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        LOG.info("number of combinations = {}", combinations);
        setNumberOfVariables(params.length);
    }

    @Override
    public ClusteringAlgorithm getAlgorithm() {
        return evolution.getAlgorithm();
    }

    @Override
    public ClusterEvaluation getObjective(int idx) {
        return evolution.getObjective(idx);
    }

    public Dataset<? extends Instance> getDataset() {
        return evolution.getDataset();
    }

    public ClusterEvaluation getExternal() {
        return evolution.getExternal();
    }

    @Override
    public boolean iskLimited() {
        return evolution.iskLimited();
    }
}
