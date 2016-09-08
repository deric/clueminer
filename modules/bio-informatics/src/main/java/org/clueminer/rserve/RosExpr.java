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
package org.clueminer.rserve;

import org.clueminer.r.api.IRengine;
import org.clueminer.r.api.RException;
import org.clueminer.r.api.RExpr;
import org.clueminer.r.api.ROperationNotSupported;
import org.openide.util.Exceptions;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper around Rosuda expression
 *
 * @author deric
 */
public class RosExpr implements RExpr {

    private Logger log = LoggerFactory.getLogger(getClass());
    private REXP r;

    public RosExpr(REXP r, IRengine engine) throws RException {
        this.r = r;

        if (r == null) {
            throw new RException(engine, "Evaluation error");
        } else if (r.inherits("try-error")) {
            try {
                throw new RException(engine, r.asString().replace("\n", " - "));
            } catch (REXPMismatchException e) {
                throw new ROperationNotSupported(engine, "Evaluation error");
            }
        }
    }

    @Override
    public double asDouble() {
        try {
            return r.asDouble();
        } catch (REXPMismatchException ex) {
            log.error(ex.getMessage(), ex);
        }
        return Double.NaN;
    }

    @Override
    public double[][] asDoubleMatrix() {
        try {
            return r.asDoubleMatrix();
        } catch (REXPMismatchException ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public int asInteger() {
        try {
            return r.asInteger();
        } catch (REXPMismatchException ex) {
            log.error(ex.getMessage(), ex);
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public int[] asIntegers() {
        try {
            return r.asIntegers();
        } catch (REXPMismatchException ex) {
            log.error(ex.getMessage(), ex);
            Exceptions.printStackTrace(ex);
        }
        return new int[0];
    }

    @Override
    public String[] asStrings() {
        try {
            return r.asStrings();
        } catch (REXPMismatchException ex) {
            Exceptions.printStackTrace(ex);
        }
        return new String[0];
    }

    @Override
    public double[] asDoubles() {
        try {
            return r.asDoubles();
        } catch (REXPMismatchException ex) {
            Exceptions.printStackTrace(ex);
        }
        return new double[0];
    }
}
