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
package org.clueminer.evolution.mo;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.reflect.InvocationTargetException;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.clueminer.utils.ServiceFactory;
import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.problem.impl.AbstractGenericProblem;
import org.uma.jmetal.solution.IntegerSolution;

/**
 *
 * @author deric
 */
public abstract class BaseIntProblem extends AbstractGenericProblem<IntegerSolution> implements IntegerProblem {

    private static final long serialVersionUID = 7426909200734863348L;

    protected Int2ObjectOpenHashMap<String> mapping;
    protected int[] lowerLimit;
    protected int[] upperLimit;
    protected Parameter[] params;
    protected Executor exec;
    protected Props defaultProp;

    @Override
    public Integer getUpperBound(int index) {
        return upperLimit[index];
    }

    @Override
    public Integer getLowerBound(int index) {
        return lowerLimit[index];
    }

    /**
     * Variable mapped to given index
     *
     * @param index
     * @return
     */
    public String getVar(int index) {
        return mapping.get(index);
    }

    /**
     * Get instance of service factory if available for given parameter
     *
     * @param param
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static ServiceFactory getFactory(Parameter param) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return param.getFactory();
    }

    public abstract ClusteringAlgorithm getAlgorithm();

    public abstract ClusterEvaluation getObjective(int idx);

    public abstract Dataset<? extends Instance> getDataset();

    public abstract ClusterEvaluation getExternal();

    public abstract boolean iskLimited();

    public void setDefaultProps(Props prop) {
        this.defaultProp = prop;
    }

    public Props getDefaultProps() {
        if (defaultProp == null) {
            return new Props();
        }
        return this.defaultProp.copy();
    }

}
