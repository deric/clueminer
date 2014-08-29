package org.clueminer.evolution;

import java.util.Arrays;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public class AbstractPopulation<E extends AbstractIndividual> {

    protected E[] individuals = null;
    protected double avgFitness = 0;
    protected double bestFitness = 0;

    /**
     * Method counts fitness of each individual and averages it.
     *
     * @return average fitness value
     */
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
    public void sortByFitness() {
        Arrays.sort(individuals);
    }

    public E[] getIndividuals() {
        return individuals;
    }

    public int individualsLength() {
        return individuals.length;
    }

    public E getIndividual(int idx) {
        return this.individuals[idx];
    }

    public void setIndividuals(int index, E individual) {
        individuals[index] = individual;
    }

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
}
