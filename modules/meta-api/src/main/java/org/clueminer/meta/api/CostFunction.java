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
package org.clueminer.meta.api;

import java.util.Map;

/**
 * Measure/estimates expenses of specific algorithm/procedure
 *
 * @author deric
 */
public interface CostFunction {

    /**
     * unique method identifier
     *
     * @return name of cost estimator
     */
    String getName();

    /**
     * Measure expenses (like computing time, memory used) of given method with certain parameters
     *
     * @param method
     * @param measure
     * @param value
     * @param parameters
     */
    void submit(String method, CostMeasure measure, double value, Map<String, Double> parameters);

    double estimate(String method, CostMeasure measure, Map<String, Double> parameters);

    void updateModel();

    int numObservation();

    /**
     *
     * @return true with mo
     */
    boolean isModelReady();
}
