package org.clueminer.evolution.mo;

import org.clueminer.evolution.singlem.SingleMuteIndividual;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author Tomas Barton
 */
public class MoSolution implements Solution<String> {

    private final SingleMuteIndividual individual;
    private final MoProblem problem;
    private double[] objectives;

    public MoSolution(MoProblem problem, SingleMuteIndividual individual) {
        this.individual = individual;
        this.problem = problem;
        this.objectives = new double[problem.evolution.getNumObjectives()];
    }

    public void evaluate() {

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
    public String getVariableValue(int index) {
        return individual.getGen(problem.getVar(index));
    }

    @Override
    public void setVariableValue(int index, String value) {
        individual.setAlgorithm(null);
    }

    @Override
    public String getVariableValueString(int index) {
        return individual.getGen(problem.getVar(index));
    }

    @Override
    public int getNumberOfVariables() {
        return problem.getNumVars();
    }

    @Override
    public int getNumberOfObjectives() {
        return objectives.length;
    }

    @Override
    public double getOverallConstraintViolationDegree() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOverallConstraintViolationDegree(double violationDegree) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumberOfViolatedConstraints() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setNumberOfViolatedConstraints(int numberOfViolatedConstraints) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Solution copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttribute(Object id, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getAttribute(Object id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
