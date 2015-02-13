package org.clueminer.evolution.mo;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.evolution.singlem.SingleMuteIndividual;
import static org.clueminer.evolution.singlem.SingleMuteIndividual.getFactory;
import org.clueminer.utils.Props;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Exceptions;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.AbstractGenericSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 *
 * @author Tomas Barton
 */
public class MoSolution implements IntegerSolution, Solution<Integer> {

    private static final long serialVersionUID = -523309284446031981L;

    private final SingleMuteIndividual individual;
    private final MoProblem problem;
    private double[] objectives;
    private int[] variables;
    private double overallConstraintViolationDegree = 0;
    private int numberOfViolatedConstraints = 0;
    protected final JMetalRandom randomGenerator;
    protected Map<Object, Object> attributes;

    public MoSolution(MoProblem problem, SingleMuteIndividual individual) {
        randomGenerator = JMetalRandom.getInstance();
        this.individual = individual;
        this.problem = problem;
        this.variables = new int[problem.getNumberOfVariables()];
        attributes = new HashMap<>();

        objectives = new double[problem.getNumberOfObjectives()];

        numberOfViolatedConstraints = 0;
        overallConstraintViolationDegree = 0.0;

        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            variables[i] = randomGenerator.nextInt(problem.getLowerBound(i), problem.getUpperBound(i));
        }

        individual.updateCustering();
    }

    public void evaluate() {
        for (int i = 0; i < objectives.length; i++) {
            objectives[i] = individual.countFitness(problem.evolution.getObjective(i));
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
        return individual.getGen(problem.getVar(index));
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
        MoSolution copy = new MoSolution(problem, individual.deepCopy());
        int i = 0;
        for (int o : variables) {
            copy.variables[i] = o;
            i++;
        }
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
            Props prop = individual.getProps();
            Parameter param = problem.params[id];
            switch (param.getType()) {
                case STRING:
                    ServiceFactory f = getFactory(param);
                    List<String> list = f.getProviders();
                    prop.put(param.getName(), list.get(value));
                    break;
                case BOOLEAN:
                    prop.putBoolean(param.getName(), (value != 0));
                    break;

                default:
                    throw new RuntimeException(param.getType() + " is not supported yet");
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (update) {
            individual.updateCustering();
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

}
