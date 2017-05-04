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
package org.clueminer.projection;

/**
 *
 * @author deric
 */
public class TSNEConfigImpl implements TSNEConfig {

    protected double[][] xin;
    protected int outputDims;
    protected int initialDims;
    protected double perplexity;
    protected int maxIter;
    protected boolean usePca;
    protected double theta;
    protected boolean silent;
    protected boolean printError;

    public TSNEConfigImpl(double[][] xin, int outputDims, int initialDims, double perplexity, int maxIter,
            boolean usePca, double theta, boolean silent, boolean printError) {
        this.xin = xin;
        this.outputDims = outputDims;
        this.initialDims = initialDims;
        this.perplexity = perplexity;
        this.maxIter = maxIter;
        this.usePca = usePca;
        this.theta = theta;
        this.silent = silent;
        this.printError = printError;
    }

    public TSNEConfigImpl(double[][] xin, int outputDims, int initialDims, double perplexity, int maxIter) {
        this(xin, outputDims, initialDims, perplexity, maxIter, true, 0.5, false, true);
    }

    @Override
    public double[][] getXin() {
        return xin;
    }

    @Override
    public void setXin(double[][] xin) {
        this.xin = xin;
    }

    @Override
    public int getOutputDims() {
        return outputDims;
    }

    @Override
    public void setOutputDims(int outputDims) {
        this.outputDims = outputDims;
    }

    @Override
    public int getInitialDims() {
        return initialDims;
    }

    @Override
    public void setInitialDims(int initialDims) {
        this.initialDims = initialDims;
    }

    @Override
    public double getPerplexity() {
        return perplexity;
    }

    @Override
    public void setPerplexity(double perplexity) {
        this.perplexity = perplexity;
    }

    @Override
    public int getMaxIter() {
        return maxIter;
    }

    @Override
    public void setMaxIter(int maxIter) {
        this.maxIter = maxIter;
    }

    @Override
    public boolean usePca() {
        return usePca;
    }

    @Override
    public void setUsePca(boolean usePca) {
        this.usePca = usePca;
    }

    @Override
    public double getTheta() {
        return theta;
    }

    @Override
    public void setTheta(double theta) {
        this.theta = theta;
    }

    @Override
    public boolean isSilent() {
        return silent;
    }

    @Override
    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    @Override
    public boolean isPrintError() {
        return printError;
    }

    @Override
    public void setPrintError(boolean printError) {
        this.printError = printError;
    }

    @Override
    public int getXStartDim() {
        return xin[0].length;
    }

    @Override
    public int getNrRows() {
        return xin.length;
    }

}
