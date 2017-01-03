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
package org.clueminer.jri;

import org.clueminer.r.api.RBackend;
import org.clueminer.r.api.RException;
import org.clueminer.r.api.RExpr;
import org.rosuda.JRI.REXP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class JriExpr implements RExpr {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final REXP r;

    public JriExpr(REXP r, RBackend engine) throws RException {
        this.r = r;

        if (r == null) {
            throw new RException(engine, "Evaluation error");
        }
    }

    @Override
    public double asDouble() {
        return r.asDouble();
    }

    @Override
    public double[][] asDoubleMatrix() {
        return r.asDoubleMatrix();
    }

    @Override
    public int asInteger() {
        return r.asInt();
    }

    @Override
    public int[] asIntegers() {
        return r.asIntArray();
    }

    @Override
    public String[] asStrings() {
        return r.asStringArray();
    }

    @Override
    public double[] asDoubles() {
        return r.asDoubleArray();
    }
}
