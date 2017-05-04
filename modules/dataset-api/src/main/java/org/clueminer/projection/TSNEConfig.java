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
public interface TSNEConfig {

    double[][] getXin();

    void setXin(double[][] xin);

    int getOutputDims();

    void setOutputDims(int n);

    int getInitialDims();

    void setInitialDims(int initial_dims);

    double getPerplexity();

    void setPerplexity(double perplexity);

    int getMaxIter();

    void setMaxIter(int max_iter);

    boolean usePca();

    void setUsePca(boolean use_pca);

    double getTheta();

    void setTheta(double theta);

    boolean isSilent();

    void setSilent(boolean silent);

    boolean isPrintError();

    void setPrintError(boolean print_error);

    int getXStartDim();

    int getNrRows();
}
