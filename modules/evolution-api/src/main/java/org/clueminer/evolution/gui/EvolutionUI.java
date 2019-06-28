/*
 * Copyright (C) 2011-2019 clueminer.org
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
package org.clueminer.evolution.gui;

import org.clueminer.evolution.api.Evolution;

/**
 *
 * @author Tomas Barton
 */
public interface EvolutionUI {

    /**
     * Identifier of this UI
     *
     * @return
     */
    String getName();

    /**
     * Update algorithm settings
     *
     * @param alg
     */
    void updateAlgorithm(Evolution alg);

    /**
     * number of generations
     *
     * @return
     */
    int getGenerations();

    /**
     * Population size
     *
     * @return
     */
    int getPopulation();

    /**
     * Test whether given algorithm is supported by the UI
     *
     * @param evolve
     * @return
     */
    boolean isUIfor(Evolution evolve);
}
