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
package org.clueminer.evolution.api;

import java.util.EventListener;

/**
 *
 * @author Tomas Barton
 */
public interface EvolutionListener extends EventListener {

    /**
     * Called when evolution starts
     *
     * @param evolution
     */
    void started(Evolution evolution);

    /**
     * Called after each iteration of evolution
     *
     * @param result
     */
    void resultUpdate(Individual[] result);

    /**
     * Best individual found in a generation
     *
     * @param generationNum
     * @param external
     * @param population
     */
    void bestInGeneration(int generationNum, Population<? extends Individual> population, double external);

    /**
     * Final evolution result
     *
     * @param evolution
     * @param g
     * @param best
     * @param time
     * @param bestFitness
     * @param avgFitness
     * @param external
     */
    void finalResult(Evolution evolution, int g, Individual best,
            Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external);
}
