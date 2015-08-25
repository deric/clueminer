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

import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.Executor;
import org.clueminer.evolution.mo.MoEvolution;
import org.clueminer.evolution.mo.MoSolution;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.selection.NaryTournamentSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;

/**
 *
 * @author deric
 */
public class KmEvolution extends MoEvolution {

    private static final Logger logger = Logger.getLogger(KmEvolution.class.getName());
    private int numSolutions = 10;
    private List<Solution> moPop;

    public KmEvolution(Executor exec) {
        super(exec);
    }

    @Override
    public void run() {
        setAlgorithm(new KMeans());
        HashSet<String> skipParams = new HashSet<>();
        skipParams.add(AgglParams.LOG);
        skipParams.add(AgglParams.STD);
        if (defaultProp != null && defaultProp.getBoolean(KMeansBagging.FIXED_K, false)) {
            skipParams.add("k");
        }
        KmProblem problem = new KmProblem(this, getAlgorithm(), skipParams, defaultProp);

        Algorithm moAlg;
        CrossoverOperator crossover;
        MutationOperator mutation;
        SelectionOperator selection;
        if (getNumObjectives() < 2) {
            throw new RuntimeException("provide at least 2 objectives. currently we have just " + getNumObjectives());
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

        double mutationProb = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = problem.getNumberOfVariables();
        mutation = new IntegerPolynomialMutation(mutationProb, mutationDistributionIndex);

        selection = new NaryTournamentSelection(numSolutions, new DominanceComparator());
        System.out.println("mutation: " + mutationProb);
        System.out.println("crossover: " + getCrossoverProbability());
        moAlg = new NSGAIIBuilder(problem, crossover, mutation)
                .setSelectionOperator(selection)
                .setMaxIterations(this.getGenerations())
                .setPopulationSize(this.getPopulationSize())
                //.setSolutionListEvaluator(new MultithreadedSolutionListEvaluator(8, problem))
                .build();

        fireEvolutionStarted(this);
        logger.info("starting evolution");
        //AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(moAlg).execute();
        //try {
        moAlg.run();
        /*  } catch (Exception e) {            logger.log(Level.SEVERE, "failed clustering with {0} & {1}", new Object[]{getObjective(0).getName(), getObjective(1).getName()});
         throw new RuntimeException("clustering failed", e);
         }*/
        moPop = ((NSGAII) moAlg).getResult();
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

    public List<Solution> getSolution() {
        return moPop;
    }

}
