package org.clueminer.evolution.mo;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.HashEvaluationTable;
import static org.clueminer.evolution.singlem.SingleMuteIndividual.getFactory;
import org.clueminer.oo.api.OpSolution;
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
public class MoSolution implements IntegerSolution, Solution<Integer>, OpSolution<Integer> {

    private static final long serialVersionUID = -523309284446031981L;

    protected Clustering<? extends Cluster> clustering;
    protected ClusteringAlgorithm algorithm;
    protected Props genom;
    private final MoProblem problem;
    private final double[] objectives;
    private final int[] variables;
    private double overallConstraintViolationDegree = 0;
    private int numberOfViolatedConstraints = 0;
    protected final JMetalRandom randomGenerator;
    protected Map<Object, Object> attributes;
    private static final Logger logger = Logger.getLogger(MoSolution.class.getName());
    private static int counter = 0;

    public MoSolution(MoProblem problem) {
        randomGenerator = JMetalRandom.getInstance();
        this.problem = problem;
        this.variables = new int[problem.getNumberOfVariables()];
        attributes = new HashMap<>();
        algorithm = problem.evolution.getAlgorithm();
        objectives = new double[problem.getNumberOfObjectives()];
        genom = new Props();

        numberOfViolatedConstraints = 0;
        overallConstraintViolationDegree = 0.0;

        int value;
        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            value = randomGenerator.nextInt(problem.getLowerBound(i), problem.getUpperBound(i));
            setVariableValue(i, value, false);
        }
        updateCustering();
        counter++;
    }

    /**
     * Copying constructor
     *
     * @param problem
     * @param other
     */
    public MoSolution(MoProblem problem, MoSolution other) {
        randomGenerator = other.randomGenerator;
        this.problem = problem;
        this.variables = new int[problem.getNumberOfVariables()];
        attributes = new HashMap<>();
        algorithm = problem.evolution.getAlgorithm();
        objectives = new double[problem.getNumberOfObjectives()];
        genom = other.genom.clone();

        numberOfViolatedConstraints = 0;
        overallConstraintViolationDegree = 0.0;

        System.arraycopy(other.variables, 0, variables, 0, problem.getNumberOfVariables());
        clustering = other.clustering;
        counter++;
    }

    public void evaluate() {
        updateCustering();
        ClusterEvaluation eval;
        for (int i = 0; i < objectives.length; i++) {
            eval = problem.evolution.getObjective(i);
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
                    ServiceFactory f = getFactory(param);
                    List<String> list = f.getProviders();
                    genom.put(param.getName(), list.get(value));
                    //check if configuration makes sense
                    while (!isValid()) {
                        logger.log(Level.INFO, "mutated from invalid config{0} to {1}", new Object[]{param.getName(), list.get(value)});
                        value = randomGenerator.nextInt(problem.getLowerBound(id), problem.getUpperBound(id));
                        genom.put(param.getName(), list.get(value));
                    }
                    logger.log(Level.FINE, "mutated {0} to {1}", new Object[]{param.getName(), list.get(value)});
                    break;
                case BOOLEAN:
                    logger.log(Level.FINE, "mutated {0} to !{1}", new Object[]{param.getName(), value});
                    genom.putBoolean(param.getName(), (value != 0));
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
            ret = ret && aggl.isLinkageSupported(genom.get(AgglParams.LINKAGE));
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
    public EvaluationTable evaluationTable(Clustering<? extends Cluster> clustering) {
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
    public final Clustering<? extends Cluster> updateCustering() {
        logger.log(Level.FINE, "starting clustering {0}", genom.toString());
        clustering = problem.exec.clusterRows(problem.evolution.getDataset(), genom);
        ClusterEvaluation eval = problem.evolution.getExternal();
        if (eval != null) {
            logger.log(Level.FINE, "finished clustering, supervised score ({0}): {1}", new Object[]{eval.getName(), countFitness(eval)});
        }
        return clustering;
    }

    @Override
    public Clustering<? extends Cluster> getClustering() {
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
