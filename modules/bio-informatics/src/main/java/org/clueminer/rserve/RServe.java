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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.clueminer.r.api.RException;
import org.clueminer.r.api.RExpr;
import org.clueminer.r.api.RLibraryNotLoadedException;
import org.clueminer.r.api.ROperationNotSupported;
import org.clueminer.utils.Props;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.clueminer.r.api.RBackend;

/**
 * This class is used throughout the framework to provide access to the R
 * framework.
 *
 * <p>
 * This class is a wrapper class for {@link RConnection} which adds convenience
 * functions.
 *
 * @author deric
 *
 */
public class RServe implements RBackend {

    protected RConnection connection;

    protected int pid;

    protected boolean interrupted;

    protected Logger log;

    protected Set<String> loadedLibraries;

    public RServe(Props conf) throws RException, ROperationNotSupported {
        super();
        String host = conf.get("host", "localhost");
        int port = conf.getInt("port", 6311);
        try {
            this.connection = new RConnection(host, port);
            try {
                this.pid = this.connection.eval("Sys.getpid()").asInteger();
            } catch (REXPMismatchException e) {
                throw new ROperationNotSupported(e.getMessage(), e);
            }
            // set buffer size to 100MB
            // this.connection.setSendBufferSize(1024l * 1024 * 1024 * 100);
            this.log = LoggerFactory.getLogger(this.getClass());
            this.loadedLibraries = new HashSet<>();
        } catch (RserveException ex) {
            System.err.println("FATAL ERROR: failed to connect to RServe on " + host + ":" + port);
            System.exit(0);
            //throw new RException(this, string);
        }
    }

    /**
     * This method tries to load the library with the given name.
     *
     * <p>
     * If the library could not be loaded, this method throws a
     * {@link RLibraryNotLoadedException}.
     *
     * @param name            The name of the library.
     * @param requiredByClass The name of the class that requires the library.
     * @return True, if the library was loaded successfully or was loaded
     *         before.
     * @throws RLibraryNotLoadedException
     * @throws InterruptedException
     */
    @Override
    public boolean loadLibrary(final String name, final String requiredByClass)
            throws RLibraryNotLoadedException, InterruptedException {
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

    /**
     * This method clears all variables stored in the session corresponding to
     * this rengine.
     *
     * @throws InterruptedException
     */
    public void clear() throws InterruptedException, RException {
        if (interrupted) {
            throw new InterruptedException();
        }
        this.eval("rm(list=ls(all=TRUE))");
    }

    /**
     * This method allows to assign a two-dimensional double array.
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
        try {
            this.eval(arg0 + " <- c()");
            this.connection.assign(arg0, oneDim);
            this.eval(arg0 + " <- matrix(" + arg0 + ",nrow=" + x + ",ncol=" + y + ",byrow=T)");
        } catch (REngineException e) {
            throw new RException(this, e.getMessage(), e);
        }
    }

    /**
     * This method allows to assign a two-dimensional integer array.
     *
     * @param arg0 The variable name in R.
     * @param arg1 A two-dimensional integer array which is assigned to the new
     *             variable.
     * @throws RException
     * @throws InterruptedException
     */
    @Override
    public void assign(String arg0, int[][] arg1) throws RException, InterruptedException {
        try {
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
            this.connection.assign(arg0, oneDim);
            this.eval(arg0 + " <- matrix(" + arg0 + ",nrow=" + x + ",ncol=" + y
                    + ",byrow=T)");
        } catch (RserveException ex) {
            log.error(ex.getMessage(), ex);
        } catch (REngineException ex) {
            log.error(ex.getMessage(), ex);
            throw new RException(this, ex);
        }
    }

    @Override
    public RExpr eval(String cmd) throws RException, InterruptedException {
        if (interrupted) {
            throw new InterruptedException();
        }
        try {
            this.connection.assign(".tmp.", cmd);
            REXP r = this.connection.eval("try(eval(parse(text=.tmp.)),silent=TRUE)");
            RExpr res = new RosExpr(r, this);
            return res;
        } catch (REngineException e) {
            throw new RException(this, e.getMessage());
        } catch (NullPointerException e) {
            System.out.format("%s - %s%n", Thread.currentThread(), this);
            throw e;
        }
    }

    /**
     * TODO: use this instead of printStackTrace() This method logs the last
     * error.
     *
     * @throws InterruptedException
     */
    @Override
    public void printLastError() throws InterruptedException {
        if (this.interrupted) {
            throw new InterruptedException();
        }
        log.error("R error: " + this.connection.getLastError());
    }

    @Override
    public void assign(String arg0, int[] arg1) throws RException, InterruptedException {
        if (interrupted) {
            throw new InterruptedException();
        }

        try {
            this.connection.assign(arg0, arg1);
        } catch (REngineException ex) {
            log.error(ex.getMessage(), ex);
            throw new RException(this, ex);
        }
    }

    @Override
    public void assign(String arg0, double[] arg1) throws RException, InterruptedException {
        if (interrupted) {
            throw new InterruptedException();
        }
        try {
            this.connection.assign(arg0, arg1);
        } catch (REngineException ex) {
            log.error(ex.getMessage(), ex);
            throw new RException(this, ex);
        }
    }

    @Override
    public String getLastError() throws InterruptedException {
        if (this.interrupted) {
            throw new InterruptedException();
        }
        return this.connection.getLastError();
    }

    /**
     * TODO: Put javadoc of {@link RConnection#close()}
     *
     * @return
     */
    protected boolean close() {
        return this.connection.close();
    }

    @Override
    public boolean interrupt() {
        try {
            interrupted = true;
            this.connection.close();
            Runtime.getRuntime().exec(("kill -9 " + this.pid).split(" "));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean shutdown() {
        try {
            this.connection.shutdown();
            return true;
        } catch (RserveException e) {
            return false;
        }
    }

    public void assign(String arg0, String[] arg1) throws RException, InterruptedException {
        if (interrupted) {
            throw new InterruptedException();
        }
        try {
            this.connection.assign(arg0, arg1);
        } catch (REngineException ex) {
            log.error(ex.getMessage(), ex);
            throw new RException(this, ex);
        }
    }
}
