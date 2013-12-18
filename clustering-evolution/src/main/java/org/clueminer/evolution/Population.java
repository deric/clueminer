package org.clueminer.evolution;

import org.clueminer.clustering.api.evolution.Individual;
import java.util.ArrayList;
import java.util.List;
import org.clueminer.clustering.api.evolution.Evolution;

/**
 *
 * @author Tomas Barton
 */
public class Population extends AbstractPopulation<WeightsIndividual> {

    private Evolution evolution;

    public Population(Evolution evolve, int size) {
        this.evolution = evolve;
        individuals = new WeightsIndividual[size];
        for (int i = 0; i < individuals.length; i++) {
            individuals[i] = new WeightsIndividual(evolution);
            individuals[i].countFitness();
        }
        getAvgFitness();
    }

    /**
     * Method to select individuals from population
     *
     * @param count how many individuals would be selected
     * @return List<Individual> of selected individuals
     */
    public List<? extends Individual> selectIndividuals(int count) {
        ArrayList<WeightsIndividual> selected = new ArrayList<WeightsIndividual>(count);
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
                if (evolution.getEvaluator().compareScore(getIndividual(rand[j]).getFitness(), max)) {
                    max = this.getIndividual(rand[j]).getFitness();
                    max_id = j;
                }
            }
            if (!selected.contains(this.getIndividual(rand[max_id]))) {
                selected.add(getIndividual(rand[max_id]));
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
