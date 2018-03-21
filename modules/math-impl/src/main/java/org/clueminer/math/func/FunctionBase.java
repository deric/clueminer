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
package org.clueminer.math.func;

import org.clueminer.math.Function;
import org.clueminer.math.impl.DenseVector;

/**
 *
 * @author deric
 */
/**
 * Simple base abstract class for implementing a {@link Function} by
 * implementing {@link #f(double[]) } to call the vector version.
 *
 * @author Edward Raff
 */
public abstract class FunctionBase implements Function {

    private static final long serialVersionUID = 424945317407895409L;

    @Override
    public double f(double... x) {
        return f(DenseVector.toDenseVec(x));
    }

}
