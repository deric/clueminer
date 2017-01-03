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

import java.util.HashSet;
import java.util.Set;
import org.clueminer.r.api.RBackend;
import org.clueminer.r.api.RException;
import org.clueminer.r.api.RExpr;
import org.clueminer.r.api.RLibraryNotLoadedException;
import org.rosuda.JRI.Rengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Execute R code via native JRI binding
 *
 * @author deric
 */
public class JRI implements RBackend {

    private Rengine engine;
    protected Set<String> loadedLibraries;
    protected boolean interrupted;
    protected Logger log;

    public JRI() {
        engine = new Rengine(new String[]{"--no-save"}, false, null);
        interrupted = false;
        this.log = LoggerFactory.getLogger(this.getClass());
        this.loadedLibraries = new HashSet<>();
    }

    @Override
    public boolean loadLibrary(String name, String requiredByClass) throws RLibraryNotLoadedException, InterruptedException {
        if (this.interrupted) {
            throw new InterruptedException();
        }
        try {
            if (this.loadedLibraries.contains(name)) {
                return true;
            }
            this.log.debug("Loading R library '" + name + "' ...");
            this.eval("library(" + name + ")");
            this.loadedLibraries.add(name);
            this.log.debug("R library '" + name + "' loaded successfully");
            return true;
        } catch (RException e) {
            this.log.error("R library '" + name + "' loading failed");
            throw new RLibraryNotLoadedException(requiredByClass, name);
        }
    }

    @Override
    public void clear() throws RException, InterruptedException {
        if (interrupted) {
            throw new InterruptedException();
        }
        this.eval("rm(list=ls(all=TRUE))");
    }

    /**
     * Allows assigning a two-dimensional double array.
     *
     * @param arg0 The variable name in R.
     * @param arg1 A two-dimensional double array which is assigned to the new
     *             variable.
     *
     * @throws InterruptedException
     */
    @Override
    public void assign(String arg0, double[][] arg1) throws RException, InterruptedException {
        if (interrupted) {
            throw new InterruptedException();
        }
        int x = arg1.length;
        int y = x > 0 ? arg1[0].length : 0;
        double[] oneDim = new double[x * y];
        for (int i = 0; i < x; i++) {
            System.arraycopy(arg1[i], 0, oneDim, i * y, y);
        }
        this.eval(arg0 + " <- c()");
        this.engine.assign(arg0, oneDim);
        this.eval(arg0 + " <- matrix(" + arg0 + ",nrow=" + x + ",ncol=" + y + ",byrow=T)");
    }

    /**
     * Allows to assigning a two-dimensional integer array.
     *
     * @param arg0 The variable name in R.
     * @param arg1 A two-dimensional integer array which is assigned to the new
     *             variable.
     * @throws RException
     * @throws InterruptedException
     */
    @Override
    public void assign(String arg0, int[][] arg1) throws RException, InterruptedException {
        if (interrupted) {
            throw new InterruptedException();
        }
        int x = arg1.length;
        int y = x > 0 ? arg1[0].length : 0;
        int[] oneDim = new int[x * y];
        for (int i = 0; i < x; i++) {
            System.arraycopy(arg1[i], 0, oneDim, i * y, y);
        }
        this.eval(arg0 + " <- c()");
        this.engine.assign(arg0, oneDim);
        this.eval(arg0 + " <- matrix(" + arg0 + ",nrow=" + x + ",ncol=" + y + ",byrow=T)");
    }

    @Override
    public void assign(String arg0, int[] arg1) throws RException, InterruptedException {
        if (interrupted) {
            throw new InterruptedException();
        }

        this.engine.assign(arg0, arg1);
    }

    @Override
    public void assign(String arg0, double[] arg1) throws RException, InterruptedException {
        if (interrupted) {
            throw new InterruptedException();
        }
        this.engine.assign(arg0, arg1);
    }

    @Override
    public void assign(String arg0, String[] arg1) throws RException, InterruptedException {
        if (interrupted) {
            throw new InterruptedException();
        }
        this.engine.assign(arg0, arg1);
    }

    @Override
    public RExpr eval(String cmd) throws RException, InterruptedException {
        if (interrupted) {
            throw new InterruptedException();
        }
        try {
            this.engine.assign(".tmp.", cmd);
            org.rosuda.JRI.REXP r = this.engine.eval("try(eval(parse(text=.tmp.)),silent=TRUE)");
            RExpr res = new JriExpr(r, this);
            return res;
        } catch (NullPointerException e) {
            System.out.format("%s - %s%n", Thread.currentThread(), this);
            throw e;
        }
    }

    @Override
    public void printLastError() throws InterruptedException {
        if (this.interrupted) {
            throw new InterruptedException();
        }
        log.error("R error: unknow (currently not supported)");
    }

    @Override
    public boolean interrupt() {
        interrupted = true;
        engine.interrupt();
        return true;
    }

    @Override
    public String getLastError() throws InterruptedException {
        return "";
    }

    @Override
    public boolean shutdown() {
        //nothing to do
        return true;
    }

}
