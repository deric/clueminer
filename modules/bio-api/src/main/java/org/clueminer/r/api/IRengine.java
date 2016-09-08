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
package org.clueminer.r.api;

/**
 * Provides access to execution of R code
 *
 * @author deric
 */
public interface IRengine {

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
    boolean loadLibrary(final String name, final String requiredByClass)
            throws RLibraryNotLoadedException, InterruptedException;

    /**
     * This method clears all variables stored in the session corresponding to
     * this Rengine.
     *
     * @throws de.clusteval.api.r.RException
     * @throws InterruptedException
     */
    void clear() throws RException, InterruptedException;

    /**
     * This method allows to assign a two-dimensional double array.
     *
     * @param arg0 The variable name in R.
     * @param arg1 A two-dimensional double array which is assigned to the new
     *             variable.
     * @throws de.clusteval.api.r.RException
     * @throws InterruptedException
     */
    void assign(String arg0, double[][] arg1) throws RException, InterruptedException;

    /**
     * This method allows to assign a two-dimensional integer array.
     *
     * @param arg0 The variable name in R.
     * @param arg1 A two-dimensional integer array which is assigned to the new
     *             variable.
     * @throws de.clusteval.api.r.RException
     * @throws InterruptedException
     */
    void assign(String arg0, int[][] arg1) throws RException, InterruptedException;

    void assign(String arg0, int[] arg1) throws RException, InterruptedException;

    void assign(String arg0, double[] arg1) throws RException, InterruptedException;

    void assign(String arg0, String[] arg1) throws RException, InterruptedException;

    RExpr eval(String cmd) throws RException, InterruptedException;

    /**
     * TODO: use this instead of printStackTrace() This method logs the last
     * error.
     *
     * @throws InterruptedException
     */
    void printLastError() throws InterruptedException;

    boolean interrupt();

    String getLastError() throws InterruptedException;

    /**
     * Shutdown backend engine
     *
     * @return
     */
    boolean shutdown();

}
