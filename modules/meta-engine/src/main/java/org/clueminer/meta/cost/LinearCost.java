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
package org.clueminer.meta.cost;

import java.util.Collection;
import java.util.Map;
import org.clueminer.meta.api.CostFunction;
import org.clueminer.meta.api.CostMeasure;
import org.clueminer.meta.api.CostMeasurement;
import org.clueminer.meta.api.MetaStorage;
import org.clueminer.meta.engine.MetaStore;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple model for estimating algorithm complexity/cost.
 *
 * @author deric
 */
@ServiceProvider(service = CostFunction.class)
public class LinearCost implements CostFunction {

    public String NAME = "Linear cost model";
    private static final Logger LOG = LoggerFactory.getLogger(LinearCost.class);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void submit(String method, CostMeasure measure, double value, Map<String, Double> parameters) {
        MetaStorage storage = MetaStore.fetchStorage();
        storage.insertCost(method, measure, value, parameters);
    }

    @Override
    public double estimate(String method, CostMeasure measure, Map<String, Double> parameters) {
        MetaStorage storage = MetaStore.fetchStorage();
        Collection<CostMeasurement> costs = storage.findAllCostMeasurements(method, measure);
        LOG.debug("fetched {} cost records", costs.size());
        //TODO: build model,
        return -1;
    }

    @Override
    public void updateModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int numObservation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isModelReady(String method, CostMeasure measure) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
