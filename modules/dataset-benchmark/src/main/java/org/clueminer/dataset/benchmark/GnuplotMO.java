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
package org.clueminer.dataset.benchmark;

import java.util.List;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.oo.api.OpListener;
import org.clueminer.oo.api.OpSolution;

/**
 *
 * @author deric
 */
public class GnuplotMO extends GnuplotHelper implements OpListener {

    private Evolution evolution;

    @Override
    public void started(Evolution evolution) {
        this.evolution = evolution;
    }

    @Override
    public void finalResult(List<OpSolution> result) {
        for (OpSolution sol : result) {
            //TODO: implement
        }
    }

}
