/*
 * Copyright (C) 2015 clueminer.org
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
package org.clueminer.evolution.mo;

import org.clueminer.oo.api.OpSolution;
import org.clueminer.utils.CollectionTransformer;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author deric
 */
public class SolTransformer extends CollectionTransformer<Solution, OpSolution> {

    private static SolTransformer instance;

    private SolTransformer() {

    }

    public static SolTransformer getInstance() {
        if (instance == null) {
            instance = new SolTransformer();
        }
        return instance;
    }

    @Override
    public OpSolution transform(Solution e) {
        return (OpSolution) e;
    }

}
