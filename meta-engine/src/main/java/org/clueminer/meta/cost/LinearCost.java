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
package org.clueminer.meta.cost;

import java.util.Map;
import org.clueminer.meta.api.CostFunction;
import org.clueminer.meta.api.CostMeasure;
import org.clueminer.meta.api.MetaStorage;
import org.clueminer.meta.api.MetaStorageFactory;
import org.clueminer.project.api.ProjectController;
import org.openide.util.Lookup;
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

    private MetaStorage fetchStorage() {
        MetaStorage storage = null;
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        if (pc != null) {
            storage = pc.getCurrentProject().getLookup().lookup(MetaStorage.class);
        } else {
            LOG.error("missing project controller");
        }
        if (storage == null) {
            LOG.info("meta storage not set, using default one");
            storage = MetaStorageFactory.getInstance().getDefault();
        }
        return storage;
    }

    @Override
    public void submit(String method, CostMeasure measure, double value, Map<String, Double> parameters) {
        MetaStorage storage = fetchStorage();
        storage.insertCost(method, measure, value, parameters);
    }

    @Override
    public double estimate(String method, CostMeasure measure, Map<String, Double> parameters) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
