package org.clueminer.evolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Tomas Barton
 */
public class Population {

    private Evolution evolution;
    protected Individual[] individuals = null;
    protected double avgFitness = 0;
    private double bestFitness = 0;

    public Population(Evolution evolve, int size) {
        this.evolution = evolve;
        individuals = new Individual[size];
        for (int i = 0; i < individuals.length; i++) {
            individuals[i] = new Individual(evolution) {
            };
            individuals[i].countFitness();
        }
        getAvgFitness();
    }

    /**
     * Method counts fitness of each individual and averages it.
     *
     * @return average fitness value
     */
    public final double getAvgFitness() {
        avgFitness = 0;
        for (int i = 0; i < individuals.length; i++) {
            individuals[i].getFitness();
            avgFitness += individuals[i].getFitness();
            bestFitness = Math.max(bestFitness, individuals[i].getFitness());
        }
        avgFitness = avgFitness / individuals.length;
        return avgFitness;
    }

    /**
     * Returns best individual from population
     *
     * @return best individual
     */
    public Individual getBestIndividual() {
        Individual best = this.individuals[0];
        for (int i = 0; i < this.individuals.length; i++) {
            if (this.individuals[i].getFitness() > best.getFitness()) {
                best = this.individuals[i];
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

    public Individual[] getIndividuals() {
        return individuals;
    }

    public int individualsLength() {
        return individuals.length;
    }

    public Individual getIndividual(int idx) {
        return this.individuals[idx];
    }

    public void setIndividuals(int index, Individual individual) {
        individuals[index] = individual;
    }

    public double getBestFitness() {
        return bestFitness;
    }

    public void setBestFitness(double bestFitness) {
        this.bestFitness = bestFitness;
    }

    /**
     * Method to select individuals from population
     *
     * @param count how many individuals would be selected
     * @return List<Individual> of selected individuals
     */
    public List<Individual> selectIndividuals(int count) {
        ArrayList<Individual> selected = new ArrayList<Individual>();
        //tournament selection
        int rand_cnt = getIndividuals().length / 10;
        int rand[] = new int[rand_cnt];
        for (int i = 0; i < count; i++) {
            double max = 0;
            int max_id = 0;
            for (int j = 0; j < rand_cnt; j++) {
                rand[j] = randomInt(getIndividuals().length - 1);
            }
            for (int j = 0; j < rand_cnt; j++) {
                if (this.getIndividual(rand[j]).getFitness() > max) {
                    max = this.getIndividual(rand[j]).getFitness();
                    max_id = j;
                }
            }
            if (!selected.contains(this.getIndividual(rand[max_id]))) {
                selected.add(this.getIndividual(rand[max_id]));
            } else {
                i--;
            }
        }
        return selected;
    }

    /**
     *
     * @param max
     * @return random number from 0 to max
     */
    public int randomInt(int max) {
        return (int) (Math.random() * (max + 1));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== POPULATION ===\n");
        for (int i = 0; i < individuals.length; i++) {
            sb.append(individuals[i].toString());
            sb.append("\n");
        }
        sb.append("=== avgFIT: ").append(avgFitness).append(" ===\n");
        return sb.toString();
    }
}
