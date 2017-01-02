/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.evolution.eval;

/**
 *
 * @author deric
 */
public class EvalGenom {

    private EvalExpr[] expr;

    public EvalGenom(int size) {
        expr = new EvalExpr[size];
    }

    public double evaluate() {
        double value = 1.0;
        for (EvalExpr ex : expr) {
            value *= ex.value();
        }
        return value;
    }

}
