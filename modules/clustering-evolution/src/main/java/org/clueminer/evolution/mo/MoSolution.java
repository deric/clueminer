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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.HashEvaluationTable;
import org.clueminer.oo.api.OpSolution;
import org.clueminer.utils.Props;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.AbstractGenericSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class MoSolution<E extends Instance, C extends Cluster<E>> implements IntegerSolution, Solution<Integer>, OpSolution<Integer, E, C> {

    private static final long serialVersionUID = -523309284446031981L;

    protected Clustering<E, C> clustering;
    protected ClusteringAlgorithm algorithm;
    protected Props genom;
    private final BaseIntProblem problem;
    private final double[] objectives;
    private final int[] variables;
    private double overallConstraintViolationDegree = 0;
    private int numberOfViolatedConstraints = 0;
    protected final JMetalRandom randomGenerator;
    protected Map<Object, Object> attributes;
    private static final Logger LOG = LoggerFactory.getLogger(MoSolution.class);
    private static int counter = 0;

    public MoSolution(BaseIntProblem problem) {
        randomGenerator = JMetalRandom.getInstance();
        this.problem = problem;
        this.variables = new int[problem.getNumberOfVariables()];
        attributes = new HashMap<>();
        algorithm = problem.getAlgorithm();
        objectives = new double[problem.getNumberOfObjectives()];
        genom = problem.getDefaultProps();

        numberOfViolatedConstraints = 0;
        overallConstraintViolationDegree = 0.0;

        int value;
        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            value = randomGenerator.nextInt(problem.getLowerBound(i), problem.getUpperBound(i));
            setVariableValue(i, value, false);
        }
        LOG.info("created solution: {}", genom.toString());
        updateCustering();
    }

    /**
     * Copying constructor
     *
     * @param problem
     * @param other
     */
    public MoSolution(BaseIntProblem problem, MoSolution other) {
        randomGenerator = other.randomGenerator;

        this.problem = problem;
        this.variables = new int[problem.getNumberOfVariables()];
        attributes = new HashMap<>();
        algorithm = problem.getAlgorithm();
        objectives = new double[problem.getNumberOfObjectives()];
        genom = other.genom.clone();
        LOG.info("created solution: {}", genom.toString());

        numberOfViolatedConstraints = 0;
        overallConstraintViolationDegree = 0.0;

        System.arraycopy(other.variables, 0, variables, 0, problem.getNumberOfVariables());
        clustering = other.clustering;
    }

    public void evaluate() {
        updateCustering();
        ClusterEvaluation eval;
        for (int i = 0; i < objectives.length; i++) {
            eval = problem.getObjective(i);
            if (eval.isMaximized()) {
                objectives[i] = -countFitness(eval);
            } else {
                objectives[i] = countFitness(eval);
            }
        }
    }

    @Override
    public void setObjective(int index, double value) {
        objectives[index] = value;
    }

    @Override
    public double getObjective(int index) {
        return objectives[index];
    }

    @Override
    public Integer getVariableValue(int index) {
        return (int) variables[index];
        //return individual.getGen(problem.getVar(index));
    }

    @Override
    public void setVariableValue(int index, Integer value) {
        setVariableValue(index, value, true);
    }

    @Override
    public String getVariableValueString(int index) {
        return getGen(problem.getVar(index));
    }

    @Override
    public int getNumberOfVariables() {
        return problem.getNumberOfVariables();
    }

    @Override
    public int getNumberOfObjectives() {
        return objectives.length;
    }

    @Override
    public double getOverallConstraintViolationDegree() {
        return overallConstraintViolationDegree;
    }

    @Override
    public void setOverallConstraintViolationDegree(double violationDegree) {
        this.overallConstraintViolationDegree = violationDegree;
    }

    @Override
    public int getNumberOfViolatedConstraints() {
        return numberOfViolatedConstraints;
    }

    @Override
    public void setNumberOfViolatedConstraints(int numberOfViolatedConstraints) {
        this.numberOfViolatedConstraints = numberOfViolatedConstraints;
    }

    @Override
    public Solution copy() {
        MoSolution copy = new MoSolution(problem, this);
        return copy;
    }

    /**
     * Update individual
     *
     * @param id
     * @param value
     * @param update
     */
    public final void setVariableValue(int id, int value, boolean update) {
        try {
            Parameter param = problem.params[id];
            switch (param.getType()) {
                case STRING:
                    ServiceFactory f = param.getFactory();
                    List<String> list = f.getProviders();
                    genom.put(param.getName(), list.get(value));
                    //check if configuration makes sense
                    while (!isValid()) {
                        LOG.trace("mutated from {}  with invalid value: {}", param.getName(), list.get(value));
                        int newValue;
                        do {
                            newValue = randomGenerator.nextInt(problem.getLowerBound(id), problem.getUpperBound(id));
                        } while (newValue == value);
                        genom.put(param.getName(), list.get(newValue));
                        value = newValue;
                    }
                    LOG.trace("mutated {} to {}", param.getName(), list.get(value));
                    break;
                case BOOLEAN:
                    LOG.trace("mutated {} to !{}", param.getName(), value);
                    genom.putBoolean(param.getName(), (value != 0));
                    break;
                case INTEGER:
                    LOG.trace("mutated {} to {}", param.getName(), value);
                    genom.putInt(param.getName(), value);
                    break;
                default:
                    throw new RuntimeException(param.getType() + " is not supported yet");
            }

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        //finally set variable value
        variables[id] = value;
        if (update) {
            updateCustering();
        }
    }

    @Override
    public void setAttribute(Object id, Object value) {
        attributes.put(id, value);
    }

    @Override
    public Object getAttribute(Object id) {
        return attributes.get(id);
    }

    @Override
    public String toString() {
        String result = "Variables: ";
        for (int var : variables) {
            result += "" + var + " ";
        }
        result += "Objectives: ";
        for (Double obj : objectives) {
            result += "" + obj + " ";
        }
        result += "\t";
        result += "AlgorithmAttributes: " + attributes + "\n";

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractGenericSolution)) {
            return false;
        }

        MoSolution that = (MoSolution) o;

        if (Double.compare(that.overallConstraintViolationDegree, overallConstraintViolationDegree)
                != 0) {
            return false;
        }
        if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null) {
            return false;
        }
        if (objectives != null ? !Arrays.equals(objectives, that.objectives) : that.objectives != null) {
            return false;
        }
        return !(variables != null ? !Arrays.equals(variables, that.variables) : that.variables != null);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = objectives != null ? Arrays.hashCode(objectives) : 0;
        result = 31 * result + (variables != null ? Arrays.hashCode(variables) : 0);
        //result = 31 * result + (problem != null ? problem.hashCode() : 0);
        temp = Double.doubleToLongBits(overallConstraintViolationDegree);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }

    @Override
    public Integer getLowerBound(int index) {
        return problem.getLowerBound(index);
    }

    @Override
    public Integer getUpperBound(int index) {
        return problem.getUpperBound(index);
    }

    /**
     * Perform mutation of randomly selected attribute and don't update
     * clustering
     */
    private void randomMutation() {
        int id = randomGenerator.nextInt(0, problem.getNumberOfVariables() - 1);
        int value = randomGenerator.nextInt(problem.getLowerBound(id), problem.getUpperBound(id));
        setVariableValue(id, value, false);
    }

    /**
     * Check if configuration is within constrains (could possibly produce some
     * result)
     *
     * @return
     */
    @Override
    public boolean isValid() {
        boolean ret = true;
        if (algorithm instanceof AgglomerativeClustering) {
            AgglomerativeClustering aggl = (AgglomerativeClustering) algorithm;
            ret = ret && aggl.isLinkageSupported(genom.get(AlgParams.LINKAGE));
        }
        return ret;
    }

    /**
     *
     * @param clust
     * @return
     */
    public boolean isValid(Clustering clust) {
        boolean ret = true;
        if (clustering == null) {
            return false;
        }

        if (clustering.size() < 2) {
            //we don't want solutions with 0 or 1 cluster
            return false;
        }

        Dataset<? extends Instance> dataset = problem.getDataset();
        //strange clustering with missing items
        if (clustering.instancesCount() != dataset.size()) {
            return false;
        }

        //limit maximum size of clusters allowed
        if (problem.iskLimited()) {
            if (clustering.size() > Math.sqrt(dataset.size())) {
                return false;
            }
        }

        return ret;
    }

    @Override
    public Props getProps() {
        return genom;
    }

    /**
     * Hash table with various evaluations scores (eliminates repeated
     * computations)
     *
     * @param clustering
     * @return
     */
    @Override
    public EvaluationTable<E, C> evaluationTable(Clustering<E, C> clustering) {
        EvaluationTable evalTable = clustering.getEvaluationTable();
        //we try to compute score just once, to eliminate delays
        if (evalTable == null) {
            Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
            if (dataset == null) {
                throw new RuntimeException("no dataset associated with clustering");
            }
            evalTable = new HashEvaluationTable(clustering, dataset);
            clustering.setEvaluationTable(evalTable);
        }
        return evalTable;
    }

    @Override
    public final Clustering<E, C> updateCustering() {
        LOG.debug("starting clustering {}", genom.toString());
        //count number of clustering algorithm executions
        counter++;
        clustering = problem.exec.clusterRows(problem.getDataset(), genom);
        while (!isValid(clustering)) {
            randomMutation();
            counter++;
            clustering = problem.exec.clusterRows(problem.getDataset(), genom);
        }

        ClusterEvaluation<E, C> eval = problem.getExternal();
        if (eval != null) {
            LOG.debug("finished clustering, supervised score ({}): {}", eval.getName(), countFitness(eval));
        }
        return clustering;
    }

    @Override
    public Clustering<E, C> getClustering() {
        return clustering;
    }

    /**
     * Clustering should be updated after each mutation
     *
     * @param eval
     * @return
     */
    public double countFitness(ClusterEvaluation eval) {
        if (clustering == null) {
            updateCustering();
        }
        EvaluationTable et = evaluationTable(clustering);
        if (et == null) {
            throw new RuntimeException("missing eval table");
        }
        return et.getScore(eval);
    }

    public String getGen(String key) {
        return genom.get(key);
    }

    public void setGen(String key, String value) {
        genom.put(key, value);
    }

    public static int getSolutionsCount() {
        return counter;
    }

    public static void setSolutionsCount(int cnt) {
        counter = cnt;
    }
}
