/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.evolution;

import java.lang.reflect.Array;
import org.clueminer.evolution.api.AbstractIndividual;
import org.clueminer.evolution.api.AbstractPopulation;
import org.clueminer.evolution.api.Population;

/**
 * Simple population implementation.
 *
 * @author deric
 * @param <I>
 */
public class BasePopulation<I extends AbstractIndividual> extends AbstractPopulation<I> implements Population<I> {

    public BasePopulation(int size) {
        individuals = (I[]) Array.newInstance(AbstractIndividual.class, size);
    }

    public BasePopulation(I[] individuals) {
        this.individuals = individuals;
    }

}
