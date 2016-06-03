/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.evolution.attr;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.api.AbstractIndividual;
import org.clueminer.evolution.api.AbstractPopulation;
import org.clueminer.evolution.api.EvolutionSO;
import org.clueminer.evolution.api.Population;

/**
 *
 * @author Tomas Barton
 * @param <I>
 * @param <E>
 * @param <C>
 */
public class TournamentPopulation<I extends AbstractIndividual<I, E, C>, E extends Instance, C extends Cluster<E>> extends AbstractPopulation<I> implements Population<I> {

    private final EvolutionSO<I, E, C> evolution;

    public TournamentPopulation(EvolutionSO<I, E, C> evolve, int size, Class<?> klass) {
        this.evolution = evolve;
        individuals = (I[]) Array.newInstance(klass, size);
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
    public List<I> selectIndividuals(int count) {
        ArrayList<I> selected = new ArrayList<>(count);
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
        for (I individual : individuals) {
            sb.append(individual.toString());
            sb.append("\n");
        }
        sb.append("=== avgFIT: ").append(avgFitness).append(" ===\n");
        return sb.toString();
    }

}
