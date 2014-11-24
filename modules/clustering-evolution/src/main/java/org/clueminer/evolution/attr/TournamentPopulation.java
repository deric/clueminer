package org.clueminer.evolution.attr;

import java.lang.reflect.Array;
import org.clueminer.clustering.api.evolution.Individual;
import java.util.ArrayList;
import java.util.List;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.evolution.AbstractIndividual;
import org.clueminer.evolution.AbstractPopulation;

/**
 *
 * @author Tomas Barton
 * @param <T>
 */
public class TournamentPopulation<T extends AbstractIndividual> extends AbstractPopulation<T> {

    private final Evolution<T> evolution;

    public TournamentPopulation(Evolution evolve, int size, Class<?> klass) {
        this.evolution = evolve;
        individuals = (T[]) Array.newInstance(klass, size);
        for (int i = 0; i < individuals.length; i++) {
            individuals[i] = evolution.createIndividual();
            individuals[i].countFitness();
        }
        getAvgFitness();
    }

    /**
     * Method to select individuals from population
     *
     * @param count how many individuals would be selected
     * @return List<> of selected individuals
     */
    public List<? extends Individual> selectIndividuals(int count) {
        ArrayList<T> selected = new ArrayList<>(count);
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
                if (evolution.getEvaluator().isBetter(getIndividual(rand[j]).getFitness(), max)) {
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
        for (T individual : individuals) {
            sb.append(individual.toString());
            sb.append("\n");
        }
        sb.append("=== avgFIT: ").append(avgFitness).append(" ===\n");
        return sb.toString();
    }

}
