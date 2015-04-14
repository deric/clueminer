package org.clueminer.evolution.api;

import java.util.Arrays;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public abstract class AbstractPopulation<E extends AbstractIndividual> implements Population<E> {

    protected E[] individuals = null;
    protected double avgFitness = 0;
    protected double bestFitness = 0;

    /**
     * Method counts fitness of each individual and averages it.
     *
     * @return average fitness value
     */
    @Override
    public double getAvgFitness() {
        avgFitness = 0;
        for (E individual : individuals) {
            individual.getFitness();
            avgFitness += individual.getFitness();
            bestFitness = Math.max(bestFitness, individual.getFitness());
        }
        avgFitness /= individuals.length;
        return avgFitness;
    }

    /**
     * Returns best individual from population
     *
     * @return best individual
     */
    @Override
    public E getBestIndividual() {
        E best = this.individuals[0];
        for (E individual : this.individuals) {
            if (individual.getFitness() > best.getFitness()) {
                best = individual;
            }
        }
        return best;
    }

    /**
     * This methods needs that Individual implements interface Comparable.
     */
    @Override
    public void sortByFitness() {
        Arrays.sort(individuals);
    }

    @Override
    public E[] getIndividuals() {
        return individuals;
    }

    @Override
    public int size() {
        return individuals.length;
    }

    @Override
    public E getIndividual(int idx) {
        return this.individuals[idx];
    }

    @Override
    public void setIndividuals(int index, E individual) {
        individuals[index] = individual;
    }

    /**
     *
     * @return
     */
    @Override
    public double getBestFitness() {
        return bestFitness;
    }

    public void setBestFitness(double bestFitness) {
        this.bestFitness = bestFitness;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== POPULATION ===\n");
        for (E individual : individuals) {
            sb.append(individual.toString());
            sb.append("\n");
        }
        sb.append("=== avgFIT: ").append(avgFitness).append(" ===\n");
        return sb.toString();
    }

    @Override
    public void setIndividuals(E[] individuals) {
        this.individuals = individuals;
    }
}
