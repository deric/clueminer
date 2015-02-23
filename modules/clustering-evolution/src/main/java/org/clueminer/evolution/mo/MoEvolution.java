package org.clueminer.evolution.mo;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Executor;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionMO;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.multim.MultiMuteEvolution;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.selection.NaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.comparator.DominanceComparator;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Evolution.class)
public class MoEvolution extends MultiMuteEvolution implements Runnable, EvolutionMO, Lookup.Provider {

    private static final String name = "MOE";
    private static final Logger logger = Logger.getLogger(MoEvolution.class.getName());
    protected List<ClusterEvaluation> objectives;
    private int numSolutions = 15;

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

    @Override
    public void addObjective(ClusterEvaluation eval) {
        objectives.add(eval);
    }

    @Override
    public void removeObjective(ClusterEvaluation eval) {
        objectives.remove(eval);
    }

    public ClusterEvaluation getObjective(int idx) {
        return objectives.get(idx);
    }

    @Override
    public List<ClusterEvaluation> getObjectives() {
        return objectives;
    }

    @Override
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
        if (getNumObjectives() < 2) {
            throw new RuntimeException("provide at least 2 objectives. currently we have just" + getNumObjectives());
        }
        logger.log(Level.INFO, "starting evolution {0}", getName());
        logger.log(Level.INFO, "variables: ", problem.getNumberOfVariables());
        logger.log(Level.INFO, "objectives: ", getNumObjectives());
        for (int i = 0; i < getNumObjectives(); i++) {
            logger.log(Level.INFO, "objective {0}: {1}", new Object[]{i, getObjective(i).getName()});
        }

        double crossoverDistributionIndex = 20.0;
        crossover = new IntegerSBXCrossover(getCrossoverProbability(), crossoverDistributionIndex);

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutation = new IntegerPolynomialMutation(mutationProbability, mutationDistributionIndex);

        selection = new NaryTournamentSelection(numSolutions, new DominanceComparator());

        moAlg = new NSGAIIBuilder(problem)
                .setCrossoverOperator(crossover)
                .setMutationOperator(mutation)
                .setSelectionOperator(selection)
                .setMaxIterations(this.getGenerations())
                .setPopulationSize(this.getPopulationSize())
                //.setSolutionListEvaluator(new MultithreadedSolutionListEvaluator(8, problem))
                .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(moAlg).execute();

        List<Solution> moPop = ((NSGAII) moAlg).getResult();
        int i = 0;
        for (Solution s : moPop) {
            System.out.print(i + ": ");
            for (int j = 0; j < getNumObjectives(); j++) {
                if (j > 0) {
                    System.out.print(", ");
                }
                System.out.print(s.getObjective(j));
            }
            System.out.print("\n");
            System.out.println("prop: " + ((MoSolution) s).getIndividual().getProps().toString());
            i++;
        }
        long computingTime = algorithmRunner.getComputingTime();
        System.out.println("computing time: " + computingTime);
        /*
         int numberOfDimensions = getNumObjectives();
         Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
         Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

         Hypervolume hypervolume = new Hypervolume();
         hypervolume.execute(frontA, frontB);*/

        Individual[] pop = new Individual[moPop.size()];
        for (int j = 0; j < moPop.size(); j++) {
            MoSolution b = (MoSolution) moPop.get(j);
            pop[j] = b.getIndividual();
        }

        fireResultUpdate(pop);
    }

    @Override
    public void removeAll() {
        if (objectives != null && !objectives.isEmpty()) {
            objectives.clear();
        }
    }

}
