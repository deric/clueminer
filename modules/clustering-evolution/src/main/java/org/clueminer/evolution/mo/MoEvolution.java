package org.clueminer.evolution.mo;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Executor;
import org.clueminer.events.ListenerList;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionMO;
import org.clueminer.evolution.multim.MultiMuteEvolution;
import org.clueminer.oo.api.OpListener;
import org.clueminer.oo.api.OpSolution;
import org.openide.util.Exceptions;
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
    private int numSolutions = 5;
    private boolean kLimit;
    protected final transient ListenerList<OpListener> moListeners = new ListenerList<>();

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

    @Override
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
        logger.log(Level.INFO, "variables: {0}", problem.getNumberOfVariables());
        logger.log(Level.INFO, "objectives: {0}", getNumObjectives());
        logger.log(Level.INFO, "generations: {0}", getGenerations());
        logger.log(Level.INFO, "population: {0}", getPopulationSize());
        logger.log(Level.INFO, "requested solutions: {0}", getNumSolutions());
        for (int i = 0; i < getNumObjectives(); i++) {
            logger.log(Level.INFO, "objective {0}: {1}", new Object[]{i, getObjective(i).getName()});
        }
        MoSolution.setSolutionsCount(0);

        double crossoverDistributionIndex = problem.getNumberOfVariables();
        crossover = new IntegerSBXCrossover(getCrossoverProbability(), crossoverDistributionIndex);

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = problem.getNumberOfVariables();
        mutation = new IntegerPolynomialMutation(mutationProbability, mutationDistributionIndex);

        selection = new NaryTournamentSelection(numSolutions, new DominanceComparator());
        System.out.println("mutation: " + mutationProbability);
        System.out.println("crossover: " + getCrossoverProbability());
        moAlg = new NSGAIIBuilder(problem)
                .setCrossoverOperator(crossover)
                .setMutationOperator(mutation)
                .setSelectionOperator(selection)
                .setMaxIterations(this.getGenerations())
                .setPopulationSize(this.getPopulationSize())
                //.setSolutionListEvaluator(new MultithreadedSolutionListEvaluator(8, problem))
                .build();

        fireEvolutionStarted(this);
        logger.info("starting evolution");
        //AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(moAlg).execute();
        try {
            moAlg.run();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "failed clustering with {0} & {1}", new Object[]{getObjective(0).getName(), getObjective(1).getName()});
            Exceptions.printStackTrace(e);
        }
        List<Solution> moPop = ((NSGAII) moAlg).getResult();
        logger.log(Level.INFO, "result size: {0}", moPop.size());
        fireFinalResult(moPop);
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
            System.out.println("prop: " + ((MoSolution) s).getProps().toString());
            i++;
        }
        //long computingTime = algorithmRunner.getComputingTime();
        //System.out.println("computing time: " + computingTime);
        logger.log(Level.INFO, "explored solutions: {0}", MoSolution.getSolutionsCount());
        /*
         int numberOfDimensions = getNumObjectives();
         Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
         Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

         Hypervolume hypervolume = new Hypervolume();
         hypervolume.execute(frontA, frontB);*/
        /*
         Individual[] pop = new Individual[moPop.size()];
         for (int j = 0; j < moPop.size(); j++) {
         MoSolution b = (MoSolution) moPop.get(j);
         pop[j] = b.getIndividual();
         }

         fireResultUpdate(pop);*/
    }

    @Override
    public void clearObjectives() {
        if (objectives != null && !objectives.isEmpty()) {
            objectives.clear();
        }
    }

    public void addMOEvolutionListener(OpListener listener) {
        moListeners.add(listener);
    }

    protected void fireEvolutionStarted(EvolutionMO evo) {
        if (moListeners != null) {
            for (OpListener listener : moListeners) {
                listener.started(evo);
            }
        }
    }

    /**
     * Fired when repetitive run of same datasets was finished
     */
    public void fireFinishedBatch() {
        if (moListeners != null) {
            for (OpListener listener : moListeners) {
                listener.finishedBatch();
            }
        }
    }

    protected void fireFinalResult(List<Solution> res) {
        SolTransformer trans = SolTransformer.getInstance();
        List<OpSolution> solutions = trans.transform(res, new LinkedList<OpSolution>());
        if (solutions != null && solutions.size() > 0) {
            if (moListeners != null) {
                for (OpListener listener : moListeners) {
                    listener.finalResult(solutions);
                }
            }
        } else {
            throw new RuntimeException("transforming solutions failed");
        }
    }

    @Override
    public int getNumSolutions() {
        return numSolutions;
    }

    /**
     * Number of solutions to be returned from evolution
     *
     * @param numSolutions should be lower than population size
     */
    @Override
    public void setNumSolutions(int numSolutions) {
        this.numSolutions = numSolutions;
    }

    public boolean iskLimited() {
        return kLimit;
    }

    public void setkLimit(boolean kLimit) {
        this.kLimit = kLimit;
    }

}
