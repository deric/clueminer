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

import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.mo.BaseIntProblem;
import static org.clueminer.evolution.mo.BaseIntProblem.getFactory;
import org.clueminer.evolution.mo.MoSolution;
import org.clueminer.utils.Props;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Exceptions;
import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;

/**
 *
 * @author deric
 */
public class KmProblem extends BaseIntProblem implements IntegerProblem {

    private static final long serialVersionUID = 8835095132879570407L;

    protected final KmEvolution evolution;
    private static final Logger logger = Logger.getLogger(KmProblem.class.getName());

    private HashSet<String> blacklist = new HashSet<>();

    public KmProblem(KmEvolution evolution, ClusteringAlgorithm alg) {
        this.evolution = evolution;
        setNumberOfObjectives(evolution.getNumObjectives());
        initializeGenomMapping(evolution.getAlgorithm());
        exec = new ClusteringExecutorCached(alg);
    }

    public KmProblem(KmEvolution evolution, ClusteringAlgorithm alg, HashSet<String> blacklist, Props defaultProps) {
        this.evolution = evolution;
        this.blacklist = blacklist;
        this.defaultProp = defaultProps;
        setNumberOfObjectives(evolution.getNumObjectives());
        initializeGenomMapping(evolution.getAlgorithm());
        exec = new ClusteringExecutorCached(alg);
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
        ArrayList<Parameter> lp = new ArrayList<>();
        ArrayList<Integer> lower = new ArrayList<>();
        ArrayList<Integer> upper = new ArrayList<>();
        int i = 0, size;
        int combinations = 1;
        for (Parameter p : params) {
            try {
                if (!blacklist.contains(p.getName())) {
                    mapping.put(i, p.getName());
                    lp.add(i, p);
                    logger.log(Level.INFO, "param {0}: {1}", new Object[]{i, p.getName()});
                    switch (p.getType()) {
                        case STRING:
                            ServiceFactory f = getFactory(p);
                            //indexed from zero, must be size - 1
                            size = f.getAll().size();
                            lower.add(i, 0);
                            upper.add(i, size - 1);
                            combinations *= size;
                            logger.log(Level.INFO, "possible values: {0}", size);
                            break;
                        case BOOLEAN:
                            lower.add(i, 0);
                            upper.add(i, 1);
                            combinations *= 2;
                            logger.log(Level.INFO, "possible values: {0}", 2);
                            break;
                        case INTEGER:
                            logger.log(Level.INFO, "min: {0}", p.getMin());
                            if (!Double.isNaN(p.getMin())) {
                                lower.add(i, (int) p.getMin());
                            }
                            System.out.println("default prop: " + defaultProp);
                            if (defaultProp != null && "k".equals(p.getName()) && defaultProp.containsKey("max_k")) {
                                upper.add(i, defaultProp.getInt("max_k"));
                            } else {
                                logger.log(Level.INFO, "max: {0}", p.getMax());
                                if (!Double.isNaN(p.getMax())) {
                                    upper.add(i, (int) p.getMax());
                                }
                            }
                            int pos = upper.get(i) - lower.get(i);
                            combinations *= pos;
                            logger.log(Level.INFO, "possible values: {0}", pos);
                            break;
                        default:
                            throw new RuntimeException(p.getType() + " is not supported yet (param: " + p.getName() + ")");
                    }
                    i++;
                }
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        logger.log(Level.INFO, "number of combinations = {0}", combinations);
        setNumberOfVariables(lp.size());
        params = lp.toArray(new Parameter[0]);
        lowerLimit = Ints.toArray(lower);
        upperLimit = Ints.toArray(upper);
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

    public void setParamBlacklist(HashSet<String> blacklist) {
        this.blacklist = blacklist;
    }
}
