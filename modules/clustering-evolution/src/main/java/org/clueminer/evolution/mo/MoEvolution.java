package org.clueminer.evolution.mo;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.logging.Logger;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Executor;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.multim.MultiMuteEvolution;
import org.openide.util.Lookup;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;

/**
 *
 * @author Tomas Barton
 */
public class MoEvolution extends MultiMuteEvolution implements Runnable, Evolution, Lookup.Provider {

    private static final String name = "MOE";
    private static final Logger logger = Logger.getLogger(MoEvolution.class.getName());
    protected List<ClusterEvaluation> objectives;
    private int numberOfPoints = 10;

    public MoEvolution() {
        init(new ClusteringExecutorCached());
    }

    public MoEvolution(Executor executor) {
        init(executor);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected void prepareHook() {
        this.objectives = Lists.newLinkedList();
    }

    public void addObjective(ClusterEvaluation eval) {
        objectives.add(eval);
    }

    public void removeObjective(ClusterEvaluation eval) {
        objectives.remove(eval);
    }

    public ClusterEvaluation getObjective(int idx) {
        return objectives.get(idx);
    }

    public List<ClusterEvaluation> getObjectives() {
        return objectives;
    }

    public int getNumObjectives() {
        return objectives.size();
    }

    @Override
    public void run() {

        Problem problem = new MoProblem(this);
        Algorithm moAlg;
        CrossoverOperator crossover;
        MutationOperator mutation;
        SelectionOperator selection;

        double crossoverProbability = 0.9;
        double crossoverDistributionIndex = 20.0;
        crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        selection = new BinaryTournamentSelection();

        moAlg = new NSGAIIBuilder(problem)
                .setCrossoverOperator(crossover)
                .setMutationOperator(mutation)
                .setSelectionOperator(selection)
                .setMaxIterations(250)
                .setPopulationSize(100)
                .setSolutionListEvaluator(new MultithreadedSolutionListEvaluator(8, problem))
                .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(moAlg).execute();

        // List<Solution> moPop = ((NSGAII) moAlg).getResult();
        long computingTime = algorithmRunner.getComputingTime();
        System.out.println("computing time: " + computingTime);
        /*
         int numberOfDimensions = getNumObjectives();
         Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
         Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

         Hypervolume hypervolume = new Hypervolume();
         hypervolume.execute(frontA, frontB);*/

    }

}
